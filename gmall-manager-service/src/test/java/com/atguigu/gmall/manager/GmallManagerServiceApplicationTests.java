package com.atguigu.gmall.manager;

import com.atguigu.gmall.manager.mapper.BaseCatalog1Mapper;
import com.atguigu.gmall.manager.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.omg.CORBA.TIMEOUT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallManagerServiceApplicationTests {
	@Autowired
	UserMapper userMapper;
	@Autowired
	BaseCatalog1Mapper mapper;
	@Autowired
	CatalogService catalogService;
	@Autowired
	StringRedisTemplate stringRedisTemplate; //key-value 都是String的
	@Autowired
	JedisPool jedisPool;

	@Test
	public void TestJedis(){
		Jedis jedis = jedisPool.getResource();
		jedis.set("myredis","666");
		String s = jedis.get("zxcvbvb");
		System.out.println(s == null);
		String s1 = jedis.get("sku:60:info");
		System.out.println("null".equals(s1));
	}

	@Test
	public void testStringRedisTemplate(){
		/*//1、新建一个jedis连接池
		JedisPool jedisPool = new JedisPool();
		//2、从池中获取jedis客户端
		Jedis jedisClient = jedisPool.getResource();*/
		ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
		valueOperations.set("hello","world",200, TimeUnit.SECONDS);
		System.out.println("666666666666666666666");
		String hello = valueOperations.get("hello");
		System.out.println("返回的是："+hello);


	}




	@Test
	public void TestCatalogService(){
		List<BaseCatalog1> allBaseCatalog1 = catalogService.getAllBaseCatalog1();
		System.err.println("********************************************************");
		log.info("获取所有的一级分类信息：{}",allBaseCatalog1);
		System.err.println("********************************************************");
		List<BaseCatalog2> baseCatalog2ByC1Id = catalogService
				.getBaseCatalog2ByC1Id(allBaseCatalog1.get(0).getId());
		System.err.println("********************************************************");
		log.info("{}的二级分类信息：{}",allBaseCatalog1.get(0),baseCatalog2ByC1Id);
		System.err.println("********************************************************");
		List<BaseCatalog3> baseCatalog3ByC2Id = catalogService
				.getBaseCatalog3ByC2Id(baseCatalog2ByC1Id.get(0).getId());
		System.err.println("********************************************************");
		log.info("{}的三级分类信息：{}",baseCatalog2ByC1Id.get(0),baseCatalog3ByC2Id);
		System.err.println("********************************************************");

	}

	@Test
	public void BaseCatalog1MapperTeest() {
		BaseCatalog1 baseCatalog1 = new BaseCatalog1();
		baseCatalog1.setName("测试数据1");
		mapper.insert(baseCatalog1);
		log.info("数据保存成功!id是{},name是{}",baseCatalog1.getId(),baseCatalog1.getName());
	}

	@Test
	public void contextLoads() {
		for (User user : userMapper.selectList(null)) {
			System.out.println(user);
		}
		;
		System.out.println("***********************");
		//要让xml生效一定加上mybatis-plus.mapper-locations=classpath:mapper/*.xml
		User user = new User();
		user.setName("Jone");
		user.setAge(18);
		User userById = userMapper.getUserById(user);
		System.out.println(userById);
	}

}
