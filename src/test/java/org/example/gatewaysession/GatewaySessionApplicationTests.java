package org.example.gatewaysession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.gatewaysession.entity.UserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
@Slf4j
public class GatewaySessionApplicationTests {
	@Autowired
	private RedisTemplate redisTemplate;

	String hashKey = "hashKey";
	String hashField = "hashField";
	String hashValue = "hashValue";

	String hashSessionKey = "spring:session:sessions:e202cd7e-ab9f-4d01-ba7d-2c8c87124b92";

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
	void testRedisSessionUSerInfo() throws JsonProcessingException {
		String sessionStudentValue = (String) redisTemplate.opsForHash().get(hashSessionKey, "sessionAttr:sessionKey");
		log.info("sessionStudentValue: {}", sessionStudentValue);

		ObjectMapper objectMapper = new ObjectMapper();
		UserInfo user = objectMapper.readValue(sessionStudentValue, UserInfo.class);
		log.info("UserInfo: {}", user);
	}

	@Test
	void contextLoads() {
	}
}
