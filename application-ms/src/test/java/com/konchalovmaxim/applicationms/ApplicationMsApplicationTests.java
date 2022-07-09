package com.konchalovmaxim.applicationms;

import com.konchalovmaxim.applicationms.config.HttpProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnableConfigurationProperties(HttpProperties.class)
class ApplicationMsApplicationTests {

    @Test
    void contextLoads() {
    }

}
