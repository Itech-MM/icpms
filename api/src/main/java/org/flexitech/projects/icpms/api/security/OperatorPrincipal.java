package org.flexitech.projects.icpms.api.security;

import java.util.Collection;
import java.util.List;

import org.flexitech.projects.icpms.common.enums.OperatorRole;
import org.flexitech.projects.icpms.persistence.entities.operator.Operator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;

@Getter
public class OperatorPrincipal implements UserDetails {

	private static final long serialVersionUID = 1L;

	private final Operator operator;

	public OperatorPrincipal(Operator operator) {
		this.operator = operator;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new java.util.ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_OPERATOR"));

		for (OperatorRole role : OperatorRole.values()) {
			if (role.getCode().equals(operator.getRole())) {
				authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
				break;
			}
		}

		return authorities;
	}

	@Override
	public String getPassword() {
		return operator.getPassword();
	}

	@Override
	public String getUsername() {
		return operator.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return operator.getStatus() != null && operator.getStatus() == 1;
	}
}