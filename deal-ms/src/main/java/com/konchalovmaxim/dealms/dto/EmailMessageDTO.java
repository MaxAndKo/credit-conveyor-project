package com.konchalovmaxim.dealms.dto;

import com.konchalovmaxim.dealms.enums.Theme;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailMessageDTO {
    private String address;
    private Theme theme;
    private Long applicationId;
}
