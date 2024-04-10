package org.example.gatewaysession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.fastjson.JSON;

import java.util.Map;


@SpringBootTest
@Slf4j
public class GatewaySessionApplicationTests {
	@Autowired
	private RedisTemplate redisTemplate;

	String hashKey = "hashKey";
	String hashField = "hashField";
	String hashValue = "hashValue";

	@Test
	void testRedis() {
		redisTemplate.opsForHash().put(hashKey, hashField, hashValue);

		String hashValue = (String) redisTemplate.opsForHash().get(hashKey, hashField);
		log.info("hashValue: {}", hashValue);

		String sessionValue = (String) redisTemplate.opsForHash().get("spring:session:sessions:ce12db89-5b11-46e9-b0ea-175d31bd8815",
				"sessionAttr:sessionKey");
		log.info("sessionValue: {}", sessionValue);

		if (sessionValue != null && sessionValue.substring(1, sessionValue.length() - 1).equals("sessionValue")) {
			log.info("sessionValue is sessionValue");
		}
    }

	@Test
	void testRedisGet() throws JsonProcessingException {
		// String str = (String) redisTemplate.opsForHash().get("hashKey", "userInfo");
		String str = (String) redisTemplate.opsForValue().get("userInfo");
		log.info("testRedisGet userInfo:{}", str);

		// fastJson 解析方法
		Map maps = (Map) JSON.parse(str);
		log.info("maps:{}", maps);
		log.info("functions:{}", maps.get("functionList"));

		// jackson解析方法
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> userInfoMap = mapper.readValue(str, Map.class);
		log.info("maps:{}", userInfoMap);
		log.info("functions:{}", userInfoMap.get("functionList"));

//		List<String> functionList = (List<String>) string.get("functionList");
//		log.info("testRedisGet userInfo functionList:{}", functionList);
	}

	@Test
	void contextLoads() {
	}
}
