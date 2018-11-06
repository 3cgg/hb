package me.libme.module.hbase;

import me.libme.module.hbase.config.yaml.YamlHBaseConfig;

/**
 * Created by J on 2018/9/29.
 */
public class HBaseExecutor {

    private static HBaseConnector.HBaseExecutor executor;


    public static HBaseConnector.HBaseExecutor defaultExecutor(){

        if(executor==null){
            synchronized (HBaseExecutor.class){
                if(executor==null){
                    HBaseConfig hBaseConfig=new YamlHBaseConfig(Thread.currentThread().getContextClassLoader()
                            .getResourceAsStream("application-cpp-hbase.yml")).find();
                    HBaseExecutor.executor=executor(hBaseConfig);
                }
            }
        }
        return executor;
    }


    public static HBaseConnector.HBaseExecutor executor(HBaseConfig hBaseConfig){
        return new HBaseConnector(hBaseConfig).connect();
    }


}
