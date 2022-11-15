package com.kkb.common.tools.assigner;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.kkb.plugins.uid.config.SystemProperties;
import com.kkb.plugins.uid.worker.WorkerIdAssigner;
import com.kkb.plugins.uid.worker.WorkerNode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.LongCodec;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import reactor.core.publisher.Operators;

import java.util.Date;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * workId分配器，用于保证workId唯一
 *
 * @author zhangyang
 */
@Slf4j
public class RedisIdAssigner implements WorkerIdAssigner, InitializingBean, BeanFactoryAware {

    private static final String DEFAULT_REDIS_KEY_TEMPLATE = "REDIS_ID_ASSIGNER:%s";
    private static final String DEFAULT_REDIS_KEY = "REDIS_ID_ASSIGNER:GLOBAL";
    private static final String REDIS_INCR_KEY = "WORK_ID";

    @Getter
    private SystemProperties systemProperties;

    @Autowired
    private RedissonClient redissonClient;

    private ObjectMapper jsonMapper;

    private AbstractAutowireCapableBeanFactory beanFactory;

    private final String redisKey;

    public RedisIdAssigner() {
        //如果不传入redisKey则使用默认的全局RedisKey，一旦项目启动确定使用哪一种形式就不能在改变
        this.redisKey = DEFAULT_REDIS_KEY;
    }

    public RedisIdAssigner(String serviceName) {
        Assert.hasText(serviceName, "The redis assigner for service key must not be null");
        this.redisKey = String.format(DEFAULT_REDIS_KEY_TEMPLATE, serviceName);
    }

    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (AbstractAutowireCapableBeanFactory) beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.jsonMapper = new ObjectMapper();
        this.jsonMapper.addMixIn(WorkerNode.class, WorkModeMixIn.class);
        this.systemProperties = initSystemProperties();
    }


    private SystemProperties initSystemProperties() {
        return (SystemProperties) beanFactory.createBean(SystemProperties.class, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, false);
    }


    public long assignWorkerId() {
        WorkerNode workerNode = this.getWorkerNode();
        this.saveWorkerNode(workerNode);
        log.info("uid generator log. save worker node: {}", workerNode);
        return workerNode.getId();
    }

    private void saveWorkerNode(WorkerNode workerNode) {
        long id = -1L;
        try {
            String workValue = jsonMapper.writeValueAsString(workerNode);
            id = redissonClient.getScript(LongCodec.INSTANCE).eval(RScript.Mode.READ_WRITE,
                    "local result = tonumber(redis.call('hincrby', KEYS[1], ARGV[1], 1));" +
                            "redis.call('hset', KEYS[1], result, ARGV[2]);" +
                            "return result;", RScript.ReturnType.INTEGER, Lists.newArrayList(redisKey), REDIS_INCR_KEY, workValue);
        } catch (Exception ex) {
            throw new WorkIdAssignException(workerNode, "uid generator error. error when insert worker_node.", ex);
        }

        if (id == -1L) {
            log.error("uid generator error log. error when insert worker_node. node: {}", workerNode);
            throw new WorkIdAssignException(workerNode, "uid generator error. error when insert worker_node.");
        } else {
            workerNode.setId(id);
        }
    }


    @Autowired(required = false)
    public void setSystemProperties(SystemProperties systemProperties) {
        this.systemProperties = systemProperties;
    }

    private WorkerNode getWorkerNode() {
        WorkerNode workerNode = new WorkerNode();
        workerNode.setInstance(this.systemProperties.getInstance());
        workerNode.setHostName(this.systemProperties.getHostname());
        workerNode.setPort(this.systemProperties.getPort());
        workerNode.setPid(this.systemProperties.getPid());
        workerNode.setType(this.systemProperties.getType());
        workerNode.setActiveProfiles(this.systemProperties.getActiveProfiles());
        workerNode.setCreateTime(new Date());
        return workerNode;
    }


    abstract static class WorkModeMixIn {

        @JsonIgnore
        abstract Date getUpdateTime();

        @JsonIgnore
        abstract Long getId();

        @JsonFormat(pattern = "yyyy-MM-dd")
        abstract Date getCreateTime();

    }

}
