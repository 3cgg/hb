package me.libme.module.hbase;

/**
 * Created by J on 2018/11/6.
 */
public class HBaseConfig {

    private String zkHost;

    private int zkPort;

    public String getZkHost() {
        return zkHost;
    }

    public void setZkHost(String zkHost) {
        this.zkHost = zkHost;
    }

    public int getZkPort() {
        return zkPort;
    }

    public void setZkPort(int zkPort) {
        this.zkPort = zkPort;
    }
}
