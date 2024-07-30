package dac2dac.doctect.health_list.dto.response.healthScreening;

import dac2dac.doctect.health_list.dto.response.DoctorInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HealthScreeningDetailDto {

    DoctorInfo doctorInfo;

    HealthScreeningInfo healthScreeningInfo;
    MeasurementTestInfo measurementTestInfo;
    BloodTestInfo bloodTestInfo;
    OtherTestInfo otherTestInfo;

    @Builder
    public HealthScreeningDetailDto(DoctorInfo doctorInfo, HealthScreeningInfo healthScreeningInfo, MeasurementTestInfo measurementTestInfo, BloodTestInfo bloodTestInfo, OtherTestInfo otherTestInfo) {
        this.doctorInfo = doctorInfo;
        this.healthScreeningInfo = healthScreeningInfo;
        this.measurementTestInfo = measurementTestInfo;
        this.bloodTestInfo = bloodTestInfo;
        this.otherTestInfo = otherTestInfo;
    }
}
