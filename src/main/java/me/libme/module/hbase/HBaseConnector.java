package me.libme.module.hbase;

import me.libme.kernel._c.util.JStringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by J on 2018/11/5.
 */
public class HBaseConnector {

    private final HBaseConfig hBaseConfig;

    public HBaseConnector(HBaseConfig hBaseConfig) {
        this.hBaseConfig = hBaseConfig;
    }

    public HBaseExecutor connect(){

       return new HBaseExecutor(){};

    }


    public abstract class HBaseExecutor{

        private final Logger LOGGER= LoggerFactory.getLogger(HBaseExecutor.class);

        private final Connection connection;

        private final TableOperations tableOperations;

        private final ColumnOperations columnOperations;

        private final QueryOperations queryOperations;

        public TableOperations tableOperations() {
            return tableOperations;
        }

        public ColumnOperations columnOperations() {
            return columnOperations;
        }

        public QueryOperations queryOperations() {
            return queryOperations;
        }

        private HBaseExecutor() {
            Configuration conf = HBaseConfiguration.create();
            conf.set(HConstants.ZOOKEEPER_CLIENT_PORT, String.valueOf(hBaseConfig.getZkPort()));
            conf.set(HConstants.ZOOKEEPER_QUORUM, hBaseConfig.getZkHost());

            try {
                this.connection= ConnectionFactory.createConnection(conf);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(),e);
                throw new RuntimeException(e);
            }
            this.tableOperations=new _TableOperations();
            this.columnOperations=new _ColumnOperations();
            this.queryOperations=new _QueryOperations();
        }

        private class _QueryOperations implements QueryOperations{

            private final Logger LOGGER= LoggerFactory.getLogger(QueryOperations.class);


            @Override
            public Map<Value, KeyValue> scan(String tableName, String family, String column) {
                return scan(tableName, family, column, RowValueConvert::stringVal, ColumnValueConvert::stringVal);
            }

            @Override
            public  Map<Value, KeyValue> scan(String tableName, String family, String column, RowValueConvert rowConvert, ColumnValueConvert columnValueConvert) {
                try(Table table=connection.getTable(TableName.valueOf(tableName))){
                    Scan scan=new Scan();
                    scan.addColumn(Bytes.toBytes(family),Bytes.toBytes(column));
                    try(ResultScanner scanner=table.getScanner(scan)){
                        Map<Value,KeyValue> map=new HashMap<>();
                        Result result=scanner.next();
                        while(result!=null){
                            map.put(rowConvert.convert(result.getRow()),
                                    new KeyValue(family,column, columnValueConvert.convert(family,column,result.value())));
                            result=scanner.next();
                        }
                        return map;
                    }
                }catch (Exception e){
                    LOGGER.error(e.getMessage(),e);
                    throw new RuntimeException(e);
                }
            }

            @Override
            public Map<Value, List<KeyValue>> scan(String tableName, RowValueConvert rowConvert, ColumnValueConvert columnValueConvert, KeyValue... keyValue) {

                return scan(tableName, rowConvert, columnValueConvert,null, keyValue);

            }

            @Override
            public Map<Value, List<KeyValue>> scan(String tableName, RowValueConvert rowConvert, ColumnValueConvert columnValueConvert, IFilter filter, KeyValue... keyValue) {
                try(Table table=connection.getTable(TableName.valueOf(tableName))){
                    Scan scan=new Scan();
                    for(KeyValue kv:keyValue){
                        scan.addColumn(Bytes.toBytes(kv.getFamily()),Bytes.toBytes(kv.getColumn()));
                    }
                    if(filter!=null){
                        scan.setFilter(filter.filter());
                    }

                    try(ResultScanner scanner=table.getScanner(scan)){
                        Map<Value,List<KeyValue>> map=new HashMap<>();
                        Result result=scanner.next();
                        while(result!=null){
                            List<KeyValue> list=new ArrayList<>();
                            result.getNoVersionMap().forEach((family,qualifierVal)->{
                                qualifierVal.forEach((qualifier,val)->{
                                    String familyStr=Bytes.toString(family);
                                    String columnStr=Bytes.toString(qualifier);
                                    KeyValue kv=new KeyValue(familyStr,columnStr,
                                            columnValueConvert.convert(familyStr,columnStr,val));
                                    list.add(kv);
                                });
                            });
                            map.put(rowConvert.convert(result.getRow()),list);
                            result=scanner.next();
                        }
                        return map;
                    }

                }catch (Exception e){
                    LOGGER.error(e.getMessage(),e);
                    throw new RuntimeException(e);
                }
            }

