package com.meizhou.mybatis.cache;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

/**
 * Created by meizhou on 2018/8/19.
 */
public class SqlPaster3 {

    public static void main(String[] args) {
        String sql = SQLUtils.format("select * from city where city_id>1 and user_id=1 or (aaa=1 and kkk=9)", JdbcConstants.MYSQL);
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        StringBuffer where = new StringBuffer();
        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        for (SQLStatement stmt : stmtList) {
            stmt.accept(statVisitor);
        }
        System.out.println(statVisitor.getParameters());
        System.out.println(statVisitor.getColumns());
        System.out.println(statVisitor.getTables());
        System.out.println(statVisitor.getConditions());
    }
}
