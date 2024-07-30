package dac2dac.doctect.noncontact_diag.entity;

import dac2dac.doctect.common.entity.BaseEntity;
import dac2dac.doctect.doctor.entity.Doctor;
import dac2dac.doctect.noncontact_diag.entity.constant.NoncontactDiagType;
import dac2dac.doctect.noncontact_diag.entity.constant.ReservationStatus;
import dac2dac.doctect.bootpay.entity.PaymentMethod;
import dac2dac.doctect.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoncontactDiagReservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @OneToOne
    @JoinColumn(name = "symptom_id")
    private Symptom symptom;

    @OneToOne
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;

    private LocalDate reservationDate;
    private LocalTime reservationTime;

    @Enumerated(EnumType.STRING)
    private NoncontactDiagType diagType;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private Boolean isConsent;

    @Builder
    public NoncontactDiagReservation(User user, Doctor doctor, Symptom symptom, PaymentMethod paymentMethod, LocalDate reservationDate, LocalTime reservationTime, NoncontactDiagType diagType,
        ReservationStatus status, Boolean isConsent) {
        this.user = user;
        this.doctor = doctor;
        this.symptom = symptom;
        this.paymentMethod = paymentMethod;
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
        this.diagType = diagType;
        this.status = status;
        this.isConsent = isConsent;
    }
}


