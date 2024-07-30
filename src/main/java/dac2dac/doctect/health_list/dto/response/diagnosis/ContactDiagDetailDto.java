package dac2dac.doctect.health_list.dto.response.diagnosis;

import dac2dac.doctect.health_list.dto.response.HostpitalInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ContactDiagDetailDto {

    private HostpitalInfo agencyInfo;
    private DiagDetailInfo diagDetailInfo;

    @Builder
    public ContactDiagDetailDto(HostpitalInfo agencyInfo, DiagDetailInfo diagDetailInfo) {
        this.agencyInfo = agencyInfo;
        this.diagDetailInfo = diagDetailInfo;
    }
}
