package com.konchalovmaxim.creditconveyorms.dto;

import com.konchalovmaxim.creditconveyorms.enums.EmploymentPosition;
import com.konchalovmaxim.creditconveyorms.enums.EmploymentStatus;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
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
