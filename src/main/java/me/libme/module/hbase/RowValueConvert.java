package me.libme.module.hbase;

import me.libme.kernel._c._i.JParser;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * Created by J on 2018/11/5.
 */
public interface RowValueConvert<T extends Value> extends JParser {


    T convert(byte[] bytes);

    static StringValue stringVal( byte[] bytes){return new StringValue(Bytes.toString(bytes));}

    static IntValue intVal(byte[] bytes){return new IntValue(Bytes.toInt(bytes));}

    static LongValue longVal(byte[] bytes){return new LongValue(Bytes.toLong(bytes));}


}
