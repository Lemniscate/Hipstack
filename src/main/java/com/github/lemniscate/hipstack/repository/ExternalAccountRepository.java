package com.github.lemniscate.hipstack.repository;

import com.github.lemniscate.hipstack.domain.user.ExternalAccount;
import com.github.lemniscate.spring.crud.repo.ApiResourceRepository;

/**
 * @Author dave 11/4/14 12:28 PM
 */
public interface ExternalAccountRepository extends ApiResourceRepository<Long, ExternalAccount> {

    ExternalAccount findByProviderIdAndProviderUserId(String providerId, String providerUserId);
}
