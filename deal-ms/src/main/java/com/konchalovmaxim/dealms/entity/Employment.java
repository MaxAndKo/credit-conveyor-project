package com.konchalovmaxim.dealms.entity;

import com.konchalovmaxim.dealms.dto.EmploymentDTO;
import com.konchalovmaxim.dealms.enums.EmploymentPosition;
import com.konchalovmaxim.dealms.enums.EmploymentStatus;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "t_employments")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Employment {
    @Id
    @GeneratedValue(generator = "employment_id_sequence")
    @SequenceGenerator(name = "employment_id_sequence",
            sequenceName = "employment_id_sequence", allocationSize = 1)
    private Long id;
    @Enumerated(EnumType.STRING)
    private EmploymentStatus employmentStatus;
    private String employer;
    private BigDecimal salary;
    @Enumerated(EnumType.STRING)
    private EmploymentPosition position;
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;

    public Employment(EmploymentDTO employmentDTO) {
        this.employmentStatus = employmentDTO.getEmploymentStatus();
        this.employer = employmentDTO.getEmployerINN();
        this.salary = employmentDTO.getSalary();
        this.position = employmentDTO.getPosition();
        this.workExperienceTotal = employmentDTO.getWorkExperienceTotal();
        this.workExperienceCurrent = employmentDTO.getWorkExperienceCurrent();
    }
}
