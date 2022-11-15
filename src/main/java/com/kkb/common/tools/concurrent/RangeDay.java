package com.kkb.common.tools.concurrent;

import com.kkb.common.tools.concurrent.date.TemporalUtils;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * TODO
 * 日期范围类，开始月份 - 结束月份
 *
 * @author zhangyang
 * @date 2020-06-23
 */
@Data
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RangeDay {

    /**
     * 开始月份
     */
    @NonNull
    private LocalDate startDay;

    /**
     * 结束月份
     */
    private LocalDate endDay;


    public static RangeDayBuilder builder() {
        return new RangeDayBuilder();
    }


    public RangeDayBuilder mutate() {
        return new RangeDayBuilder(startDay, endDay);
    }

    /**
     * 获取开始时间到结束时间的月份差值
     *
     * 例如：
     *      2020-12-11 - 2020-01-11 = 11
     */
    public int rangeMonth() {
        return (int) (endDay == null ? 1 : startDay.until(endDay, ChronoUnit.MONTHS));
    }


    @AllArgsConstructor
    public static final class RangeDayBuilder {
        private LocalDate startDay;
        private LocalDate endDay;

        private RangeDayBuilder() {
        }

        public RangeDayBuilder startDay(int year, int month, int day) {

            this.startDay = LocalDate.of(year, month, day);

            /*if (startDay.isSupported(ChronoField.DAY_OF_MONTH)) {
                startDay.with(ChronoField.DAY_OF_MONTH, day);
            }
            if (startDay.isSupported(ChronoField.MONTH_OF_YEAR)) {
                startDay.with(ChronoField.MONTH_OF_YEAR, month);
            }
            if (startDay.isSupported(ChronoField.YEAR)) {
                startDay.with(ChronoField.YEAR, year);
            }*/

            return this;
        }

        public RangeDayBuilder endDay(int year, int month, int day) {
            this.endDay = LocalDate.of(year, month, day);
            return this;
        }


        public RangeDayBuilder startMonth(@NonNull YearMonth startMonth) {
            this.startDay = startMonth.query(TemporalUtils.TO_LOCAL_DATE_OF_FIRST_DAY_QUERY);
            return this;
        }


        public RangeDayBuilder endMonth(YearMonth endMonth) {
            this.endDay = endMonth.query(TemporalUtils.TO_LOCAL_DATE_OF_LAST_DAY_QUERY);
            return this;
        }


        public RangeDayBuilder startDay(@NonNull Date startDay) {
            this.startDay = TemporalUtils.DateToLocalDateConverter.INSTANCE.convert(startDay);
            return this;
        }


        public RangeDayBuilder endDay(Date endDay) {
            this.endDay = TemporalUtils.DateToLocalDateConverter.INSTANCE.convert(endDay);
            return this;
        }


        public RangeDayBuilder startDay(@NonNull LocalDate startDay) {
            this.startDay = startDay;
            return this;
        }

        public RangeDayBuilder endDay(LocalDate endDay) {
            this.endDay = endDay;
            return this;
        }


        public RangeDay build() {
            return new RangeDay(startDay, endDay);
        }
    }
}
