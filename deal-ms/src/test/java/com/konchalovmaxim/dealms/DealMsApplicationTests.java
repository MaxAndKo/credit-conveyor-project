package com.konchalovmaxim.dealms;

import com.konchalovmaxim.dealms.entity.Client;
import com.konchalovmaxim.dealms.entity.Passport;
import com.konchalovmaxim.dealms.service.ClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DealMsApplicationTests {

    @Autowired
    private ClientService clientService;

    @Test
    void contextLoads() {
    }

    @Test
    void test(){
        Client c = new Client();
        Passport passport = new Passport();
        passport.setPassportSeries("1234");
        passport.setPassportNumber("123476");
        c.setPassport(passport);
        System.out.println(clientService.saveOrReturnExists(c));
    }

}
