package com.konchalovmaxim.creditconveyorms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "http")
@Data
public class HttpProperties {
    private List<String> excludeUrls;

}
