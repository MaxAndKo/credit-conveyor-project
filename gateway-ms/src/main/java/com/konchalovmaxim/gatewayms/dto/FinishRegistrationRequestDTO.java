package com.konchalovmaxim.gatewayms.dto;

import com.konchalovmaxim.gatewayms.enums.Gender;
import com.konchalovmaxim.gatewayms.enums.MartialStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FinishRegistrationRequestDTO {

    @NotNull
    private Gender gender;
    @NotNull
    private MartialStatus maritalStatus;
    @NotNull
    @Min(0)
    private Integer dependentAmount;
    @NotNull
    private LocalDate passportIssueDate;
    @NotEmpty
    private String passportIssueBranch;
    @NotNull
    private EmploymentDTO employment;
    @NotEmpty
    private String account;
}
