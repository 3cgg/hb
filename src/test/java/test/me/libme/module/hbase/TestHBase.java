package test.me.libme.module.hbase;

import me.libme.module.hbase.HBaseConnector;
import me.libme.module.hbase.KeyValue;
import me.libme.module.hbase.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by J on 2018/1/17.
 */
public class TestHBase {

    private static Logger logger= LoggerFactory.getLogger(TestHBase.class);


    public static void main(String[] args) {

        String tableName = "blog";

        HBaseConnector.HBaseExecutor executor=new HBaseConnector().connect();
        executor.tableOperations().delete(tableName);
        executor.tableOperations().create(tableName, "artitle", "author");

//        createHTable(connection, "blog")
        //插入数据,重复执行为覆盖

        executor.columnOperations().insert(tableName, "artitle", "engish", "002", "c++ for me");
        executor.columnOperations().insert(tableName, "artitle", "engish", "003", "python for me");
        executor.columnOperations().insert(tableName, "artitle", "chinese", "004", "C++ for china");
        //删除记录
        // deleteRecord(connection,"blog","artitle","chinese","002")
        //扫描整个表


        Map<Value, KeyValue> map = executor.queryOperations().scan(tableName, "artitle", "engish");
        logger.debug(map.toString());

        Map rowMap=new HashMap();

        map.forEach((key, value) -> {
            rowMap.put(key,executor.queryOperations().row(tableName,"artitle"));
        });

        logger.debug(rowMap.toString());
    }












}
