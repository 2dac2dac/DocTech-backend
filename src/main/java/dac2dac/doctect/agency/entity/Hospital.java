package dac2dac.doctect.agency.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hospital extends Agency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String tel;
    private String diagDiv;
    private boolean isErOperate;

    private Double longitude;
    private Double latitude;

    private String hpid;

    @Builder
    public Hospital(String name, String address, String tel, String diagDiv, Boolean isErOperate, Double longitude, Double latitude, String hpid, Integer diagTimeMonOpen, Integer diagTimeMonClose,
        Integer diagTimeTuesOpen, Integer diagTimeTuesClose, Integer diagTimeWedsOpen, Integer diagTimeWedsClose, Integer diagTimeThursOpen, Integer diagTimeThursClose, Integer diagTimeFriOpen,
        Integer diagTimeFriClose, Integer diagTimeSatOpen, Integer diagTimeSatClose, Integer diagTimeSunOpen, Integer diagTimeSunClose, Integer diagTimeHolidayOpen, Integer diagTimeHolidayClose) {
        this.name = name;
        this.address = address;
        this.tel = tel;
        this.diagDiv = diagDiv;
        this.isErOperate = isErOperate;
        this.longitude = longitude;
        this.latitude = latitude;
        this.hpid = hpid;
        this.diagTimeMonOpen = diagTimeMonOpen;
        this.diagTimeMonClose = diagTimeMonClose;
        this.diagTimeTuesOpen = diagTimeTuesOpen;
        this.diagTimeTuesClose = diagTimeTuesClose;
        this.diagTimeWedsOpen = diagTimeWedsOpen;
        this.diagTimeWedsClose = diagTimeWedsClose;
        this.diagTimeThursOpen = diagTimeThursOpen;
        this.diagTimeThursClose = diagTimeThursClose;
        this.diagTimeFriOpen = diagTimeFriOpen;
        this.diagTimeFriClose = diagTimeFriClose;
        this.diagTimeSatOpen = diagTimeSatOpen;
        this.diagTimeSatClose = diagTimeSatClose;
        this.diagTimeSunOpen = diagTimeSunOpen;
        this.diagTimeSunClose = diagTimeSunClose;
        this.diagTimeHolidayOpen = diagTimeHolidayOpen;
        this.diagTimeHolidayClose = diagTimeHolidayClose;
    }

}
