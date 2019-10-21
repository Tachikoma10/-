package com.sanriyue.scw;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ScwProjectApplicationTests {

	@Autowired
	DataSource dataSource;
	@Autowired
	StringRedisTemplate stringRedisTemplate;
	@Test
	public void TestDataSource() throws SQLException {
		System.out.println(dataSource.getClass());
		Connection connection = dataSource.getConnection();
		System.out.println(connection);
		connection.close();
	}
	@Test
	public void TestRedis() {
		stringRedisTemplate.opsForValue().set("武当", "张三");
	}

}
