package me.libme.module.hbase;

import org.apache.hadoop.hbase.util.Bytes;

/**
 * Created by J on 2018/11/5.
 */
public class LongValue extends BaseValue<Long> {

    private final long val;

    public LongValue(long val) {
        this.val = val;
    }

    @Override
    public byte[] val() {
        return Bytes.toBytes(val);
    }

    @Override
    public Long original() {
        return val;
    }
}
