package com.konchalovmaxim.dealms.entity;

import com.konchalovmaxim.dealms.enums.ApplicationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "t_applications")
@NoArgsConstructor
@Getter
@Setter
public class Application {
    @Id
    @GeneratedValue(generator = "application_id_sequence")
    @SequenceGenerator(
            name = "application_id_sequence",
            sequenceName = "application_id_sequence",
            allocationSize = 1)
    private Long id;
    @ManyToOne
    private Client client;
    @OneToOne
    private Credit credit;
    @Enumerated(value = EnumType.STRING)
    private ApplicationStatus status;
    private LocalDate creationDate;
    @OneToOne(cascade = CascadeType.ALL)
    private LoanOffer loanOffer;
    private LocalDate singDate;
    private String sesCode;
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL)
    private List<ApplicationStatusHistory> statusHistories;


    public Application(Client client) {
        this.client = client;
    }
}
