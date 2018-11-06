package me.libme.module.hbase;

/**
 * Created by J on 2018/11/6.
 */
public class Number2String {

    private String number2LongString(int val){
        String longVal=String.valueOf(Long.MAX_VALUE);
        int length=longVal.length()-String.valueOf(val).length();
        StringBuffer stringBuffer=new StringBuffer();
        while (length-->0){
            stringBuffer.append("0");
        }
        stringBuffer.append(String.valueOf(val));
        return stringBuffer.toString();
    }



}
