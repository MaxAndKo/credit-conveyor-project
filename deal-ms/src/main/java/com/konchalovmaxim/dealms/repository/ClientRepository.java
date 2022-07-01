package com.konchalovmaxim.dealms.repository;

import com.konchalovmaxim.dealms.entity.Client;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    @Query(value = "from Client c where c.passport.passportNumber = :number and c.passport.passportSeries = :series")
    public Client findClientByPassportSeriesAndNumber(@Param("series") String series, @Param("number") String number);
}
