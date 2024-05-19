package dac2dac.doctect.health_list.dto.response.healthScreening;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HealthScreeningItem {

    private LocalDateTime diagDate;

    private String doctorName;
    private String doctorHospital;

    @Builder
    public HealthScreeningItem(LocalDateTime diagDate, String doctorName, String doctorHospital) {
        this.diagDate = diagDate;
        this.doctorName = doctorName;
        this.doctorHospital = doctorHospital;
    }
}
