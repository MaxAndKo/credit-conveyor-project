package com.konchalovmaxim.dealms.service;

import com.konchalovmaxim.dealms.entity.Application;
import com.konchalovmaxim.dealms.repository.ApplicationRepository;
import com.konchalovmaxim.dealms.service.Impl.ApplicationServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ApplicationServiceTest {

    private static final Long applicationId = 5L;
    private ApplicationService applicationService;
    private ApplicationRepository applicationRepository;

    public ApplicationServiceTest() {
        this.applicationRepository = mock(ApplicationRepository.class);
        this.applicationService = new ApplicationServiceImpl(applicationRepository);
    }

    @Test
    void contextLoad() {
        assertNotNull(applicationService);
        assertNotNull(applicationRepository);
    }

    @Test
    void saveShouldInvokeRepositorySave() {
        when(applicationRepository.save(any(Application.class))).thenReturn(new Application());

        Application application = new Application();
        applicationService.save(application);

        verify(applicationRepository).save(application);
    }

    @Test
    void findByIdShouldFindApplicationById() {
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(new Application()));
        Assertions.assertNotNull(applicationService.findById(applicationId));
    }

    @Test
    void findByIdShouldNotFindApplicationById() {
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(new Application()));
        Assertions.assertNull(applicationService.findById(4L));

    }

}
