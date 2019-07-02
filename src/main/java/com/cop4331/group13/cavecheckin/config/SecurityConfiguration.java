package com.cop4331.group13.cavecheckin.config;

import com.cop4331.group13.cavecheckin.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserDao userDao;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), userDao))
                .authorizeRequests()
                .antMatchers("/h2/*").permitAll()
                .antMatchers(HttpMethod.POST, "/login").permitAll()
                .antMatchers("/user/ta/*").hasAnyRole("ADMIN", "TEACHER", "TA")
                .antMatchers("/user/teacher/*").hasAnyRole("ADMIN", "TEACHER")
                .antMatchers("/user/admin/*").hasAnyRole("ADMIN")
                .antMatchers("/course/ta/*").hasAnyRole("ADMIN", "TEACHER", "TA")
                .antMatchers("/course/teacher/*").hasAnyRole("ADMIN", "TEACHER")
                .antMatchers("/course/admin/*").hasAnyRole("ADMIN")
                .antMatchers("/session/ta/*").hasAnyRole("ADMIN", "TEACHER", "TA")
                .antMatchers("/session/teacher/*").hasAnyRole("ADMIN", "TEACHER")
                .antMatchers("/session/admin/*").hasAnyRole("ADMIN")
                .anyRequest().authenticated();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(this.userDetailsService);

        return daoAuthenticationProvider;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
