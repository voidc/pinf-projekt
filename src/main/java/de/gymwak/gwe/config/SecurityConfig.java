package de.gymwak.gwe.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.util.function.Supplier;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http.csrf().disable()
			.formLogin()
				.loginPage("/login")
				.defaultSuccessUrl("/user")
				.permitAll()
				.and()
			.logout()
				.logoutSuccessUrl("/login")
				.permitAll()
				.and()
			.authorizeRequests()
				.antMatchers("/css/**", "/img/**", "/signup", "/impressum", "/reset", "/activate", "/error").permitAll()
				.anyRequest().authenticated();
		// @formatter:on
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth, UserDetailsService service, PasswordEncoder encoder)
			throws Exception {
		auth.userDetailsService(service).passwordEncoder(encoder);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecureRandom secureRandom() {
		return new SecureRandom();
	}

}
