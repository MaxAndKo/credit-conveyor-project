package com.konchalovmaxim.dealms.service.Impl;

import com.konchalovmaxim.dealms.entity.Application;
import com.konchalovmaxim.dealms.repository.ApplicationRepository;
import com.konchalovmaxim.dealms.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository repository;

    @Override
    public Application save(Application application) {
        return repository.save(application);
    }

    @Override
    public Application findById(Long id) {
        Optional<Application> optional = repository.findById(id);
        if (optional.isPresent())
            return optional.get();
        else
            return null;
    }
}
