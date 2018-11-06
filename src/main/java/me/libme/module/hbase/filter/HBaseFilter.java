package me.libme.module.hbase.filter;

import me.libme.module.hbase.IFilter;
import org.apache.hadoop.hbase.filter.Filter;

/**
 * Created by J on 2018/11/6.
 */
public class HBaseFilter implements IFilter {

    private final Filter filter;

    public HBaseFilter(Filter filter) {
        this.filter = filter;
    }

    @Override
    public Filter filter() {
        return filter;
    }



}
