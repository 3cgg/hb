package me.libme.module.hbase;

/**
 * Created by J on 2018/11/5.
 */
public abstract class BaseValue<T> implements Value<T> {

    @Override
    public String toString() {
        return original().toString();
    }


}
