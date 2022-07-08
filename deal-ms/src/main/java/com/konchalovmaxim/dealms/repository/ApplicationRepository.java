package com.konchalovmaxim.dealms.repository;

import com.konchalovmaxim.dealms.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
}
