package me.libme.module.hbase;

import me.libme.kernel._c.util.Assert;
import me.libme.kernel._c.util.JStringUtils;

import java.math.BigDecimal;

/**
 * Created by J on 2018/11/5.
 */
public class KeyValue  {

    private String family;

    private String column;

    private Value value;

    private long version;

    public KeyValue(String family, String column,String value) {
        this(family,column,new StringValue(value));
    }

    public KeyValue(String family, String column,int value) {
        this(family,column,new IntValue(value));
    }

    public KeyValue(String family, String column,long value) {
        this(family,column,new LongValue(value));
    }

    public KeyValue(String family, String column,float value) {
        this(family,column,new FloatValue(value));
    }

    public KeyValue(String family, String column,double value) {
        this(family,column,new DoubleValue(value));
    }


    public KeyValue(String family, String column,BigDecimal value) {
        this(family,column,new BigDecimalValue(value));
    }

    public KeyValue(String family, String column, Value value) {
        this(family,column);
        this.value = value;
    }

    public KeyValue(String family, String column) {
        Assert.isTrue(JStringUtils.isNotNullOrEmpty(family),"family is empty.");
        Assert.isTrue(JStringUtils.isNotNullOrEmpty(column),"column is empty.");

        this.family = family;
        this.column = column;
    }



    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return family+":"+column+"=>"+value.toString();
    }
}
