package com.kkb.common.send.ding.client.config;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kkb.common.send.ding.dto.phone.UpmsUser;
import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ResolvableType;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.List;

@Slf4j
@Configuration
public class CorgiUserConfig {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Bean
    Decoder decoder(){
        return PageDecoder.create(new SpringDecoder(messageConverters));
    }

    @AllArgsConstructor(staticName = "create")
    private static class PageDecoder implements Decoder{

        private Decoder digest;

        @Override
        public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
            try {
                if (type instanceof Class || type instanceof ParameterizedType
                        || type instanceof WildcardType) {
                    ResolvableType resolvableType = ResolvableType.forClassWithGenerics(Page.class, ResolvableType.forClassWithGenerics(List.class, UpmsUser.class));
                    Class<?> typeClass = resolvableType.resolve();
                    Object decodeTarget = digest.decode(response, typeClass);
                    if (decodeTarget instanceof Page) {
                        Page<?> page = (Page<?>) decodeTarget;
                        return page.getRecords();
                    }
                }
            }catch (Exception e){
                log.error("Parse feign data error, fallback to try execute digest decode");
            }

            return digest.decode(response,type);
        }
    }

}
