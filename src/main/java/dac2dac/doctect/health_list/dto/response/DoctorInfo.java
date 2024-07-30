package dac2dac.doctect.health_list.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DoctorInfo {

    private LocalDateTime diagDate;

    private String doctorName;
    private String doctorHospital;

    @Builder
    public DoctorInfo(LocalDateTime diagDate, String doctorName, String doctorHospital) {
        this.diagDate = diagDate;
        this.doctorName = doctorName;
        this.doctorHospital = doctorHospital;
    }
}
