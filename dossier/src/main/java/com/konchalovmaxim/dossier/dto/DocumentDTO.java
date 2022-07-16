package com.konchalovmaxim.dossier.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDTO {
    private String firstName;
    private String lastName;
    private BigDecimal amount;
}
