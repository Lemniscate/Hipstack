package com.github.lemniscate.hipstack.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.lemniscate.spring.crud.annotation.ApiResource;
import com.github.lemniscate.spring.crud.model.Model;
import com.github.lemniscate.hipstack.domain.security.AbstractAuditingEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@ApiResource( path = "externalAccounts", omitController = true)
public class ExternalAccount extends AbstractAuditingEntity implements Model<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Setter( AccessLevel.NONE )
    private Long id;

    private String providerId, providerUserId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="account_id")
    private UserAccount account;

    @Deprecated
    public ExternalAccount() {}

    public ExternalAccount(String providerId, String providerUserId){
        this.providerId = providerId;
        this.providerUserId = providerUserId;
    }

}
