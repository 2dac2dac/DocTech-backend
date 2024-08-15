package dac2dac.doctect.user.dto;

import dac2dac.doctect.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // 권한 로직 필요
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    public Long getId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getPhoneNumber() {
        return user.getPhoneNumber();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 비즈니스 로직에 맞게 수정 필요
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 비즈니스 로직에 맞게 수정 필요
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비즈니스 로직에 맞게 수정 필요
    }

    @Override
    public boolean isEnabled() {
        return true; // 비즈니스 로직에 맞게 수정 필요
    }
}