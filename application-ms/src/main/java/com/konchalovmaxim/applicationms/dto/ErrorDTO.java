package com.konchalovmaxim.applicationms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ErrorDTO {
    private Date timestamp;
    private String status;
    private String error;
}
