package com.kkb.common.send;


/**
 * 类描述
 *
 * @author ztkool
 * @since 1.0.0
 */
public interface Sender<T, R> {

    /**
     * 发送
     *
     * @param msg
     * @return
     */
    R send(T msg);

}
