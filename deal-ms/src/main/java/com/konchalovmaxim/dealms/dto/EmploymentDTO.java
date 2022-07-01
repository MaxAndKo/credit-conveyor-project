package com.konchalovmaxim.dealms.dto;

import com.konchalovmaxim.dealms.enums.EmploymentPosition;
import com.konchalovmaxim.dealms.enums.EmploymentStatus;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EmploymentDTO {

    @NotNull
    private EmploymentStatus employmentStatus;
    @NotEmpty
    private String employerINN;
    @NotNull
    private BigDecimal salary;
    @NotNull
    private EmploymentPosition position;
    @NotNull
    private Integer workExperienceTotal;
    @NotNull
    private Integer workExperienceCurrent;

}
