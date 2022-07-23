package com.konchalovmaxim.dealms.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "t_passports")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Passport {

    @Id
    @GeneratedValue(generator = "passport_id_sequence")
    @SequenceGenerator(name = "passport_id_sequence",
            sequenceName = "passport_id_sequence", allocationSize = 1)
    private Long id;
    private String passportSeries;
    private String passportNumber;
    private LocalDate passportIssueDate;
    private String passportIssueBranch;

}
