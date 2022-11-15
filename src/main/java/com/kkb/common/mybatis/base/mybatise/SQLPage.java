package com.kkb.common.mybatis.base.mybatise;

/**
 * 项目名称：kkb-srm-plugin-server
 * 类名称：SQLPage
 * 类描述：分页
 * 创建人：YuanGL
 * 创建时间：2019年3月25日11:07:18
 * version 2.0
 */
public class SQLPage {

    private String dataSourceId;

    public SQLPage(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public String getPagedQuery(String sql, int startNum, int pagePreNum) {
        StringBuilder pagingSelect = new StringBuilder(sql.length() + 100);
        if ("ORACLE".equals(this.dataSourceId.toUpperCase())) {
            pagingSelect.append("SELECT * FROM (SELECT INNER.*, ROWNUM as RECORD_ FROM (");
            pagingSelect.append(sql);
            pagingSelect.append(") INNER WHERE ROWNUM <= ").append(startNum + pagePreNum).append(") WRAPPED WHERE WRAPPED.RECORD_ >=   ").append(startNum + 1);
        } else {
            if (!"MYSQL".equals(this.dataSourceId.toUpperCase())) {
                throw new NullPointerException("dbType is not supported");
            }

            pagingSelect.append("SELECT TEMP.*,1 RECORD_ FROM (");
            pagingSelect.append(sql);
            pagingSelect.append(" ) AS TEMP LIMIT ").append(startNum).append(" , ").append(pagePreNum);
        }

        return pagingSelect.toString();
    }

    public String getCountQuery(String sql) {
        StringBuilder buffer = new StringBuilder(sql.length() + 100);
        return "MYSQL".equals(this.dataSourceId.toUpperCase()) ? buffer.append("SELECT count(*) FROM (").append(sql).append(") AS TOTABLE").toString() : buffer.append("SELECT count(*) FROM (").append(sql).append(") ").toString();
    }

    public static boolean isSupport(String dataSourceId) {
        return "ORACLE".equals(dataSourceId.toUpperCase()) || "MYSQL".equals(dataSourceId.toUpperCase());
    }
}
