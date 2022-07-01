package com.konchalovmaxim.dealms.service;

import com.konchalovmaxim.dealms.entity.Application;

public interface ApplicationService {
    public Application save(Application application);
    public Application findById(Long id);
}
