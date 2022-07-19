package com.konchalovmaxim.dealms.service.Impl;

import com.konchalovmaxim.dealms.entity.Application;
import com.konchalovmaxim.dealms.repository.ApplicationRepository;
import com.konchalovmaxim.dealms.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository repository;

    @Override
    public Application save(Application application) {
        log.debug("Application for save: {}", application);
        Application applicationFromDB = repository.save(application);
        log.debug("Saved application: {}", applicationFromDB);

        return applicationFromDB;
    }

    @Override
    public Application findById(Long id) {
        log.debug("Finding application with id: {}", id);
        Optional<Application> optional = repository.findById(id);
        Application founded = optional.orElse(null);
        log.debug("Founded application: {}", founded);
        return founded;
    }

    @Override
    public List<Application> findAll(){
        return repository.findAll();
    }
}
