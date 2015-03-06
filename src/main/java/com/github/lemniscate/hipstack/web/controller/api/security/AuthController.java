package com.github.lemniscate.hipstack.web.controller.api.security;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonView;
import com.github.lemniscate.hipstack.svc.UserService;
import com.github.lemniscate.spring.crud.annotation.ApiController;
import com.github.lemniscate.spring.crud.util.ApiResourceSupport;
import com.github.lemniscate.spring.crud.web.assembler.ApiResourceAssemblers;
import com.github.lemniscate.spring.jsonviews.client.BaseView;
import com.github.lemniscate.spring.jsonviews.client.JsonViewResponseEntity;
import com.google.common.collect.ImmutableMap;
import com.github.lemniscate.hipstack.domain.user.UserAccount;
import com.github.lemniscate.hipstack.svc.SecurityService;
import com.github.lemniscate.hipstack.web.views.JsonViews;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@ApiController
@RequestMapping("/api/auth")
public class AuthController extends ApiResourceSupport {

    @Inject
    private UserService userService;

    @Inject
    private AuthenticationManager am;

    @Inject
    private SecurityService securityService;

    @ResponseBody
    @RequestMapping(value="")
    public ResponseEntity<?> getRoot(HttpServletRequest req) {
        UserAccount user = securityService.getCurrentUser();
        if( user == null ){
            return new ResponseEntity<Object>(ImmutableMap.<String, Object>builder()
                .put("message", "You are not currently logged in")
                .build(),
                HttpStatus.NOT_FOUND);
        }else{
            UserLoginBean result = new UserLoginBean(user, req.getSession(true).getId());
            return new JsonViewResponseEntity<Object>(JsonViews.Detailed.class, result, HttpStatus.OK);
        }
    }

    @ResponseBody
    @RequestMapping(value="/login", method= RequestMethod.POST)
    public ResponseEntity<?> postLogin(@RequestBody @Valid LoginBean bean, BindingResult beanResult, HttpServletRequest req){
        if( beanResult.hasErrors() ){
            return new ResponseEntity<Object>(ImmutableMap.<String, Object>builder()
                .put("message", "Please provide an email and password")
                .build(),
                HttpStatus.BAD_REQUEST);
        }

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

        HttpStatus status;
        UserAccount user = userService.findOneByEmail(bean.getEmail());
        if( user == null ){
            user = userService.createParticipantUser(bean.getEmail(), bean.getPassword());
            securityService.login(user, bean.getPassword());
            headers.add(ApiResourceAssemblers.X_SELF_HREF, assemblers.assemble(user).getLink("self").getHref() );
            status = HttpStatus.CREATED;
        }else{
            try {
                securityService.login(user, bean.getPassword());
                status = HttpStatus.OK;
            }catch(BadCredentialsException ex){
                return new ResponseEntity<Object>(ImmutableMap.<String, Object>builder()
                    .put("message", "Invalid login credentials")
                    .build(),
                    HttpStatus.UNAUTHORIZED);
            }
        }

        UserLoginBean result = new UserLoginBean(user, req.getSession(true).getId());

        return new JsonViewResponseEntity<Object>(JsonViews.Detailed.class, result, headers, status);
    }

    @ResponseBody
    @RequestMapping(value="/logout")
    public ResponseEntity<?> postLogin(HttpServletRequest req) {
        req.getSession().invalidate();
        return ResponseEntity.ok(ImmutableMap.<String, Object>builder()
            .put("message", "You are now logged out")
            .build());
    }


    @Data
    static class LoginBean{
        @NotEmpty
        private String email, password;
    }

    @Data
    static class UserLoginBean{
        @JsonUnwrapped
        @JsonView(BaseView.class)
        private final UserAccount user;
        @JsonView(BaseView.class)
        private final String token;
    }
}
