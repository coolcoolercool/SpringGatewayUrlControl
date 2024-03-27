package org.example.gatewaysession.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "black-ip")
public class BlackIP {
    private List<String> IpList;
}
