package com.konchalovmaxim.dealms.repository;

import com.konchalovmaxim.dealms.entity.Passport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassportRepository extends JpaRepository<Passport, Long> {
    public Passport findPassportByPassportSeriesAndPassportNumber(String passportSeries, String passportNumber);
}
