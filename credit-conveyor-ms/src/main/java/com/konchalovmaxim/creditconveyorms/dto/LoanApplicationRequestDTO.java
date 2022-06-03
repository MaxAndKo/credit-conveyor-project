package com.konchalovmaxim.creditconveyorms.dto;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Validated
public class LoanApplicationRequestDTO {

    @Min(10000)
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

    @Pattern(regexp = "[\\w\\.]{2,50}@[\\w\\.]{2,20}")
    private String email;

    @NotNull
    private LocalDate birthdate;

    @NotEmpty
    @Size(min = 4, max = 4)
    private String passportSeries;

    @NotEmpty
    @Size(min = 6, max = 6)
    private String passportNumber;
}