            @Override
            public Map<Value, List<KeyValue>> row(String tableName, Value row,ColumnValueConvert columnValueConvert) {
                try(Table table=connection.getTable(TableName.valueOf(tableName))){
                    Get get=new Get(row.val());
                    Result result=table.get(get);
                    List<KeyValue> list=new ArrayList<>();
                    result.getNoVersionMap().forEach((family,qualifierVal)->{
                        qualifierVal.forEach((qualifier,val)->{
                            String familyStr=Bytes.toString(family);
                            String columnStr=Bytes.toString(qualifier);
                            KeyValue kv=new KeyValue(familyStr,columnStr,
                                    columnValueConvert.convert(familyStr,columnStr,val));
                            list.add(kv);
                        });
                    });
                    Map<Value,List<KeyValue>> map=new HashMap<>();
                    map.put(row,list);
                    return map;
                }catch (Exception e){
                    LOGGER.error(e.getMessage(),e);
                    throw new RuntimeException(e);
                }
            }

            @Override
            public Map<Value, List<KeyValue>> row(String tableName, Value row) {
                return row(tableName,row, ColumnValueConvert::stringVal);
            }


            @Override
            public Map<Value, List<KeyValue>> row(String tableName, String row) {
                return row(tableName,new StringValue(row));
            }

            @Override
            public Map<Value, List<KeyValue>> row(String tableName, long row) {
                return row(tableName,new LongValue(row));
            }
        }




        private class _ColumnOperations implements ColumnOperations{

            private final Logger LOGGER= LoggerFactory.getLogger(ColumnOperations.class);


            @Override
            public void insert(String tableName, String row, String family, String column, String value) {
                insert(tableName,new StringValue(row),family,column,new StringValue(value));
            }

            @Override
            public void insert(String tableName, long row, String family, String column, String value) {
                insert(tableName,new LongValue(row),family,column,new StringValue(value));
            }

            @Override
            public void insert(String tableName, Value row, String family, String column, Value value) {
                insert(tableName,row,new KeyValue(family,column,value));
            }

