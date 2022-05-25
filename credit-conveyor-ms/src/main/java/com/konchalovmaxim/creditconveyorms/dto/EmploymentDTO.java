package com.konchalovmaxim.creditconveyorms.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
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
