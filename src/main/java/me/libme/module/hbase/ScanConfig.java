package me.libme.module.hbase;

/**
 * Created by J on 2018/11/7.
 */
public class ScanConfig {


    private int cacheing;

    private int batch;

    public int getCacheing() {
        return cacheing;
    }

    public void setCacheing(int cacheing) {
        this.cacheing = cacheing;
    }

    public int getBatch() {
        return batch;
    }

    public void setBatch(int batch) {
        this.batch = batch;
    }
}
