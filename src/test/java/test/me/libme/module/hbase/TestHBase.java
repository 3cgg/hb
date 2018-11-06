package test.me.libme.module.hbase;

import me.libme.module.hbase.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by J on 2018/1/17.
 */
public class TestHBase {

    private static Logger logger= LoggerFactory.getLogger(TestHBase.class);

    String[] color=new String[]{"red","black","red","orange"};
    double[] weight=new double[]{1.5,1.6,2.0,2.2};
    double[] length=new double[]{2.5,2.6,2.8,2.2};
    double[] width=new double[]{1.5,1.6,1.8,1.9};
    String[] category=new String[]{"大型车","中型车","小型车","微型车"};
    String[] snType=new String[]{"黄牌","黑牌","蓝牌","其他"};


    String tableName = "car-info";

    String metaInfoFamily="meta-info";
    String statisticsFamily="statistics";

    HBaseConnector.HBaseExecutor executor= HBaseExecutor.defaultExecutor();

    private void insert(){

        executor.tableOperations().delete(tableName);
        executor.tableOperations().create(tableName, metaInfoFamily, statisticsFamily);

//        createHTable(connection, "blog")
        //插入数据,重复执行为覆盖



        for(int i=0;i<10;i++){
            List<KeyValue> keyValues=new ArrayList<>();
            Random random=new Random();
            keyValues.add(new KeyValue(metaInfoFamily,"color",color[random.nextInt(4)]));
            keyValues.add(new KeyValue(metaInfoFamily,"weight",weight[random.nextInt(4)]));
            keyValues.add(new KeyValue(metaInfoFamily,"length",length[random.nextInt(4)]));
            keyValues.add(new KeyValue(metaInfoFamily,"width",width[random.nextInt(4)]));
            keyValues.add(new KeyValue(metaInfoFamily,"category",category[random.nextInt(4)]));
            keyValues.add(new KeyValue(metaInfoFamily,"type",snType[random.nextInt(4)]));
            String row=new Date().getTime()+"E"+(random.nextInt(9000)+1000);
            executor.columnOperations().insert(tableName, new StringValue(row),keyValues.toArray(new KeyValue[]{}));
        }

        Map<Value, KeyValue> map = executor.queryOperations().scan(tableName, metaInfoFamily, "color");
        logger.debug(map.toString());

        Map rowMap=new HashMap();

        map.forEach((key, value) -> {
            rowMap.put(key,executor.queryOperations().row(tableName,key));
        });

        logger.debug(rowMap.toString());


    }


    private class DefineColumnValueConvert implements ColumnValueConvert{
        @Override
        public Value convert(String family, String column, byte[] bytes) {
            if(metaInfoFamily.equals(family)
                    &&"color".equals(column)){
                return ColumnValueConvert.stringVal(family, column, bytes);
            }else if(metaInfoFamily.equals(family)
                    &&"weight".equals(column)){
                return ColumnValueConvert.doubleVal(family, column, bytes);
            }else if(metaInfoFamily.equals(family)
                    &&"length".equals(column)){
                return ColumnValueConvert.doubleVal(family, column, bytes);
            }else if(metaInfoFamily.equals(family)
                    &&"width".equals(column)){
                return ColumnValueConvert.doubleVal(family, column, bytes);
            }else if(metaInfoFamily.equals(family)
                    &&"category".equals(column)){
                return ColumnValueConvert.stringVal(family, column, bytes);
            }else if(metaInfoFamily.equals(family)
                    &&"type".equals(column)){
                return ColumnValueConvert.stringVal(family, column, bytes);
            }

            return ColumnValueConvert.stringVal(family, column, bytes);
        }
    }


    private void get(){

        DefineColumnValueConvert defineColumnValueConvert=new DefineColumnValueConvert();

        Map<StringValue,List<KeyValue>> any=executor.queryOperations().scan(tableName, RowValueConvert::stringVal,defineColumnValueConvert);
        any.forEach((value, keyValues) -> {
            System.out.println(value+"=>"+keyValues);

        });


        List<String> anyOne=new ArrayList<>();

        Map<Value, KeyValue> all= executor.queryOperations().scan(tableName,metaInfoFamily,"length",RowValueConvert::stringVal,ColumnValueConvert::doubleVal);
        all.forEach((value, keyValue) -> {
            System.out.println(value+"=>"+keyValue);
            anyOne.add(String.valueOf(value.original()));

        });



        String row=anyOne.get(0);
        Map<Value, KeyValue> length=executor.columnOperations().get(tableName,new StringValue(row),metaInfoFamily,"length",ColumnValueConvert::doubleVal);

        length.forEach((value, keyValue) -> {
            System.out.println(value+"=>"+keyValue.getValue().original());
        });


        System.out.println("end");

    }


    public static void main(String[] args) {

        TestHBase testHBase=new TestHBase();

//        testHBase.insert();

        testHBase.get();




    }












}
