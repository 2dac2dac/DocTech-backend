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
            logger.debug("Authorization Header: {}", authorization);

            // Authorization 헤더 검증
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                logger.debug("No Bearer token found in Authorization header.");
                filterChain.doFilter(request, response);
                return;
            }

            logger.debug("Bearer token found in Authorization header.");

            // Bearer 부분 제거 후 순수 토큰만 획득
            String token = authorization.substring(7); // Bearer 다음부터 토큰

            // 토큰 소멸 시간 검증
            if (jwtUtil.isExpired(token)) {
                logger.warn("Token has expired.");
                filterChain.doFilter(request, response);
                return;
            }

            logger.debug("Token is valid and not expired.");

            // 토큰에서 username과 id 획득
            String username = jwtUtil.getUsername(token);
            Long id = jwtUtil.getId(token);

            logger.debug("Token parsed successfully. Username: {}, ID: {}", username, id);

            // UserEntity를 생성하여 값 set
            User userEntity = new User();
            userEntity.setUsername(username);
            userEntity.setId(id);

            // UserDetails에 회원 정보 객체 담기
            CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

            // 스프링 시큐리티 인증 토큰 생성
            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

            // 세션에 사용자 등록
            SecurityContextHolder.getContext().setAuthentication(authToken);

            logger.debug("User authenticated and set in SecurityContext.");

        } catch (Exception e) {
            logger.error("Error processing JWT token: {}", e.getMessage(), e);
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
}
