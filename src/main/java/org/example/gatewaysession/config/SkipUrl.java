package org.example.gatewaysession.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Component
@ConfigurationProperties(prefix = "skip-url")
public class SkipUrl {
    @Value("${skip-url.url-list}")
    private List<String> urlList;
}
