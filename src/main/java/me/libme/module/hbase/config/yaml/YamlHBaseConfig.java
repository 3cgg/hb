package me.libme.module.hbase.config.yaml;

import me.libme.kernel._c.yaml.YamlMapConfig;
import me.libme.module.hbase.HBaseConfig;
import me.libme.module.hbase.config.HBaseConfigFinder;
import org.apache.hadoop.hbase.HConstants;

import java.io.InputStream;

/**
 * Created by J on 2018/9/29.
 */
public class YamlHBaseConfig implements HBaseConfigFinder {

    private YamlMapConfig yamlMapConfig;

    public YamlHBaseConfig(InputStream inputStream) {
        this.yamlMapConfig = new YamlMapConfig(inputStream);
    }

    @Override
    public HBaseConfig find() {
        HBaseConfig hBaseConfig=new HBaseConfig();

        String zkHost=yamlMapConfig.getString(HConstants.ZOOKEEPER_QUORUM,"zk.3cgg.rec");
        int zkPort=yamlMapConfig.getInt(HConstants.ZOOKEEPER_CLIENT_PORT,2181);
        hBaseConfig.setZkHost(zkHost);
        hBaseConfig.setZkPort(zkPort);

        return hBaseConfig;
    }



























}
