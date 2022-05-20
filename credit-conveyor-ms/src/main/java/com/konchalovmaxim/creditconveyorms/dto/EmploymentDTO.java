package com.konchalovmaxim.creditconveyorms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentDTO {

    private EmploymentStatus employmentStatus;
    private String employerINN;
    private BigDecimal salary;
    private Position position;
    private Integer workExperienceTotal;
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
