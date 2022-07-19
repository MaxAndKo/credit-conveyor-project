package com.konchalovmaxim.dealms.service;

import com.konchalovmaxim.dealms.entity.Application;

import java.util.List;

public interface ApplicationService {
    Application save(Application application);
    Application findById(Long id);

    List<Application> findAll();
}
