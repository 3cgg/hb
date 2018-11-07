package me.libme.module.hbase;

import java.util.List;
import java.util.Map;

/**
 * Created by J on 2018/11/5.
 */
public interface QueryOperations {

    Map<Value,KeyValue>  scan(String tableName,String family,String column);

    <T extends Value> Map<T,KeyValue>  scan(String tableName, String family, String column, RowValueConvert rowConvert, ColumnValueConvert columnValueConvert);

    <T extends Value> Map<T,List<KeyValue>>  scan(String tableName, RowValueConvert<T> rowConvert, ColumnValueConvert columnValueConvert, KeyValue... keyValue);

    <T extends Value> Map<T,List<KeyValue>> row(String tableName, T row,ColumnValueConvert columnValueConvert);

    <T extends Value> Map<T,List<KeyValue>> row(String tableName, T row);

    Map<Value,List<KeyValue>> row(String tableName, String row);

    Map<Value,List<KeyValue>> row(String tableName, long row);

    <T extends Value> Map<T,List<KeyValue>>  scan(String tableName, RowValueConvert<T> rowConvert, ColumnValueConvert columnValueConvert, IFilter filter, KeyValue... keyValue);

    <T extends Value> Map<T,List<KeyValue>>  scan(String tableName, RowValueConvert<T> rowConvert, ColumnValueConvert columnValueConvert, IFilter filter, ScanConfig scanConfig, KeyValue... keyValue);

}
