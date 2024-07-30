package dac2dac.doctect.noncontact_diag.entity.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NoncontactDiagType {
    VOICE("음성진료"),
    VIDEO("화상진료");

    private final String NoncontactDiagType;
}
