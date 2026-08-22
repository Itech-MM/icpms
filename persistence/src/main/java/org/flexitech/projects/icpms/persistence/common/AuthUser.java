package org.flexitech.projects.icpms.persistence.common;

import java.util.Collection;
import java.util.Set;

import org.flexitech.projects.icpms.persistence.entities.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;

@Getter
public class AuthUser implements UserDetails {
    /**
	 * 
	 */
	private static final long serialVersionUID = -7059454804583626817L;
	private final User user;
	private Set<GrantedAuthority> authorities;
    public AuthUser(User user){
        this.user = user;
    }
    
    public AuthUser(User user, Set<GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getName();
    }

    // implement other UserDetails methods
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}