            @Override
            public void insert(String tableName, Value row, KeyValue... keyValue) {

                try(Table table=connection.getTable(TableName.valueOf(tableName))){
                    Put put=new Put(row.val());
                    for(KeyValue kv:keyValue){
                        put.addColumn(Bytes.toBytes(kv.getFamily()),Bytes.toBytes(kv.getColumn())
                                ,kv.getValue().val());
                    }
                    table.put(put);
                }catch (Exception e){
                    LOGGER.error(e.getMessage(),e);
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void delete(String tableName, Value row, String family, String column) {
                delete(tableName,row,new KeyValue(family,column));
            }

            @Override
            public void delete(String tableName, Value row, KeyValue... keyValue) {
                try(Table table=connection.getTable(TableName.valueOf(tableName))){
                    Delete delete=new Delete(row.val());
                    for(KeyValue kv:keyValue){
                        delete.addColumn(Bytes.toBytes(kv.getFamily()),Bytes.toBytes(kv.getColumn()));
                    }
                    table.delete(delete);
                }catch (Exception e){
                    LOGGER.error(e.getMessage(),e);
                    throw new RuntimeException(e);
                }
            }

            @Override
            public Map<Value,KeyValue> get(String tableName, Value row, String family, String column) {
                return get(tableName, row, family, column, ColumnValueConvert::stringVal);
            }

            @Override
            public Map<Value,KeyValue> get(String tableName, Value row, String family, String column, ColumnValueConvert columnValueConvert) {
                try(Table table=connection.getTable(TableName.valueOf(tableName))){
                    Get get=new Get(row.val());
                    get.addColumn(Bytes.toBytes(family),Bytes.toBytes(column));
                    Result result=table.get(get);
                    Map<Value,KeyValue> map=new HashMap<>();
                    map.put(row,new KeyValue(family,column, columnValueConvert.convert(family,column,result.value())));
                    return map;
                }catch (Exception e){
                    LOGGER.error(e.getMessage(),e);
                    throw new RuntimeException(e);
                }
            }

            @Override
            public Map<Value,List<KeyValue>> get(String tableName, Value row, ColumnValueConvert columnValueConvert, KeyValue... keyValue) {
                try(Table table=connection.getTable(TableName.valueOf(tableName))){
                    Get get=new Get(row.val());
                    for(KeyValue kv:keyValue){
                        get.addColumn(Bytes.toBytes(kv.getFamily()),Bytes.toBytes(kv.getColumn()));
                    }
                    Result result=table.get(get);
                    List<KeyValue> list=new ArrayList<>();
                    result.getNoVersionMap().forEach((family,qualifierVal)->{
                        qualifierVal.forEach((qualifier,val)->{
                            String familyStr=Bytes.toString(family);
                            String columnStr=Bytes.toString(qualifier);
                            KeyValue kv=new KeyValue(familyStr,columnStr,
                                    columnValueConvert.convert(familyStr,columnStr,val));
                            list.add(kv);
                        });
                    });
                    Map<Value,List<KeyValue>> map=new HashMap<>();
                    map.put(row,list);
                    return map;
                }catch (Exception e){
                    LOGGER.error(e.getMessage(),e);
                    throw new RuntimeException(e);
                }

            }
        }




        private class _TableOperations implements TableOperations{

            private final Logger LOGGER= LoggerFactory.getLogger(TableOperations.class);


            @Override
            public void create(String tableName, String cfName1) {
                create(tableName, cfName1,null);
            }

            @Override
            public void create(String tableName, String cfName1, String cfName2) {

                try(Admin admin= connection.getAdmin()) {
                    TableName tn= TableName.valueOf(tableName);
                    if(admin.tableExists(tn)){
                        throw new RuntimeException("table ["+tableName+"] already exists.");
                    }

                    HTableDescriptor tableDescriptor = new HTableDescriptor(tableName);
                    if(JStringUtils.isNotNullOrEmpty(cfName1)){
                        tableDescriptor.addFamily(new HColumnDescriptor(cfName1.getBytes()));
                    }
                    if(JStringUtils.isNotNullOrEmpty(cfName2)){
                        tableDescriptor.addFamily(new HColumnDescriptor(cfName2.getBytes()));
                    }
                    admin.createTable(tableDescriptor);

                    LOGGER.info("create table : "+tableName+",with column family ["+cfName1+"]");


                }catch (Exception e){
                    LOGGER.error(e.getMessage(),e);
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void disable(String tableName) {
                try(Admin admin = connection.getAdmin()) {
                    TableName tn = TableName.valueOf(tableName);
                    if (admin.tableExists(tn)){
                        admin.disableTable(tn);
                        LOGGER.info("disable table : "+tableName);
                    }
                }catch (Exception e){
                    LOGGER.error(e.getMessage(),e);
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void enable(String tableName) {

                try(Admin admin = connection.getAdmin()) {
                    TableName tn = TableName.valueOf(tableName);
                    if (admin.tableExists(tn)){
                        admin.enableTable(tn);
                        LOGGER.info("enable table : "+tableName);
                    }
                }catch (Exception e){
                    LOGGER.error(e.getMessage(),e);
                    throw new RuntimeException(e);
                }



            }

            @Override
            public void delete(String tableName) {

                try(Admin admin = connection.getAdmin()) {
                    TableName tn = TableName.valueOf(tableName);
                    if (admin.tableExists(tn)){
                        admin.disableTable(tn);
                        admin.deleteTable(tn);
                        LOGGER.info("delete table : "+tableName);
                    }
                }catch (Exception e){
                    LOGGER.error(e.getMessage(),e);
                    throw new RuntimeException(e);
                }


            }
        }





    }








}
