package com.kkb.common.tools.concurrent.record;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * spring mvc 配置
 *
 * @author zhangyang
 */
public class RecordWebConfigurer implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RecordInterceptor());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new RecordHandlerMethodArgumentResolver());
    }


    @Slf4j
    @FieldDefaults(level = AccessLevel.PRIVATE)
    static class RecordInterceptor implements AsyncHandlerInterceptor {

        final UrlPathHelper urlPathHelper = new UrlPathHelper();

        @Override
        public boolean preHandle(@NonNull HttpServletRequest request,
                                 @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {


            //初始化当前的 holder对象
            if (!RecordHolder.isInit())
                RecordHolder.initRequest(request);

   /*         //判断当前线程是否是异步化处理返回结果
            WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);

            //如果asyncManager 不为空说明是一个异步request处理
            if (asyncManager != null){
                if (!asyncManager.isConcurrentHandlingStarted()){

                }
            }
            if (!RecordHolder.isInit())
                //绑定 request
                RecordHolder.initRequest(request);

            log.info("进来 preHandle");*/
            return true;
        }

        /**
         * 不管是异步还是非异步都可以在这里处理
         */
        @Override
        public void postHandle(@NonNull HttpServletRequest request,
                               @NonNull HttpServletResponse response, @NonNull Object handler, ModelAndView modelAndView) throws Exception {

            //停止当前记录器
            RecordHolder.stopTaskRecord();
        }

        /**
         * 这里实在实际的线程调用完成之后才会走这个逻辑
         */
        @Override
        public void afterCompletion(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Object handler, Exception ex) throws Exception {
            //如果当前的请求中带有Record对象那么从当前的线程栈中清除
            if (RecordHolder.isInit()){
                ConcurrentTaskRecord taskRecord = RecordHolder.clear(true);
                printTaskInfo(taskRecord,request);
            }
        }

        /**
         * 在 request.startAsync 之后spring拿到异步处理的占位符之后会进行调用该方法
         * 由于Spring 处理异步化是在不同的线程中处理，所以我们要从原有处理的线程中清理相关的ThreadLocal对象，防止内存泄漏
         */
        @Override
        public void afterConcurrentHandlingStarted(@NonNull HttpServletRequest request,
                                                   @NonNull HttpServletResponse response,
                                                   @NonNull Object handler) throws Exception {
            if (RecordHolder.isInit())
                //为了解决spring在异步处理中会反复请求当前的拦截器，所以在第一次并发处理启动时只清空holder中的值
                RecordHolder.clear(false);

        }


        private void printTaskInfo(ConcurrentTaskRecord taskRecord, HttpServletRequest request) {
            String servletPath = urlPathHelper.getServletPath(request);
            log.info("RequestPath: {}, {}", servletPath, taskRecord.pretty());
        }

    }

    static class RecordHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

        @Override
        public boolean supportsParameter(@NonNull MethodParameter parameter) {
            Record record = parameter.getParameterAnnotation(Record.class);
            return record != null && ConcurrentTaskRecord.class.isAssignableFrom(parameter.getParameterType());
        }

        @Override
        public Object resolveArgument(MethodParameter parameter,
                                      ModelAndViewContainer mavContainer,
                                      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
            //必须搭配HandlerInterceptor进行处理否则会出问题
            return RecordHolder.getTaskRecord();
        }
    }
}
