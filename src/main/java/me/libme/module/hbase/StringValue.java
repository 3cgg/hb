package me.libme.module.hbase;

import org.apache.hadoop.hbase.util.Bytes;

/**
 * Created by J on 2018/11/5.
 */
public class StringValue extends BaseValue<String> {

    private final String val;

    public StringValue(String val) {
        this.val = val;
    }

    @Override
    public byte[] val() {
        return Bytes.toBytes(val);
    }

    @Override
    public String original() {
        return val;
    }
}
