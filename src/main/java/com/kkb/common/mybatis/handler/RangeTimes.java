package com.kkb.common.mybatis.handler;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.Lists;
import com.kkb.common.core.exception.KkbBusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * 时间范围类
 *
 * @author zhangyang
 */
public class RangeTimes {

    private final List<RangeTime> rangeTimes;

    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private static final Supplier<CollectionType> typeSupplier;

    static {
        //手动添加 java time 对应的module模块，如果已经存在time模块，则直接忽略
        jsonMapper.registerModule(new JavaTimeModule());
        typeSupplier = () -> jsonMapper.getTypeFactory().constructCollectionType(List.class, RangeTime.class);
    }

    public RangeTimes() {
        this.rangeTimes = new ArrayList<>();
    }

    public RangeTimes(List<RangeTime> rangeTimes) {
        this.rangeTimes = rangeTimes;
    }

    public RangeTimes newTimes(RangeTime... times) {
        rangeTimes.addAll(Arrays.asList(times));
        return this;
    }

    public RangeTimes addTime(RangeTime rangeTime) {
        rangeTimes.add(rangeTime);
        return this;
    }

    public RangeTimes addTime(LocalTime startTime, LocalTime endTime) {
        rangeTimes.add(RangeTime.of(startTime, endTime));
        return this;
    }

    public List<RangeTime> getRangeTimes() {
        Collections.sort(rangeTimes);
        return Collections.unmodifiableList(rangeTimes);
    }


    public static RangeTimes from(List<RangeTime> rangeTimes) {
        return new RangeTimes(rangeTimes);
    }


    public static RangeTimes empty() {
        return new RangeTimes();
    }

    /**
     * 判断是否在工作时间
     */
    public boolean inWorkTime(LocalTime localTime) {
        return rangeTimes.isEmpty() || rangeTimes.stream()
                .noneMatch(rangeTime -> rangeTime.inCurrent(localTime));
    }


    /**
     * 获取休息时间之外的工作开始时间
     */
    public List<LocalTime> getWorkTimes() {
        List<RangeTime> rangeTimes = Optional.of(getRangeTimes()).orElseGet(Collections::emptyList);

        //获取工作时间
        List<LocalTime> workTimes = Lists.newArrayList();

        if(rangeTimes.isEmpty()){
            workTimes.add(LocalTime.MIN);
        }

        //遍历当前所有的休息时间
        for (int current = 0; current < rangeTimes.size(); current++) {
            if (rangeTimes.size() == 1) {
                workTimes.add(rangeTimes.get(current).getEndTime());
                break;
            }

            //计算下一个时间的位置
            int next = current + 1;

            if (current == rangeTimes.size() - 1)
                next = 0;

            LocalTime endTime = rangeTimes.get(current).getEndTime();
            LocalTime startTime = rangeTimes.get(next).getStartTime();
            if (startTime.compareTo(endTime) == 0)
                continue;

            workTimes.add(endTime);
        }
        return workTimes;
    }

    public List<LocalTime> getApplyTimes(){
        List<LocalTime> list = new ArrayList<>();
        List<RangeTime> rangeTimes = getRangeTimes();
        //初始化当天起始时间
        final LocalTime[] restTime = new LocalTime[]{LocalTime.MIN,LocalTime.MAX.truncatedTo(ChronoUnit.MINUTES)};
        if (null == rangeTimes || rangeTimes.isEmpty()){
            list.add(restTime[0]);
            return list;
        }
        Stream<RangeTime> stream = rangeTimes.stream()
                .sorted(Comparator.comparing(RangeTime::getStartTime));
        if(!LocalTime.MIN.isBefore(rangeTimes.get(0).getStartTime())){
            //如果休息时间段和当天起始时间相同,则跳过首个时间段
            stream = stream.skip(1);
            restTime[0] = rangeTimes.get(0).getEndTime();
        }
        //工作时间的起始时间是上次休息的结束时间
        //工作时间的结束时间是下次休息的开始时间
        stream.forEach(rangeTime -> {
            restTime[1] = rangeTime.getStartTime();
            if(restTime[0].isBefore(restTime[1])){
                list.add(restTime[0]);
            }
            restTime[0] = rangeTime.getEndTime();
        });
        //如果没有休息到最后,则需要补充工作时间段
        if(LocalTime.MAX.truncatedTo(ChronoUnit.MINUTES).isAfter(restTime[0])){
            list.add(restTime[0]);
        }

        return list;
    }


    /**
     * 将RangeTimes 转为json
     */
    public String toJson() {
        try {
            return jsonMapper.writeValueAsString(rangeTimes);
        } catch (JsonProcessingException e) {
            throw KkbBusinessException.of("RangeTimes: json encode error");
        }
    }

    /**
     * 将json转为RangeTimes对象
     *
     * @param rangeJson 时间json字符串
     */
    public static RangeTimes readSting(String rangeJson) {
        List<RangeTime> rangeTimes = null;
        try {
            rangeTimes = jsonMapper.readValue(rangeJson, typeSupplier.get());
            return from(rangeTimes);
        } catch (IOException e) {
            throw KkbBusinessException.of("RangeTimes: json decode error");
        }
    }


    @Getter
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RangeTime implements Serializable, Comparable<RangeTime> {
        /**
         * 开始时间
         */
        @JsonFormat(pattern = "HH:mm:ss")
        private LocalTime startTime;

        /**
         * 结束时间
         */
        @JsonFormat(pattern = "HH:mm:ss")
        private LocalTime endTime;

        public static RangeTime of(LocalTime startTime, LocalTime endTime) {
            return new RangeTime(startTime, endTime);
        }


        public RangeTime startTime(LocalTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public RangeTime endTime(LocalTime endTime) {
            this.endTime = endTime;
            return this;
        }


        /**
         * 判断时间是否在当前时间范围内
         */
        public boolean inCurrent(LocalTime localTime) {
            return localTime.compareTo(endTime) <= 0 && localTime.compareTo(startTime) >= 0;
//            return localTime.isBefore(endTime) && localTime.isAfter(startTime);
        }

        /**
         * 当前时间是否存在当前时间范围内
         */
        public boolean inCurrentNow() {
            return inCurrent(LocalTime.now());
        }

        @Override
        public int compareTo(@NonNull RangeTime other) {
            return this.startTime.compareTo(other.startTime);
        }
    }

}
