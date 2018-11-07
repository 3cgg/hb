package me.libme.module.hbase;

/**
 * Created by J on 2018/11/6.
 */
public abstract class Number2String {

    public static String long2LongString(long val){
        return long2LongString(val,String.valueOf(Long.MAX_VALUE).length());
    }


    public static String long2LongString(long val,int maxLength){

        int length=String.valueOf(val).length();
        StringBuffer stringBuffer=new StringBuffer();
        while (length++<maxLength){
            stringBuffer.append("0");
        }
        stringBuffer.append(String.valueOf(val));
        return stringBuffer.toString();
    }

}
