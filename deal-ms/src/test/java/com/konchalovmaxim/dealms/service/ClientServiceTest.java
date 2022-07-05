package com.konchalovmaxim.dealms.service;

import com.konchalovmaxim.dealms.entity.Client;
import com.konchalovmaxim.dealms.entity.Passport;
import com.konchalovmaxim.dealms.repository.ClientRepository;
import com.konchalovmaxim.dealms.service.Impl.ClientServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ClientServiceTest {

    private ClientService clientService;
    private ClientRepository clientRepository;

    public ClientServiceTest() {
        clientRepository = mock(ClientRepository.class);
        clientService = new ClientServiceImpl(clientRepository);
    }

    @Test
    void loadContext(){
        Assertions.assertNotNull(clientRepository);
        Assertions.assertNotNull(clientService);
    }

    @Test
    void saveShouldInvokeRepositorySave(){
        when(clientRepository.save(any(Client.class))).thenReturn(new Client());

        Client client = new Client();
        clientService.save(client);

        verify(clientRepository).save(client);
    }

    @Test
    void saveOrReturnExistsShouldReturnClientFromDB(){
        Client client = new Client();
        String series = "1234";
        String number = "567890";
        when(clientRepository.findClientByPassportSeriesAndNumber(series, number)).thenReturn(client);
        Passport passport = new Passport();
        passport.setPassportSeries(series);
        passport.setPassportNumber(number);
        Client param = new Client();
        param.setPassport(passport);

        Assertions.assertEquals(client, clientService.saveOrReturnExists(param));
    }

    @Test
    void saveOrReturnExistsShouldReturnClientFromArgument(){
        Client client = new Client();
        String series = "1234";
        String number = "567890";
        when(clientRepository.findClientByPassportSeriesAndNumber(series, number)).thenReturn(client);
        Passport passport = new Passport();
        passport.setPassportSeries("0987");
        passport.setPassportNumber("654321");
        Client param = new Client();
        param.setPassport(passport);
        when(clientRepository.save(any(Client.class))).thenReturn(param);

        Assertions.assertEquals(param, clientService.saveOrReturnExists(param));
    }
}
