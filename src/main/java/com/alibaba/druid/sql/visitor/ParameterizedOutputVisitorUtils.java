/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.visitor;

import com.alibaba.druid.sql.JdbcConstants;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2OutputVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleParameterizedOutputVisitor;
import com.alibaba.druid.sql.dialect.phoenix.visitor.PhoenixOutputVisitor;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGOutputVisitor;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerOutputVisitor;
import com.alibaba.druid.sql.parser.*;

import java.util.List;

public class ParameterizedOutputVisitorUtils {
    private final static SQLParserFeature[] features = {
            SQLParserFeature.EnableSQLBinaryOpExprGroup,
            SQLParserFeature.OptimizedForParameterized
    };

    public static String parameterize(String sql, String dbType) {
        return parameterize(sql, dbType, null);
    }

    public static String parameterize(String sql, String dbType, SQLSelectListCache selectListCache) {
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType, features);

        if (selectListCache != null) {
            parser.setSelectListCache(selectListCache);
        }

        List<SQLStatement> statementList = parser.parseStatementList();
        if (statementList.size() == 0) {
            return sql;
        }

        StringBuilder out = new StringBuilder(sql.length());
        ParameterizedVisitor visitor = createParameterizedOutputVisitor(out, dbType);

        for (int i = 0; i < statementList.size(); i++) {
            if (i > 0) {
                out.append(";\n");
            }
            SQLStatement stmt = statementList.get(i);

            if (stmt.hasBeforeComment()) {
                stmt.getBeforeCommentsDirect().clear();
            }
            stmt.accept(visitor);
        }

        if (visitor.getReplaceCount() == 0
                && parser.getLexer().getCommentCount() == 0 && sql.charAt(0) != '/') {
            return sql;
        }

        return out.toString();
    }

    public static String parameterize(List<SQLStatement> statementList, String dbType) {
        StringBuilder out = new StringBuilder();
        ParameterizedVisitor visitor = createParameterizedOutputVisitor(out, dbType);

        for (int i = 0; i < statementList.size(); i++) {
            if (i > 0) {
                out.append(";\n");
            }
            SQLStatement stmt = statementList.get(i);

            if (stmt.hasBeforeComment()) {
                stmt.getBeforeCommentsDirect().clear();
            }
            stmt.accept(visitor);
        }

        return out.toString();
    }

    public static ParameterizedVisitor createParameterizedOutputVisitor(Appendable out, String dbType) {
        if (JdbcConstants.ORACLE.equals(dbType) || JdbcConstants.ALI_ORACLE.equals(dbType)) {
            return new OracleParameterizedOutputVisitor(out);
        }

        if (JdbcConstants.MYSQL.equals(dbType)
            || JdbcConstants.MARIADB.equals(dbType)
            || JdbcConstants.H2.equals(dbType)) {
            return new MySqlOutputVisitor(out, true);
        }

        if (JdbcConstants.POSTGRESQL.equals(dbType)
                || JdbcConstants.ENTERPRISEDB.equals(dbType)) {
            return new PGOutputVisitor(out, true);
        }

        if (JdbcConstants.SQL_SERVER.equals(dbType) || JdbcConstants.JTDS.equals(dbType)) {
            return new SQLServerOutputVisitor(out, true);
        }

        if (JdbcConstants.DB2.equals(dbType)) {
            return new DB2OutputVisitor(out, true);
        }

        if (JdbcConstants.PHOENIX.equals(dbType)) {
            return new PhoenixOutputVisitor(out, true);
        }

        if (JdbcConstants.ELASTIC_SEARCH.equals(dbType)) {
            return new MySqlOutputVisitor(out, true);
        }

        return new SQLASTOutputVisitor(out, true);
    }
}
