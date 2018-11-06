package me.libme.module.hbase;

import org.apache.hadoop.hbase.util.Bytes;

/**
 * Created by J on 2018/11/5.
 */
public class FloatValue extends BaseValue<Float> {

    private final float val;

    public FloatValue(float val) {
        this.val = val;
    }

    @Override
    public byte[] val() {
        return Bytes.toBytes(val);
    }

    @Override
    public Float original() {
        return val;
    }
}
