package com.mamcharge.integrate.core.handler.type;

import com.mamcharge.integrate.core.domain.form.MamColumn;
import org.apache.ibatis.type.*;

import java.sql.*;
import java.util.Set;

/**
 * @description: 字段类型转换配置
 * @author: Quentin Zhang
 * @create: 2021-02-25 17:41
 **/
@MappedJdbcTypes(JdbcType.SMALLINT)
@MappedTypes(Boolean.class)
public class HgSmallIntTypeConverter extends BaseTypeHandler<Boolean> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType jdbcType) throws SQLException {
        ps.setBoolean(i, parameter);
    }

    @Override
    public Boolean getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.getGender(rs.getInt(columnName));
    }

    @Override
    public Boolean getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.getGender(rs.getInt(columnIndex));
    }

    @Override
    public Boolean getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.getGender(cs.getInt(columnIndex));
    }

    private Boolean getGender(Integer genderCode) {
        if(genderCode > 0) {
            return true;
        }
        else {
            return false;
        }
    }
}
