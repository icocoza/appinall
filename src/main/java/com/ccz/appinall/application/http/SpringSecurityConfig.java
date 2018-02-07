package com.ccz.appinall.application.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ccz.appinall.application.http.admin.business.service.UserDetailsServiceImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@ComponentScan("com.ccz.appinall")
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**", "/css/**", "/js/**");
	}
	
	@Override 
	protected void configure(HttpSecurity http) throws Exception {
		http	.headers()
			.frameOptions().sameOrigin()
			.httpStrictTransportSecurity().disable();
		http.authorizeRequests().antMatchers("/**").permitAll()
		.and()
		.formLogin()
		//.loginProcessingUrl("/index.html")
		.loginPage("/login")
		.successForwardUrl("/home")
		.failureUrl("/loginerror")
		.usernameParameter("email")
		.passwordParameter("passwd")
		//.loginProcessingUrl("/adminlogin")
		//.defaultSuccessUrl("/")
		.permitAll()
		.and()
		.logout()
		//.logoutUrl("/adminlogout")
		.logoutSuccessUrl("/index.html")
		//.invalidateHttpSession(true)
		//.permitAll().
//		.and()
//        .authorizeRequests()
//        .antMatchers("/admin/**").hasRole("ADMIN_MASTER")
//        .antMatchers("/adminuser/**").hasRole("ADMIN_USER")
//        .antMatchers("/user/**").hasRole("USER")
//        .antMatchers("/shared/**").hasAnyRole("USER","ADMIN_USER", "ADMIN_MASTER")
        .and()
        .exceptionHandling()
        .accessDeniedPage("/403.html")
		.and()
		.csrf().disable();
	}

	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception  {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
	}
}
