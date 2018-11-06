package me.libme.module.hbase;

import org.apache.hadoop.hbase.util.Bytes;

import java.math.BigDecimal;

/**
 * Created by J on 2018/11/5.
 */
public class BigDecimalValue extends BaseValue<BigDecimal> {

    private final BigDecimal val;

    public BigDecimalValue(BigDecimal val) {
        this.val = val;
    }

    @Override
    public byte[] val() {
        return Bytes.toBytes(val);
    }

    @Override
    public BigDecimal original() {
        return val;
    }
}
