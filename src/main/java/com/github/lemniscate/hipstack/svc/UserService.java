package com.github.lemniscate.hipstack.svc;

import com.github.lemniscate.spring.crud.svc.ApiResourceService;
import com.github.lemniscate.spring.crud.svc.ApiResourceServiceImpl;
import com.google.common.collect.Lists;
import com.github.lemniscate.hipstack.domain.user.ExternalAccount;
import com.github.lemniscate.hipstack.domain.user.UserAccount;
import com.github.lemniscate.hipstack.repository.ExternalAccountRepository;
import com.github.lemniscate.hipstack.repository.UserAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.inject.Inject;

public interface UserService extends ApiResourceService<Long, UserAccount, UserAccount, UserAccount, UserAccount>,
    SocialUserDetailsService, UserDetailsService {

    UserAccount findOneByEmail(String email);

    UserAccount createUserWithPassword(String firstName, String lastName, String displayName, String email, String rawPassword);

    UserAccount createParticipantUser(String email, String rawPassword);

    UserAccount createUserWithExternalAccount(String firstName, String lastName, String displayName, String email, Connection<?> connection);

    UserAccount findByExternalAccountDetails(String providerId, String providerUserId);

    @Slf4j
    @Component("userDetailsService")
    public static class UserServiceImpl extends ApiResourceServiceImpl<Long, UserAccount, UserAccount, UserAccount, UserAccount>
        implements UserService{

        @Inject
        private UserAccountRepository userRepo;

        @Inject
        private ExternalAccountRepository externalAccountRepo;

        @Inject
        private PasswordEncoder passwordEncoder;

        @Inject
        private SecurityService securityService;

        @Override
        public UserAccount findOneByEmail(String email){
            return userRepo.findByEmail(email);
        }

        @Override
        public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException, DataAccessException {
            return (SocialUserDetails) loadUserByUsername(userId);
        }

        @Override
        public UserAccount createUserWithPassword(String firstName, String lastName, String displayName, String email, String rawPassword){
            Assert.hasLength(firstName, "Parameter firstName is required");
            Assert.hasLength(lastName, "Parameter lastName is required");
            Assert.hasLength(displayName, "Parameter displayName is required");
            Assert.hasLength(email, "Parameter email is required");
            Assert.hasLength(rawPassword, "Parameter rawPassword is required");

            UserAccount existing = findOneByEmail(email);
            Assert.isNull(existing, "That email is already registered");

            String password = passwordEncoder.encode(rawPassword);
            UserAccount user = new UserAccount(firstName, lastName, displayName, email, password);
            save(user);
            return user;
        }

        @Override
        public UserAccount createParticipantUser(String email, String rawPassword){
            UserAccount existing = findOneByEmail(email);
            Assert.isNull(existing, "That email is already registered");

            String password = passwordEncoder.encode(rawPassword);
            UserAccount user = new UserAccount(email, password);
            save(user);
            return user;
        }

        @Override
        public UserAccount createUserWithExternalAccount(String firstName, String lastName, String displayName, String email, Connection<?> connection){
            Assert.hasLength(firstName, "Parameter firstName is required");
            Assert.hasLength(lastName, "Parameter lastName is required");
            Assert.hasLength(displayName, "Parameter displayName is required");
            Assert.hasLength(email, "Parameter email is required");
            Assert.notNull(connection, "Parameter connection is required");

            UserAccount existing = findOneByEmail(email);
            Assert.isNull(existing, "That email is already registered");

            ConnectionKey key = connection.getKey();
            ExternalAccount existingExternal = externalAccountRepo.findByProviderIdAndProviderUserId(key.getProviderId(), key.getProviderUserId());
            Assert.isNull(existingExternal, "A user is already linked to this external account");

            ExternalAccount externalAccount = new ExternalAccount(key.getProviderId(), key.getProviderUserId());
            UserAccount user = new UserAccount(firstName, lastName, displayName, email, Lists.newArrayList(externalAccount));
            save(user);
            return user;
        }

        @Override
        public UserAccount update(Long id, UserAccount bean) {
            UserAccount user = findOne(id);
            user.setFirstName(bean.getFirstName());
            user.setLastName(bean.getLastName());
            user.setDisplayName(bean.getDisplayName());
            user = save(user);


            UserAccount current = securityService.getCurrentUser();
            if( current != null && current.getId().equals(user.getId()) ){
                securityService.updateCurrentUser();
            }

            return user;
        }

        @Override
        @Transactional
        public UserDetails loadUserByUsername(final String email) {
            log.debug("Authenticating {}", email);
            String lowercaseLogin = email.toLowerCase();

            UserAccount user = userRepo.findByEmail(lowercaseLogin);
            if (user == null) {
                throw new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the database");
                //        } else if (!user.getActivated()) {
                //            throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
            }

            return user;
        }

        @Override
        public UserAccount findByExternalAccountDetails(String providerId, String providerUserId) {
            ExternalAccount external = externalAccountRepo.findByProviderIdAndProviderUserId(providerId, providerUserId);
            return external == null ? null : external.getAccount();
        }
    }
}
