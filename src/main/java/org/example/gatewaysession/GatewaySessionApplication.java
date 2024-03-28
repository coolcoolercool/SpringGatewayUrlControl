package org.example.gatewaysession;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;

@SpringBootApplication
@EnableRedisWebSession
//@EnableDiscoveryClient
@ComponentScan(basePackages = {"org.example.gatewaysession.config", "org.example.gatewaysession.filter"} )
public class GatewaySessionApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewaySessionApplication.class, args);
	}

}
