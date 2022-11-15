/*
 * Copyright
 */
package com.kkb.common.send.ding.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 类描述
 *
 * @author ztkool
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SendDingTalkMsgResponse extends CorgiResponse<SendDingTalkMsgResponse> implements Serializable {

    public static SendDingTalkMsgResponse ERROR = new SendDingTalkMsgResponse();

    /**
     * 任务id, 可用于撤回通知
     */
    private String taskId;

}
