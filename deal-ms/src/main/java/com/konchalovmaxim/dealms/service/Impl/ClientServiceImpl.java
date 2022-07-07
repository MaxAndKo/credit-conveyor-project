package com.konchalovmaxim.dealms.service.Impl;

import com.konchalovmaxim.dealms.entity.Client;
import com.konchalovmaxim.dealms.repository.ClientRepository;
import com.konchalovmaxim.dealms.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repository;

    @Override
    public Client save(Client client) {
        log.debug("Client for save: {}", client);

        Client clientFromDb = repository.save(client);
        log.debug("Saved client: {}", clientFromDb);
        return clientFromDb;

    }

    @Override
    public Client saveOrReturnExists(Client client) {
        log.debug("Incoming client for save: {}", client);
        Client clientFromDb = repository.findClientByPassportSeriesAndNumber(
                client.getPassport().getPassportSeries(),
                client.getPassport().getPassportNumber());
        log.debug("Founded client in DB: {}", clientFromDb);

        if (clientFromDb == null){
            log.debug("Returns incoming client");
            return save(client);
        }
        else {
            log.debug("Returns client from DB");
            return clientFromDb;
        }
    }

}
