package com.konchalovmaxim.dealms.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "t_passports")
@Setter
@Getter
@NoArgsConstructor
public class Passport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String passportSeries;        //TODO сделать так, чтобы сочетание полей серии и номера было уникальным
    private String passportNumber;
    private LocalDate passportIssueDate;
    private String passportIssueBranch;

}
