package com.cop4331.group13.cavecheckin.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cop4331.group13.cavecheckin.dao.UserDao;
import com.cop4331.group13.cavecheckin.service.UserDetailsServiceImpl;
import com.google.common.collect.ImmutableList;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

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
                .cors().and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), userDao))
                //DEV Added this block to expose H2 Console for development use
                .authorizeRequests()
                    .antMatchers("/h2/*").permitAll()
                    .and().headers().frameOptions().disable()
                    .and()
                // Main security authorization block
                .authorizeRequests()
                    .antMatchers(HttpMethod.POST, "/login").permitAll()
                    .antMatchers("/v2/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**").permitAll()
                    .antMatchers("/user/ta/*").hasAnyRole("ADMIN", "TEACHER", "TA")
                    .antMatchers("/user/teacher/*").hasAnyRole("ADMIN", "TEACHER")
                    .antMatchers("/user/admin/*").hasAnyRole("ADMIN")
                    .antMatchers("/course/ta/*").hasAnyRole("ADMIN", "TEACHER", "TA")
                    .antMatchers("/course/teacher/*").hasAnyRole("ADMIN", "TEACHER")
                    .antMatchers("/course/admin/*").hasAnyRole("ADMIN")
                    // DEV Unsure if we want kiosk to be validated or not, raises session expiration issues - leaving unauthorized for now
                    .antMatchers("/session/kiosk/*").permitAll() // .hasAnyRole("ADMIN", "TEACHER", "TA", "KIOSK")
                    .antMatchers("/session/ta/*").hasAnyRole("ADMIN", "TEACHER", "TA")
                    .antMatchers("/session/teacher/*").hasAnyRole("ADMIN", "TEACHER")
                    .antMatchers("/session/admin/*").hasAnyRole("ADMIN")
                    .anyRequest().permitAll();
    }

    // From Stack Overflow user Hendy Irawan. Source: https://stackoverflow.com/questions/40418441/spring-security-cors-filter --JS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(ImmutableList.of("*", "https://fierce-mesa-84981.herokuapp.com"));
        configuration.setAllowedMethods(ImmutableList.of("HEAD",
                "GET", "POST", "PUT", "DELETE", "PATCH"));
        // setAllowCredentials(true) is important, otherwise:
        // The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
        configuration.setAllowCredentials(true);
        // setAllowedHeaders is important! Without it, OPTIONS preflight request
        // will fail with 403 Invalid CORS request
        configuration.setAllowedHeaders(ImmutableList.of("Authorization", "Cache-Control", "Content-Type"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
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

    public static String getAuthSubject(String token) {
        return JWT.require(Algorithm.HMAC512(JwtProperties.SECRET.getBytes()))
                .build()
                .verify(token.replace(JwtProperties.TOKEN_PREFIX, ""))
                .getSubject();
    }
}
