package dac2dac.doctect.agency.entity.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AgencyType {
    HOSPITAL("병원"),
    PHARMACY("약국");

    private final String DiagTypeName;
}
