package com.konchalovmaxim.creditconveyorms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "not.log")
@Data
public class HttpProperties {
    private String apiDocs;
    private String swagger;

}
