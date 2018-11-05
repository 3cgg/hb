package me.libme.module.hbase;

import org.apache.hadoop.hbase.util.Bytes;

/**
 * Created by J on 2018/11/5.
 */
public class IntValue extends BaseValue<Integer> {

    private final int val;

    public IntValue(int val) {
        this.val = val;
    }

    @Override
    public byte[] val() {
        return Bytes.toBytes(val);
    }

    @Override
    public Integer original() {
        return val;
    }
}
