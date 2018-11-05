package me.libme.module.hbase;

/**
 * Created by J on 2018/11/5.
 */
public interface Value<T> {

    byte[] val();

    default T original(){return null;}

}
