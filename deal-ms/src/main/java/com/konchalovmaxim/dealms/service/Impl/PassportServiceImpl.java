package com.konchalovmaxim.dealms.service.Impl;

import com.konchalovmaxim.dealms.entity.Passport;
import com.konchalovmaxim.dealms.repository.PassportRepository;
import com.konchalovmaxim.dealms.service.PassportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PassportServiceImpl implements PassportService {

    private final PassportRepository repository;

    @Override
    public boolean notExistWithSeriesAndNumber(String passportSeries, String passportNumber) {
        return null == repository.findPassportByPassportSeriesAndPassportNumber(passportSeries, passportNumber);
    }
}
