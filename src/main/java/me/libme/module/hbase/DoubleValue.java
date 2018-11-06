package me.libme.module.hbase;

import org.apache.hadoop.hbase.util.Bytes;

/**
 * Created by J on 2018/11/5.
 */
public class DoubleValue extends BaseValue<Double> {

    private final double val;

    public DoubleValue(double val) {
        this.val = val;
    }

    @Override
    public byte[] val() {
        return Bytes.toBytes(val);
    }

    @Override
    public Double original() {
        return val;
    }
}
