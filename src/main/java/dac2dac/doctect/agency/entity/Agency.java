package dac2dac.doctect.agency.entity;

import dac2dac.doctect.agency.entity.constant.AgencyType;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Agency {

    protected String name;
    protected String address;
    protected String tel;

    protected Double longitude;
    protected Double latitude;

    protected Integer diagTimeMonOpen;
    protected Integer diagTimeMonClose;

    protected Integer diagTimeTuesOpen;
    protected Integer diagTimeTuesClose;

    protected Integer diagTimeWedsOpen;
    protected Integer diagTimeWedsClose;

    protected Integer diagTimeThursOpen;
    protected Integer diagTimeThursClose;

    protected Integer diagTimeFriOpen;
    protected Integer diagTimeFriClose;

    protected Integer diagTimeSatOpen;
    protected Integer diagTimeSatClose;

    protected Integer diagTimeSunOpen;
    protected Integer diagTimeSunClose;

    protected Integer diagTimeHolidayOpen;
    protected Integer diagTimeHolidayClose;

    public abstract AgencyType getAgencyType();

}
