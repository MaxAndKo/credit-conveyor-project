package com.konchalovmaxim.dealms.service;

import com.konchalovmaxim.dealms.entity.Client;

public interface ClientService {
    public Client save(Client client);
    public Client saveOrReturnExists(Client client);

}
