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
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String tel;
    private boolean isErOperate;

    private Double longitude;
    private Double latitude;

    private Integer diagTimeMonOpen;
    private Integer diagTimeMonClose;

    private Integer diagTimeTuesOpen;
    private Integer diagTimeTuesClose;

    private Integer diagTimeWedsOpen;
    private Integer diagTimeWedsClose;

    private Integer diagTimeThursOpen;
    private Integer diagTimeThursClose;

    private Integer diagTimeFriOpen;
    private Integer diagTimeFriClose;

    private Integer diagTimeSatOpen;
    private Integer diagTimeSatClose;

    private Integer diagTimeSunOpen;
    private Integer diagTimeSunClose;

    private Integer diagTimeHolidayOpen;
    private Integer diagTimeHolidayClose;

    @Builder
    public Hospital(String name, String address, String tel, Boolean isErOperate, Double longitude, Double latitude, Integer diagTimeMonOpen, Integer diagTimeMonClose, Integer diagTimeTuesOpen,
        Integer diagTimeTuesClose, Integer diagTimeWedsOpen, Integer diagTimeWedsClose, Integer diagTimeThursOpen, Integer diagTimeThursClose, Integer diagTimeFriOpen, Integer diagTimeFriClose,
        Integer diagTimeSatOpen, Integer diagTimeSatClose, Integer diagTimeSunOpen, Integer diagTimeSunClose, Integer diagTimeHolidayOpen, Integer diagTimeHolidayClose) {
        this.name = name;
        this.address = address;
        this.tel = tel;
        this.isErOperate = isErOperate();
        this.longitude = longitude;
        this.latitude = latitude;
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
