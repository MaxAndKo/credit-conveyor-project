package com.konchalovmaxim.dealms.dto;

import com.konchalovmaxim.dealms.entity.*;
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


    public DocumentDTO(Application application) {
        this.amount = application.getCredit().getAmount();
        this.firstName = application.getClient().getFirstName();
        this.lastName = application.getClient().getLastName();
    }
}
