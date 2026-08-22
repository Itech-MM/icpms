package org.flexitech.projects.icpms.service.auth;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.flexitech.projects.icpms.common.enums.MenuPermission;
import org.flexitech.projects.icpms.persistence.common.AuthUser;
import org.flexitech.projects.icpms.persistence.entities.user.User;
import org.flexitech.projects.icpms.persistence.repositories.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Logging in user...");
        User user = userRepository.findByPhoneNumber(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        
        Set<GrantedAuthority> authorities = /*buildAuthorities(user)*/ new HashSet<GrantedAuthority>();
        
        log.debug("user is logged!");
        return new AuthUser(user, authorities);
    }
    
	/*
	 * private Set<GrantedAuthority> buildAuthorities(User user) {
	 * Set<GrantedAuthority> authorities = new HashSet<>();
	 * 
	 * if (user.getRole() != null && user.getRole().getCode() != null) {
	 * authorities.add(new SimpleGrantedAuthority("ROLE_" +
	 * user.getRole().getCode()));
	 * 
	 * List<MenuRoleAccess> menuAccesses = menuRoleAccessRepository
	 * .findByRoleIdWithMenu(user.getRole().getId());
	 * 
	 * for (MenuRoleAccess access : menuAccesses) { if (access.getMenu() != null &&
	 * access.getMenu().getCode() != null) { String menuCode =
	 * access.getMenu().getCode();
	 * 
	 * if (Boolean.TRUE.equals(access.getCanView())) { authorities.add(new
	 * SimpleGrantedAuthority(MenuPermission.VIEW.getAuthority() + "_" + menuCode));
	 * } if (Boolean.TRUE.equals(access.getCanAccess())) { authorities.add(new
	 * SimpleGrantedAuthority(MenuPermission.ACCESS.getAuthority() + "_" +
	 * menuCode)); } if (Boolean.TRUE.equals(access.getCanEdit())) {
	 * authorities.add(new SimpleGrantedAuthority(MenuPermission.EDIT.getAuthority()
	 * + "_" + menuCode)); } if (Boolean.TRUE.equals(access.getCanDelete())) {
	 * authorities.add(new
	 * SimpleGrantedAuthority(MenuPermission.DELETE.getAuthority() + "_" +
	 * menuCode)); } } } }
	 * 
	 * return authorities; }
	 */

	public User getLoggedInUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!authentication.isAuthenticated()) {
			log.error("User is not logged in!");
			return null;
		}
		if (authentication.getPrincipal() instanceof AuthUser authUser) {
			return authUser.getUser();
		}
		return null;
	}
}
