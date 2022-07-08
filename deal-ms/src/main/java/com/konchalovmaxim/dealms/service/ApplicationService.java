package com.konchalovmaxim.dealms.service;

import com.konchalovmaxim.dealms.entity.Application;

public interface ApplicationService {
    Application save(Application application);
    Application findById(Long id);
}
