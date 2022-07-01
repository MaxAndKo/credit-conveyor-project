package com.konchalovmaxim.dealms.entity;

import com.konchalovmaxim.dealms.dto.LoanApplicationRequestDTO;
import com.konchalovmaxim.dealms.enums.MartialStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "t_clients")
@ToString
public class Client {

    @Id
    @GeneratedValue(generator = "client_id_sequence")
    @SequenceGenerator(name = "client_id_sequence", sequenceName = "client_id_sequence", allocationSize = 1)
    private Long id;

    private String firstName;

    private String lastName;

    private String middleName;

    private String email;

    private LocalDate birthdate;

    @Enumerated(EnumType.STRING)
    private MartialStatus maritalStatus;

    private Integer dependentAmount;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "passport_id")
    private Passport passport;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "employment_id")
    private Employment employment;

    private String account;

    public Client(LoanApplicationRequestDTO requestDTO) {
        firstName=requestDTO.getFirstName();
        lastName= requestDTO.getLastName();
        middleName= requestDTO.getMiddleName();
        email= requestDTO.getEmail();
        birthdate=requestDTO.getBirthdate();
        Passport InPassport = new Passport();
        InPassport.setPassportNumber(requestDTO.getPassportNumber());
        InPassport.setPassportSeries(requestDTO.getPassportSeries());
        passport = InPassport;
    }
}