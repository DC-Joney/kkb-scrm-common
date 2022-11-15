package com.kkb.common.send.ding;

import com.kkb.common.send.ding.client.CorgiDingClient;
import com.kkb.common.send.ding.client.CorgiUserClient;
import feign.Feign;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

/**
 * 钉钉发送消息
 *
 * @author zhangyang
 */
@ConditionalOnClass(Feign.class)
@EnableFeignClients(clients = {CorgiUserClient.class, CorgiDingClient.class})
public class CorgiClientConfig {


    @Bean
    public DingTalkSender dingTalkSender() {
        return new DingTalkSender();
    }

}
