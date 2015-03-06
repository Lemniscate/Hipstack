package com.github.lemniscate.hipstack.repository;

import com.github.lemniscate.hipstack.domain.user.UserAccount;
import com.github.lemniscate.spring.crud.repo.ApiResourceRepository;

/**
 * @Author dave 11/4/14 12:28 PM
 */
public interface UserAccountRepository extends ApiResourceRepository<Long, UserAccount> {

    UserAccount findByEmail(String email);
}
