package dac2dac.doctect.user.jwt;

import dac2dac.doctect.user.entity.constant.Gender;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {

        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm()); // SHA 256 알고리즘
    }

    // 검증하기
    public String getUsername(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }
    public String getId(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("id", String.class);
    }

    public String getPhoneNumber(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("PhoneNumber", String.class);
    }
    public String getEmail(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("email", String.class);
    }

    public String getUserType(String token) { // 사용자 타입 추가
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userType", String.class);
    }

    public String getOneLiner(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("oneLiner", String.class);
    }
    public Gender getGender(String token)
    {
        String genderString = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("gender", String.class);
        return Gender.valueOf(genderString.toUpperCase());

    }

    public String getBirthdDate(String token)
    {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("birthDate", String.class);

    }



    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }


    // JWT 토큰 생성하기
    //String token = jwtUtil.createJwt(username, email, phoneNumber,id, 60*60*10L);
    public String createJwt(String username,String id,String PhoneNumber, String email,String userType, String birthDate, Gender gender, Long expiredMs) {

        return Jwts.builder()
                .claim("id",id)
                .claim("email",email)
                .claim ("PhoneNumber", PhoneNumber)
                .claim("username", username)
                .claim("userType", userType) // 사용자 타입 추가
                .claim("birthDate", birthDate)
                .claim("gender",gender)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public String DoctorcreateJwt(String username,String id,String getOneLiner, String email, String userType, Long expiredMs) {

        return Jwts.builder()
                .claim("id",id)
                .claim("email",email)
                .claim ("oneLiner",getOneLiner)
                .claim("username", username)
                .claim("userType", userType) // 사용자 타입 추가
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }
}
