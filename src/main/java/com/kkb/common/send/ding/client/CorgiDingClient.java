/*
 * description
 */
package com.kkb.common.send.ding.client;


import com.kkb.common.send.ding.dto.phone.UpmsUser;
import com.kkb.common.send.ding.dto.phone.UpmsUserQuery;
import com.kkb.common.send.ding.fallback.CorgiDingFallback;
import com.kkb.common.send.ding.fallback.CorgiUserFallback;
import com.kkb.common.send.ding.meta.DingTalkMsg;
import com.kkb.common.send.ding.result.SendDingTalkMsgResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 类描述
 *
 * @author sy
 * @date 2021/9/4 3:38 下午
 * @since
 */
@FeignClient(value = "CorgiDingClient", url = "${kkb.mos.service.url.scrm-corgi}", fallbackFactory = CorgiDingFallback.class)
public interface CorgiDingClient {

    @PostMapping("/corgi/organization/sendMsg")
    SendDingTalkMsgResponse sendMsg(@RequestBody DingTalkMsg dingTalkMsg);

}
