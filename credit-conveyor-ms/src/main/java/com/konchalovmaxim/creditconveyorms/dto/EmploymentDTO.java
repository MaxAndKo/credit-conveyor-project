package com.konchalovmaxim.creditconveyorms.dto;

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
    private Position position;
    @NotNull
    private Integer workExperienceTotal;
    @NotNull
    private Integer workExperienceCurrent;

    public enum EmploymentStatus{
        БЕЗРАБОТНЫЙ,
        САМОЗАНЯТЫЙ,
        ВЛАДЕЛЕЦ_БИЗНЕСА
    }

    public enum Position{
        МЕНЕДЖЕР,
        ТОП_МЕНЕДЖЕР
    }

}
