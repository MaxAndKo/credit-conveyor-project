package com.konchalovmaxim.dealms.service;

import com.konchalovmaxim.dealms.entity.Client;

public interface ClientService {
    Client save(Client client);
    Client saveOrReturnExists(Client client);

}
