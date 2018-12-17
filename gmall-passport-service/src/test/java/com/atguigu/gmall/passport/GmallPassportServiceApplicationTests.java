package com.atguigu.gmall.passport;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallPassportServiceApplicationTests {

	@Test
	public void contextLoads() {
		//e10adc3949ba59abbe56e057f20f883e
		String s = DigestUtils.md5Hex("123456");
		System.out.println(s);
	}

}
