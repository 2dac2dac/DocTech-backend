package dac2dac.doctect.health_list.dto.response.diagnosis;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoncontactDiagItem {

    private Long diagId;

    private LocalDateTime diagDate;

    private String doctorName;
    private String doctorHostpital;
    private String doctorThumnail;

    private Integer doctorTodayOpenTime;
    private Integer doctorTodayCloseTime;
    private boolean doctorIsOpenNow;

    @Builder
    public NoncontactDiagItem(Long diagId, LocalDateTime diagDate, String doctorName, String doctorHostpital, String doctorThumnail, Integer doctorTodayOpenTime, Integer doctorTodayCloseTime, boolean doctorIsOpenNow) {
        this.diagId = diagId;
        this.diagDate = diagDate;
        this.doctorName = doctorName;
        this.doctorHostpital = doctorHostpital;
        this.doctorThumnail = doctorThumnail;
        this.doctorTodayOpenTime = doctorTodayOpenTime;
        this.doctorTodayCloseTime = doctorTodayCloseTime;
        this.doctorIsOpenNow = doctorIsOpenNow;
    }
}
