package de.gymwak.gwe.data;

import de.gymwak.gwe.model.GWEUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service public class GWERepositoryService implements UserDetailsService {
	private final GWERepository userRepository;

	@Autowired public GWERepositoryService(GWERepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		GWEUser user = userRepository.findByEmail(username);
		if (user == null) {
			throw new UsernameNotFoundException("Could not find user " + username);
		}
		return new GWEUserDetails(user);
	}

	private final class GWEUserDetails extends GWEUser implements UserDetails {

		GWEUserDetails(GWEUser user) {
			super(user);
		}

		@Override public Collection<? extends GrantedAuthority> getAuthorities() {
			return AuthorityUtils.createAuthorityList("ROLE_USER");
		}

		@Override public String getUsername() {
			return getEmail();
		}

		@Override public boolean isAccountNonExpired() {
			return true;
		}

		@Override public boolean isAccountNonLocked() {
			return true;
		}

		@Override public boolean isCredentialsNonExpired() {
			return true;
		}

		@Override public boolean isEnabled() {
			return true;
		}

		private static final long serialVersionUID = 5639683223516504866L;
	}

}
