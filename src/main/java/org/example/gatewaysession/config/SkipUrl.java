package org.example.gatewaysession.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Configuration
@ConfigurationProperties(prefix = "white-url")
public class WhiteUrl {
    @Value("${white-url}")
    private List<String> urlList;
}
