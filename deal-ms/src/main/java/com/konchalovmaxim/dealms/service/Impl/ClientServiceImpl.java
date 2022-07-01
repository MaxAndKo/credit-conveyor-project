package com.konchalovmaxim.dealms.service.Impl;

import com.konchalovmaxim.dealms.entity.Client;
import com.konchalovmaxim.dealms.repository.ClientRepository;
import com.konchalovmaxim.dealms.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repository;

    @Override
    public Client save(Client client) {
        return repository.save(client);
    }

    @Override
    public Client saveOrReturnExists(Client client) {
        Client DbClient = repository.findClientByPassportSeriesAndNumber(
                client.getPassport().getPassportSeries(),
                client.getPassport().getPassportNumber());

        if (DbClient == null){
            return save(client);
        }
        else {
            return DbClient;
        }
    }


}
