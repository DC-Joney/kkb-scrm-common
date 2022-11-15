package com.kkb.common.send.ding.fallback;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.kkb.common.core.KkbResponse;
import com.kkb.common.send.ding.dto.phone.UpmsUser;
import com.kkb.common.send.ding.dto.phone.UpmsUserQuery;
import com.kkb.common.send.ding.result.SendDingTalkMsgResponse;
import com.kkb.common.send.ding.client.CorgiUserClient;
import com.kkb.common.send.ding.meta.DingTalkMsg;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author duanxiangchao on 2021/11/11
 */
@Component
@Slf4j
public class CorgiUserFallback implements FallbackFactory<CorgiUserClient> {
    @Override
    public CorgiUserClient create(Throwable throwable) {
        return new CorgiUserClient() {

            @Override
            public KkbResponse<Page<UpmsUser>> getUserInfo(UpmsUserQuery userQuery) {
                return KkbResponse.error();
            }
        };
    }
}
