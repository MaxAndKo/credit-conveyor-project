package com.konchalovmaxim.creditconveyorms.dto;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ScoringDataDTO {

    @Min(10000)
    @NotNull
    private BigDecimal amount;

    @Min(6)
    @NotNull
    private Integer term;

    @NotEmpty
    @Size(min = 2, max = 30)
    private String firstName;

    @NotEmpty
    @Size(min = 2, max = 30)
    private String lastName;

    @Size(min = 2, max = 30)
    private String middleName;

    @NotNull
    private Gender gender;

    @NotNull
    private LocalDate birthdate;

    @NotEmpty
    @Size(min = 4, max = 4)
    private String passportSeries;

    @NotEmpty
    @Size(min = 6, max = 6)
    private String passportNumber;

    @NotNull
    private LocalDate passportIssueDate;

    @NotNull
    private String passportIssueBranch;

    @NotNull
    private MartialStatus maritalStatus;

    @NotNull
    private Integer dependentAmount;

    @NotNull
    @Valid
    private EmploymentDTO employment;

    @NotNull
    private String account;

    @NotNull
    private Boolean isInsuranceEnabled;

    @NotNull
    private Boolean isSalaryClient;

    public enum Gender{
        МУЖЧИНА,
        ЖЕНЩИНА,
        НЕБИНАРНЫЙ
    }

    public enum MartialStatus{
        НЕ_В_ОТНОШЕНИЯХ,
        В_ОТНОШЕНИЯХ,
        РАЗВЕДЕН
    }

}
