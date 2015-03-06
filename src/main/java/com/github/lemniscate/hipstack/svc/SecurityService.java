package com.github.lemniscate.hipstack.svc;

import com.github.lemniscate.hipstack.domain.user.UserAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Slf4j
@Service
public class SecurityService {

    @Inject
    private AuthenticationManager am;

    @Inject
    private UserService userService;

    public UserAccount getCurrentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if( auth != null ){
            Object p = auth.getPrincipal();
            if( p instanceof UserAccount){
                return (UserAccount) p;
            }
            log.warn("Could not determine type of user account ({})", p);
        }
        return null;
    }

    public UserAccount login(UserAccount user, Object credentials){
        Authentication auth = am.authenticate(new UsernamePasswordAuthenticationToken(user, credentials));
        SecurityContextHolder.getContext().setAuthentication(auth);
        return user;
    }

    public void updateCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if( auth != null ){
            Object p = auth.getPrincipal();
            if( p instanceof UserAccount){
                UserAccount user = (UserAccount) p;
                user = userService.findOne(user.getId());
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, null, auth.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(token);
            }
            log.warn("Could not determine type of user account ({})", p);
        }
    }
}
