package com.atguigu.gmall.manager;

import org.junit.Test;
import org.apache.commons.codec.digest.DigestUtils;
public class MD5Test {
    @Test
    public void test1(){
        //e10adc3949ba59abbe56e057f20f883e
        String s = DigestUtils.md5Hex("123456");
        System.out.println(s);
    }
}
