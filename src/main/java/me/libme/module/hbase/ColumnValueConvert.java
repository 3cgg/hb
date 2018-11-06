package me.libme.module.hbase;

import me.libme.kernel._c._i.JParser;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * Created by J on 2018/11/5.
 */
public interface ColumnValueConvert<T extends Value> extends JParser {


    T convert(String family,String column,byte[] bytes);

    static StringValue stringVal(String family,String column,byte[] bytes){return new StringValue(Bytes.toString(bytes));}

    static IntValue intVal(String family,String column,byte[] bytes){return new IntValue(Bytes.toInt(bytes));}

    static LongValue longVal(String family,String column,byte[] bytes){return new LongValue(Bytes.toLong(bytes));}

    static DoubleValue doubleVal(String family,String column,byte[] bytes){return new DoubleValue(Bytes.toDouble(bytes));}

    static FloatValue floatVal(String family,String column,byte[] bytes){return new FloatValue(Bytes.toFloat(bytes));}

    static BigDecimalValue bigDecimalVal(String family,String column,byte[] bytes){return new BigDecimalValue(Bytes.toBigDecimal(bytes));}


}
