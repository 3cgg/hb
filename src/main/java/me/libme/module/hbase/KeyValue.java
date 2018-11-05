package me.libme.module.hbase;

import me.libme.kernel._c.util.Assert;
import me.libme.kernel._c.util.JStringUtils;

/**
 * Created by J on 2018/11/5.
 */
public class KeyValue {

    private String family;

    private String column;

    private Value value;

    private long version;

    public KeyValue(String family, String column) {
        this(family,column,null);
    }

    public KeyValue(String family, String column, Value value) {
        Assert.isTrue(JStringUtils.isNotNullOrEmpty(family),"family is empty.");
        Assert.isTrue(JStringUtils.isNotNullOrEmpty(column),"column is empty.");

        this.family = family;
        this.column = column;
        this.value = value;
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
}
