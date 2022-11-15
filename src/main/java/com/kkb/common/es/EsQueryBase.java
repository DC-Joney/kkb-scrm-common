package com.kkb.common.es;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author chixin
 * @version 1.0
 * @date 2021/8/3 4:37 下午
 */
@Data
@Slf4j
public class EsQueryBase {
    //排序配置
    public List<OrderQuery> sorting;

    public EsBuilder<?> esBuilder;

    private Map<String, Object> esMap;

    public Integer page;
    //每页记录数
    public Integer size;

    public Map<String, Object> extContent;


    public String bussiness;
    /**
     * 要查询的属性
     */
    public String[] includes;

    @Data
    public static class OrderQuery {
        public String key;
        //ASC, DESC
        public String type;
    }


    @Data
    public static class EsBuilder<V> {
        //term 精确查询 ,terms in精确查询，match 模糊查询，ranger 范围查询 ,exists 是否有值, bool 组合查询
        public String action;
        public String key;
        public Object val;
        public Collection<V> vals;
        public Object fromval;
        public Object toVal;
        public Boolean hasFromVal = true;
        public Boolean hasToVal = true;

        private List<EsBuilder<?>> must;

        private List<EsBuilder<?>> mustNot;

        private List<EsBuilder<?>> should;

        public QueryBuilder parseQueryBuilder() {
            if ("term".equals(action)) {
                if (val instanceof String) {
                    key = key + ".keyword";
                }
                return QueryBuilders.termQuery(key, val);
            }
            if ("terms".equals(action)) {

                key = key + ".keyword";

                return QueryBuilders.termsQuery(key, vals.toArray());
            }
            if ("match".equals(action)) {
                return QueryBuilders.matchQuery(key, val);
            }
            if ("ranger".equals(action)) {
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(key);
                if (fromval != null) {
                    rangeQueryBuilder.from(fromval, hasFromVal);
                }
                if (toVal != null) {
                    rangeQueryBuilder.to(toVal, hasToVal);
                }
                return rangeQueryBuilder;
            }
            if ("exists".equals(action)) {
                return QueryBuilders.existsQuery(key);
            }

            if ("bool".equals(action)) {
                BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
                if (!CollectionUtils.isEmpty(must)) {
                    log.info("EsQueryBase.must:{}", must);
                    for (EsBuilder<?> esQueryBuilder : must) {
                        log.info("EsQueryBase.esQueryBuilder:{}", esQueryBuilder);
                        QueryBuilder queryBuilder = esQueryBuilder.parseQueryBuilder();
                        log.info("EsQueryBase.queryBuilder:{}", queryBuilder);
                        boolQueryBuilder.must(queryBuilder);
                    }
                }
                if (!CollectionUtils.isEmpty(mustNot)) {
                    for (EsBuilder<?> esQueryBuilder : mustNot) {
                        boolQueryBuilder.mustNot(esQueryBuilder.parseQueryBuilder());
                    }
                }
                if (!CollectionUtils.isEmpty(should)) {
                    for (EsBuilder<?> esQueryBuilder : should) {
                        boolQueryBuilder.should(esQueryBuilder.parseQueryBuilder());
                    }
                }

                return boolQueryBuilder;
            }
            return null;
        }
    }

    public Map<String, Object> getEsMapOrDefault() {
        if (null == getEsMap()) {
            setEsMap(new HashMap<>());
        }
        return getEsMap();
    }


}
