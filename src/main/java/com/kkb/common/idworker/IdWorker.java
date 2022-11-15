/*
 * description
 */
package com.kkb.common.idworker;

import com.kkb.plugins.uid.generator.UidGenerator;

import java.util.UUID;

/**
 * id 生成管理
 *
 * @author sy
 * @since 1.0.0
 */
public class IdWorker<T> {

    private UidGenerator<T> generator;

    private IdWorker(UidGenerator<T> generator) {
        this.generator = generator;
    }

    public T get() {
        return generator.get();
    }

    public String parse(T id) {
        return generator.parse(id);
    }

    public static UuidFactory uuid() {
        return new UuidFactory();
    }

    public static UidFactory uid() {
        return new UidFactory();
    }

    static abstract class Factory<T> {

        /**
         * 构建 idWorker
         *
         * @return
         */
        abstract IdWorker<T> create();

    }

    public static class UuidFactory extends Factory<String> {

        @Override
        public IdWorker<String> create() {
            return new IdWorker<>(new UuidGenerator());
        }

    }

    public static class UidFactory extends Factory<Long> {

        private UidGenerator<Long> uidGenerator;

        public UidFactory uidGenerator(UidGenerator<Long> uidGenerator) {
            this.uidGenerator = uidGenerator;
            return this;
        }

        @Override
        public IdWorker<Long> create() {
            return new IdWorker<>(uidGenerator);
        }

    }

    private static class UuidGenerator implements UidGenerator<String> {

        @Override
        public String get() {
            return UUID.randomUUID().toString();
        }

        @Override
        public String parse(String uuid) {
            return uuid;
        }

    }

}
