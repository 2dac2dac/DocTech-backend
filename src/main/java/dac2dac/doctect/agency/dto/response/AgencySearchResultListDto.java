package dac2dac.doctect.agency.dto.response;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AgencySearchResultListDto {

    private int totalCnt;
    private List<AgencySearchResultDto> agencySearchResultList = new ArrayList<>();

    @Builder
    public AgencySearchResultListDto(int totalCnt, List<AgencySearchResultDto> agencySearchResultList) {
        this.totalCnt = totalCnt;
        this.agencySearchResultList = agencySearchResultList;
    }

}
