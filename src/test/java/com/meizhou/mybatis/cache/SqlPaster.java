package com.meizhou.mybatis.cache;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

/**
 * Created by meizhou on 2018/8/19.
 */
public class SqlPaster {

    public static void main(String[] args) {
        String sql = SQLUtils.format("INSERT INTO Persons (LastName, Address) VALUES ('Wilson', 'Champs-Elysees')", JdbcConstants.MYSQL);
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        for (SQLStatement stmt : stmtList) {
            stmt.accept(statVisitor);
        }
        System.out.println(statVisitor.getParameters());
        for (TableStat.Column column : statVisitor.getColumns()) {
            System.out.println(column.getName());
            System.out.println(column.getAttributes());
        }
        System.out.println(statVisitor.getTables());
        System.out.println(statVisitor.getConditions());
    }
}
