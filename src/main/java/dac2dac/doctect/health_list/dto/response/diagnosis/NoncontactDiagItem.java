package dac2dac.doctect.health_list.dto.response.diagnosis;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class NoncontactDiagItem {

    private LocalDateTime diagDate;

    private String doctorName;
    private String doctorHostpital;
    private String doctorThumnail;

    private Double doctorAverageRating;
    private int reviewCnt;

    private Integer doctorTodayOpenTime;
    private Integer doctorTodayCloseTime;
    private boolean doctorIsOpenNow;

    @Builder
    public NoncontactDiagItem(LocalDateTime diagDate, String doctorName, String doctorHostpital, String doctorThumnail, Double doctorAverageRating, int reviewCnt, Integer doctorTodayOpenTime, Integer doctorTodayCloseTime, boolean doctorIsOpenNow) {
        this.diagDate = diagDate;
        this.doctorName = doctorName;
        this.doctorHostpital = doctorHostpital;
        this.doctorThumnail = doctorThumnail;
        this.doctorAverageRating = doctorAverageRating;
        this.reviewCnt = reviewCnt;
        this.doctorTodayOpenTime = doctorTodayOpenTime;
        this.doctorTodayCloseTime = doctorTodayCloseTime;
        this.doctorIsOpenNow = doctorIsOpenNow;
    }
}
