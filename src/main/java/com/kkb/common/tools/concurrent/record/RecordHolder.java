package com.kkb.common.tools.concurrent.record;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * 任务记录器 Holder
 *
 * @author zhangyang
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecordHolder {

    private static final ThreadLocal<HttpServletRequest> requestHolder = new NamedThreadLocal<>("Servlet Request");
    private static final ThreadLocal<ConcurrentTaskRecord> recordHolder = new NamedThreadLocal<>("Task Record");
    private static final String RECORD_ATTRIBUTE_NAME = ConcurrentTaskRecord.class.getName();

    /**
     * 将record绑定到对应的request 请求上
     */
    protected static HttpServletRequest initRequest(HttpServletRequest request) {
        HttpServletRequest servletRequest = requestHolder.get();
        if (servletRequest == null)
            requestHolder.set(request);
        return request;
    }

    /**
     * 用于为 requestHolder 设置不同的 request，不建议同一个请求中 切换request
     *
     * @param request request 请求
     */
    public static void setRequest(HttpServletRequest request) {
        requestHolder.set(request);
    }

    public static HttpServletRequest getRequest() {
        return requestHolder.get();
    }

    /**
     * 同一个request中可以切换不用的 record 收集器
     *
     * @param taskRecord 任务收集器
     */
    public static void setTaskRecord(ConcurrentTaskRecord taskRecord) {
        HttpServletRequest request = getRequest();

        if (request == null) {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes instanceof NativeWebRequest) {
                NativeWebRequest webRequest = (NativeWebRequest) requestAttributes;
                request = webRequest.getNativeRequest(HttpServletRequest.class);
                if (request != null)
                    setRequest(request);
            }
        }

        if (request == null)
            throw new RequestNotFoundException("Require request object, but can not found");

        recordHolder.set(taskRecord);
        if (request.getAttribute(RECORD_ATTRIBUTE_NAME) == null)
            request.setAttribute(RECORD_ATTRIBUTE_NAME, taskRecord);

    }

    /**
     * 获取当前的taskRecord对象
     */
    public static ConcurrentTaskRecord getTaskRecord() {

        HttpServletRequest request = requestHolder.get();

        //如果 当前不存在request对象，则直接返回空
        if (request == null)
            return null;

        ConcurrentTaskRecord taskRecord = recordHolder.get();
        if (taskRecord == null) {
            taskRecord = (ConcurrentTaskRecord) request.getAttribute(RECORD_ATTRIBUTE_NAME);
        }

        if (taskRecord == null) {
            taskRecord = new ConcurrentTaskRecord(16);
            setTaskRecord(taskRecord);
        }

        return taskRecord;
    }


    /**
     * 一定要记得清除，否则会造成内存泄漏
     */
    public static ConcurrentTaskRecord clear(boolean clearAttributes) {
        ConcurrentTaskRecord taskRecord = getTaskRecord();

        recordHolder.remove();

        //判断是否需要从request的attributes中进行清除，如果不需要则直接清空当前的 holder即可
        if (clearAttributes){
            //将record记录器从当前request请求中移除
            if (getRequest() != null) {
                getRequest().removeAttribute(RECORD_ATTRIBUTE_NAME);
            }
        }

        requestHolder.remove();
        return taskRecord;
    }

    /**
     * 获取所有的任务信息
     */
    public static String getTaskInfo() {
        return Optional.ofNullable(getTaskRecord())
                .map(ConcurrentTaskRecord::pretty).orElse(null);
    }

    /**
     * 不建议调用 stop操作，会导致统计信息准确
     */
    public static void stopTaskRecord() {
        Optional.ofNullable(getTaskRecord())
                .ifPresent(ConcurrentTaskRecord::stop);
    }

    /**
     * 如果使用了阿里的 TTL 保证当前threadLocal 是共享的就可以直接使用该方法
     */
    public static ConcurrentTaskRecord.RecordTask newRecord(String taskName) {
        return Optional.ofNullable(getTaskRecord())
                .map(r -> r.newRecord(taskName))
                .orElse(null);
    }

    public static boolean isInit(){
        return getRequest() != null;
    }
}
