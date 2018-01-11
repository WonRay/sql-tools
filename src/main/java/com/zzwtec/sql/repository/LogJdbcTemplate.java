package com.zzwtec.sql.repository;

import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.fastjson.JSONObject;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于记录jdbc层面的日志
 * @author 毛文超
 * */
public class LogJdbcTemplate extends JdbcTemplate {

    private LogHandler logHandler;

    private static final Pattern UPDATE_PATTERN = Pattern.compile("^(UPDATE|update).*");
    private static final Pattern INSERT_PATTERN = Pattern.compile("^(INSERT|insert).*");
    private static final Pattern DELETE_PATTERN = Pattern.compile("^(DELETE|delete).*");


    public LogJdbcTemplate(DataSource dataSource,LogHandler logHandler){
        super(dataSource);
        this.logHandler = logHandler;
    }


    @Override
    public int update(String sql, Object... args) throws DataAccessException {
        LogOperate logOperate = parseOperate(sql);
        Assert.notNull(logOperate,"不匹配的操作类型,日志记录失败!");
        switch (logOperate){
            case DELETE:
                deleteRecordLog(logOperate,sql,args);
                break;
            case UPDATE:
                updateRecordLog(logOperate,sql,args);
                break;
            case INSERT:
                insertRecordLog(logOperate,sql,args);
                break;
            default:
                break;
        }
        return super.update(sql, args);
    }

    private void updateRecordLog(LogOperate operate,String sql,Object ...args){
        MySqlStatementParser mySqlStatementParser = new MySqlStatementParser(sql);
        SQLUpdateStatement sqlUpdateStatement = mySqlStatementParser.parseUpdateStatement();
        SQLTableSource tableSource = sqlUpdateStatement.getTableSource();
        Assert.notNull(tableSource,"不匹配的操作类型,日志记录失败!");

        List<SQLUpdateSetItem> items = sqlUpdateStatement.getItems();
        JSONObject log = new JSONObject();
        for (int i = 0; i < items.size(); i++) {
            SQLUpdateSetItem sqlUpdateSetItem = items.get(i);
            log.put(sqlUpdateSetItem.getColumn().toString(),args[i]);
        }

        logHandler.handle(operate,sqlUpdateStatement.getTableName().getSimpleName(),sqlUpdateStatement.getTableName().getSimpleName(),log);
    }

    private void insertRecordLog(LogOperate operate,String sql,Object ...args){

    }

    private void deleteRecordLog(LogOperate operate,String sql,Object ...args){


    }


    @Override
    public int update(String sql) throws DataAccessException {
        LogOperate logOperate = parseOperate(sql);
        Assert.notNull(logOperate,"不匹配的操作类型,日志记录失败!");
        switch (logOperate){
            case DELETE:
                deleteRecordLog(logOperate,sql);
                break;
            case UPDATE:
                updateRecordLog(logOperate,sql);
                break;
            case INSERT:
                insertRecordLog(logOperate,sql);
                break;
            default:
                break;
        }
        return super.update(sql);
    }

    private void updateRecordLog(LogOperate operate,String sql){

    }

    private void insertRecordLog(LogOperate operate,String sql){


    }

    private void deleteRecordLog(LogOperate operate,String sql){


    }

    @Override
    public <T> int[][] batchUpdate(String sql, Collection<T> batchArgs, int batchSize, ParameterizedPreparedStatementSetter<T> pss) throws DataAccessException {
        LogOperate logOperate = parseOperate(sql);
        Assert.notNull(logOperate,"不匹配的操作类型,日志记录失败!");
        switch (logOperate){
            case DELETE:
                batchDeleteRecordLog(logOperate,sql,batchArgs,pss);
                break;
            case UPDATE:
                batchUpdateRecordLog(logOperate,sql,batchArgs,pss);
                break;
            case INSERT:
                batchInsertRecordLog(logOperate,sql,batchArgs,pss);
                break;
            default:
                break;
        }
        return super.batchUpdate(sql, batchArgs, batchSize, pss);
    }


    private void batchUpdateRecordLog(LogOperate operate,String sql, Collection batchArgs, ParameterizedPreparedStatementSetter pss){


    }

    private void batchInsertRecordLog(LogOperate operate,String sql, Collection batchArgs, ParameterizedPreparedStatementSetter pss){

    }

    private void batchDeleteRecordLog(LogOperate operate,String sql, Collection batchArgs, ParameterizedPreparedStatementSetter pss){

    }

    /**
     * 解析sql语句的操作类型
     * */
    private LogOperate parseOperate(String sql){
        Matcher matcher = UPDATE_PATTERN.matcher(sql);
        boolean matches = matcher.matches();
        if(matches){
            return LogOperate.UPDATE;
        }

        matcher.usePattern(INSERT_PATTERN);
        matches = matcher.matches();

        if(matches){
            return LogOperate.INSERT;
        }

        matcher.usePattern(DELETE_PATTERN);
        matches = matcher.matches();

        if(matches){
            return LogOperate.DELETE;
        }

        return null;
    }

}
