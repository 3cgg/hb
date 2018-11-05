package me.libme.module.hbase;

/**
 * be aware of column family, we recommend the number of that is up to two
 * Created by J on 2018/11/5.
 */
public interface TableOperations {

    void create(String tableName,String cfName1);

    void create(String tableName,String cfName1,String cfName2);

    void disable(String tableName);

    void enable(String tableName);

    void delete(String tableName);

}
