package com.kkb.common.mybatis.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.util.StringUtils;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * RangeTimes 类型的类型处理器
 * @author zhangyang
 *
 * @see org.apache.ibatis.type.TypeHandler
 */
public class RangeTimesTypeHandler extends BaseTypeHandler<RangeTimes> {

    @Override
    public void setNonNullParameter(PreparedStatement statement, int i, RangeTimes rangeTimes, JdbcType jdbcType) throws SQLException {
        statement.setString(i, rangeTimes.toJson());
    }

    @Override
    public RangeTimes getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        String rangeJson = resultSet.getString(columnName);
        return parseJson(rangeJson);
    }

    @Override
    public RangeTimes getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        String rangeJson = resultSet.getString(columnIndex);
        return parseJson(rangeJson);
    }

    @Override
    public RangeTimes getNullableResult(CallableStatement statement, int columnIndex) throws SQLException {
        String rangeJson = statement.getString(columnIndex);
        return parseJson(rangeJson);
    }

    /**
     * 将 json转为 RangeTimes 对象
     * @param rangeJson 数据库返回的 json字符串
     */
    private RangeTimes parseJson(String rangeJson) {
        return Optional.ofNullable(rangeJson)
                .filter(StringUtils::hasText)
                .map(RangeTimes::readSting)
                .orElseGet(RangeTimes::new);
    }
}
