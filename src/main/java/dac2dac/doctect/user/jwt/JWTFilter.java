package dac2dac.doctect.user.jwt;

import dac2dac.doctect.user.dto.CustomUserDetails;
import dac2dac.doctect.user.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

// JWT 검증 필터
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(JWTFilter.class);

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // Request에서 Authorization 헤더를 찾음
            String authorization = request.getHeader("Authorization");
            System.out.println("Authorization Header"+ authorization);

            // Authorization 헤더 검증
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                System.out.println("여기로 들어오면 안되는데");
                System.out.println("Authoriazion"+ authorization);
                filterChain.doFilter(request, response);
                return;
            }

            logger.debug("Bearer token found in Authorization header.");

            // Bearer 부분 제거 후 순수 토큰만 획득
            String token = authorization.substring(7); // Bearer 다음부터 토큰
            System.out.println("token::" + token);

            // 토큰 소멸 시간 검증
            if (jwtUtil.isExpired(token)) {
                System.out.println("Token이 만료됨");
                filterChain.doFilter(request, response);
                return;
            }

            System.out.println("Token is valid and not expired.");

            // 토큰에서 username과 id 획득
            String username = jwtUtil.getUsername(token);


            System.out.println("Token parsed successfully. Username: {}, ID: {}"+username);

            // UserEntity를 생성하여 값 set
            User userEntity = new User();
            userEntity.setUsername(username);

            // UserDetails에 회원 정보 객체 담기
            CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

            // 스프링 시큐리티 인증 토큰 생성
            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

            // 세션에 사용자 등록
            SecurityContextHolder.getContext().setAuthentication(authToken);

            System.out.println("User authenticated and set in SecurityContext.");

        } catch (Exception e) {
            logger.error("Error processing JWT token: {}", e.getMessage(), e);
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
}
