package dac2dac.doctect.noncontact_diag.entity.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationStatus {
    SIGN_UP("예약신청완료"),
    CANCLE("예약취소"),
    COMPLETE("예약완료");

    private final String ReservationStatus;
}
