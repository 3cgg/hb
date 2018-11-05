package me.libme.module.hbase;

import java.util.List;
import java.util.Map;

/**
 * Created by J on 2018/11/5.
 */
public interface ColumnOperations<T extends Value> {

    void insert(String tableName,String row,String family,String column,String value);

    void insert(String tableName,long row,String family,String column,String value);

    void insert(String tableName,T row,String family,String column,Value value);

    void insert(String tableName,T row,KeyValue... keyValue);

    void delete(String tableName,T row,String family,String column);

    void delete(String tableName,T row,KeyValue... keyValue);

    Map<T,KeyValue> get(String tableName, T row, String family, String column);

    Map<T,KeyValue> get(String tableName, T row, String family, String column,ColumnValueConvert columnValueConvert);

    Map<T,List<KeyValue>> get(String tableName, T row, ColumnValueConvert columnValueConvert, KeyValue... keyValue);

}
