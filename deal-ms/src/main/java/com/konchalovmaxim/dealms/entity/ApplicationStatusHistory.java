package com.konchalovmaxim.dealms.entity;

import com.konchalovmaxim.dealms.enums.ApplicationStatus;
import com.konchalovmaxim.dealms.enums.ChangeType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "t_status_history")
@Setter
@Getter
@NoArgsConstructor
@ToString(exclude = "application")
public class ApplicationStatusHistory {
    @Id
    @GeneratedValue(generator = "application_status_history_id_sequence")
    @SequenceGenerator(name = "application_status_history_id_sequence",
            sequenceName = "application_status_history_id_sequence", allocationSize = 1)
    private Long id;
    @Enumerated(value = EnumType.STRING)
    private ApplicationStatus status;
    private LocalDate time;
    @Enumerated(value = EnumType.STRING)
    private ChangeType changeType;
    @ManyToOne
    @JoinColumn(name = "application_id")
    private Application application;

    public ApplicationStatusHistory(ChangeType changeType, Application application) {
        this.application = application;
        this.status = application.getStatus();
        this.changeType = changeType;
        time = LocalDate.now();
    }
}
