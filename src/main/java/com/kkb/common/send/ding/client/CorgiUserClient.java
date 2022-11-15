/*
 * description
 */
package com.kkb.common.send.ding.client;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kkb.common.core.KkbResponse;
import com.kkb.common.send.ding.dto.phone.UpmsUser;
import com.kkb.common.send.ding.dto.phone.UpmsUserQuery;
import com.kkb.common.send.ding.fallback.CorgiUserFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 类描述
 *
 * @author sy
 * @date 2021/9/4 3:38 下午
 * @since
 */
@FeignClient(value = "CorgiUserClient", url = "${kkb.mos.service.url.scrm-corgi}", fallbackFactory = CorgiUserFallback.class)
public interface CorgiUserClient {

    /**
     * 可支持分页，如果需要支持分页，自定义新的FeignClient
     * @param userQuery 查询条件
     */
    @PostMapping("/api/v1.0/corgi/user/queryUserByNameOrPhone")
    KkbResponse<Page<UpmsUser>> getUserInfo(@RequestBody UpmsUserQuery userQuery);


}
