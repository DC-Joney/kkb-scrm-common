package com.kkb.common.send.ding.fallback;


import com.kkb.common.send.ding.client.CorgiDingClient;
import com.kkb.common.send.ding.client.CorgiUserClient;
import com.kkb.common.send.ding.meta.DingTalkMsg;
import com.kkb.common.send.ding.result.SendDingTalkMsgResponse;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author duanxiangchao on 2021/11/11
 */
@Component
@Slf4j
public class CorgiDingFallback implements FallbackFactory<CorgiDingClient> {
    @Override
    public CorgiDingClient create(Throwable throwable) {
        return new CorgiDingClient() {

            @Override
            public SendDingTalkMsgResponse sendMsg(DingTalkMsg dingTalkMsg) {
                log.warn("feign error. method: CorgiUserClient#sendMsg({})", dingTalkMsg, throwable);
                return null;
            }



        };
    }
}
