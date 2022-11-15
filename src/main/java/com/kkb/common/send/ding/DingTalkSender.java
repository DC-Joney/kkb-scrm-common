/*
 * Copyright
 */
package com.kkb.common.send.ding;


import com.kkb.common.send.Sender;
import com.kkb.common.send.ding.client.CorgiDingClient;
import com.kkb.common.send.ding.client.CorgiUserClient;
import com.kkb.common.send.ding.meta.DingTalkMsg;
import com.kkb.common.send.ding.result.SendDingTalkMsgResponse;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * 类描述
 *
 * @author sy
 * @date 2021-12-02 12:17
 * @since
 */
@Slf4j
public class DingTalkSender implements Sender<DingTalkMsg, SendDingTalkMsgResponse> {

    @Resource
    private CorgiDingClient dingClient;

    @Override
    public SendDingTalkMsgResponse send(DingTalkMsg msg) {
        if (null == msg) {
            log.error("ding talk. error when send ding talk msg. msg is null.");
            return SendDingTalkMsgResponse.ERROR;
        }
        if (null == msg.getUseridList()) {
            log.error("ding talk. error when send ding talk msg. userIdList is null.");
            return SendDingTalkMsgResponse.ERROR;
        }

        SendDingTalkMsgResponse sendResponse = dingClient.sendMsg(msg);

        if (null == sendResponse || !sendResponse.isSuccess()) {
            sendResponse = SendDingTalkMsgResponse.ERROR;
            log.error("SendMsgService.sendMarkdownMsg 钉钉消息提醒调用失败 response : {} ", sendResponse);
        }
        return sendResponse;

    }
}
