package org.example.gatewaysession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.gatewaysession.config.AuthConfig;
import org.example.gatewaysession.util.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.fastjson.JSON;

import java.util.*;


@SpringBootTest
@Slf4j
public class GatewaySessionApplicationTests {
	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private AuthConfig authConfig;

	String hashKey = "hashKey";
	String hashField = "hashField";
	String hashValue = "hashValue";

	final String JWT_REDIS_KEY = "jwt_redis_key";

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
	void testListDisjoint() {
		List<String> firstList = Arrays.asList("teacher", "worker", "student");
		List<String> secondList = Arrays.asList("user", "admin");

		if (Collections.disjoint(firstList, secondList)) {
			System.out.println("Collections.disjoint方法：firstList、secondList没有交集");
		}

		List<String> thirdList = Arrays.asList("user", "bbb");;
		if (!Collections.disjoint(secondList, thirdList)) {
			System.out.println("Collections.disjoint方法：firstList、secondList有交集");
		}
	}

	@Test
	void testGenJwt() {
		String jwtStr = JwtUtils.createToken(JWT_REDIS_KEY, "userInfo", authConfig.jwtSecretKey);
		log.info("testGenJwt jwtStr:{}", jwtStr);
		String userInfo = JwtUtils.verifyToken(jwtStr, JWT_REDIS_KEY, authConfig.jwtSecretKey);
		log.info("userInfo:{}", userInfo);
	}

	@Test
	void contextLoads() {
	}
}
