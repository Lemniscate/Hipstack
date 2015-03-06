package com.github.lemniscate.hipstack.config;

import com.github.lemniscate.hipstack.svc.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.inject.Inject;

/**
 * Created by dave on 3/5/15.
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Inject
    private UserService userService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth
            .userDetailsService(userService)
                .passwordEncoder(passwordEncoder())
                .and()
        ;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .logout()
                .deleteCookies("JSESSIONID")
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
            .and()
            .authorizeRequests()
                    //Anyone can access the urls, but the security preprocessor will run here
                .antMatchers(
                        "/",
                        "/app**",
                        "/src/**",
                        "/dist/**",
                        "/bower_components/**",
                        "/api/auth/**",
                        "/dummy**"
                ).permitAll()
            //The rest of the our application is protected.
//            .antMatchers("/**").hasRole("DEFAULT")

        ;



    }


    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

//    @EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
//    private static class GlobalSecurityConfiguration extends GlobalMethodSecurityConfiguration {
//
////        @Override
////        protected MethodSecurityExpressionHandler createExpressionHandler() {
////            return new OAuth2MethodSecurityExpressionHandler();
////        }
//    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }

}
