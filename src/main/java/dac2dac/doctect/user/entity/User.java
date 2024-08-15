package dac2dac.doctect.user.entity;

import dac2dac.doctect.common.entity.BaseEntity;
import dac2dac.doctect.user.entity.constant.SocialType;
import jakarta.persistence.*;
import lombok.Getter;


@Entity
@Getter
@Table(name = "\"user\"")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;
    private String username;
    private String email;
    private String phoneNumber;
    private String code; // 구글, 카카오의 로그아웃, 탈퇴를 위한 코드
    private Double longitude;
    private Double latitude;
    private String password;


    public void setLocation(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    // 사용자 이름 설정
    public void setUsername(String username) {
        this.username = username;
    }

    // 암호 설정 (암호화 필요)
    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSocialType(SocialType socialType) {
        this.socialType = socialType;
    }


}
