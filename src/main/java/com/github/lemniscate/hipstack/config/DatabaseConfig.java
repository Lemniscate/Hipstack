package com.github.lemniscate.hipstack.config;

import com.github.lemniscate.hipstack.Constants;
import com.github.lemniscate.hipstack.domain.user.UserAccount;
import com.github.lemniscate.hipstack.svc.SecurityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.inject.Inject;


@Configuration
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
public class DatabaseConfig {

    @Inject
    private SecurityService securityService;

    @Bean
    public AuditorAware<String> springSecurityAuditorAware(){
        return () -> {
            UserAccount user = securityService.getCurrentUser();
            return (user == null ? Constants.SYSTEM_ACCOUNT : user.getUsername());
        };
    }

}
