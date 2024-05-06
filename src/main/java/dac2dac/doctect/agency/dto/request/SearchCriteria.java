package dac2dac.doctect.agency.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchCriteria {

    @Min(-180)
    @Max(180)
    @NotNull(message = "longitude는 필수 값입니다.")
    private Double longitude;

    @Min(-90)
    @Max(90)
    @NotNull(message = "latitude는 필수 값입니다.")
    private Double latitude;

    @Size(min = 2, max = 20, message = "검색어는 2자 이상, 20자 이하로 입력해 주세요.")
    @Pattern(regexp = "^[가-힣ㄱ-ㅎㅏ-ㅣa-zA-Z0-9]+$", message = "검색어는 한글, 영문, 숫자만 포함할 수 있습니다.")
    private String keyword; // 검색어

    private boolean hospital; // 병원
    private boolean pharmacy; // 약국
    private boolean er; // 응급실

    private boolean openNow; // 현재 영업중
    private boolean openAllYear; // 연중 무휴
    private boolean openAtMidnight; // 야간 영업

    private boolean mon;
    private boolean tue;
    private boolean wed;
    private boolean thu;
    private boolean fri;
    private boolean sat;
    private boolean sun;
}
