package com.symptom.config;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@MappedTypes(Date.class)
public class SqliteDateTypeHandler extends BaseTypeHandler<Date> {

    private static final String[] PATTERNS = {
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd"
    };

    private Date parse(String value) throws SQLException {
        if (value == null || value.isEmpty()) {
            return null;
        }
        for (String pattern : PATTERNS) {
            try {
                return new SimpleDateFormat(pattern).parse(value);
            } catch (ParseException ignored) {
            }
        }
        throw new SQLException("Cannot parse date: " + value);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Date parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(parameter));
    }

    @Override
    public Date getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getString(columnName));
    }

    @Override
    public Date getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getString(columnIndex));
    }

    @Override
    public Date getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getString(columnIndex));
    }
}
