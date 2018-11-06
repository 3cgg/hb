package me.libme.module.hbase;

/**
 * Created by J on 2018/11/6.
 */
public interface IFilter {

    org.apache.hadoop.hbase.filter.Filter filter();

}
