package com.github.lemniscate.hipstack.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.github.lemniscate.spring.crud.annotation.ApiResource;
import com.github.lemniscate.spring.crud.model.Model;
import com.google.common.collect.Lists;
import com.github.lemniscate.hipstack.domain.security.AbstractAuditingEntity;
import com.github.lemniscate.hipstack.web.views.JsonViews;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter @Setter
@ApiResource( path = "users" )
public class UserAccount extends AbstractAuditingEntity implements SocialUserDetails, Model<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Setter( AccessLevel.NONE )
    @JsonView(JsonViews.Summary.class)
    private Long id;

    @JsonIgnore
    private String password;

    @JsonView(JsonViews.Summary.class)
    private String firstName, lastName, displayName;

    @JsonView(JsonViews.Detailed.class)
    private String email;

    @JsonView(JsonViews.Summary.class)
    private Boolean active;

    @JsonIgnore
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<ExternalAccount> externalAccounts = Lists.newArrayList();

    @Deprecated
    public UserAccount() {}

    public UserAccount(String firstName, String lastName, String displayName, String email, String password) {
        this(email, password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
    }

    public UserAccount(String firstName, String lastName, String displayName, String email, List<ExternalAccount> externalAccounts) {
        this(email, null);
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
        externalAccounts.forEach(this::addExternalAccount);
    }

    public UserAccount(String email, String password) {
        this.email = email;
        this.password = password;
        this.active = true;
    }

    public void addExternalAccount(ExternalAccount account){
        Assert.notNull(account, "Can not add an external account");
        account.setAccount(this);
        externalAccounts.add(account);
    }


    // FIXME implement the social ID portion
    // SpringSecuritySocial methods
    @Override
    public String getUserId() {
        return email;
    }

    // SpringSecurity methods
    @JsonIgnore
    @Override
    public Collection< ? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
        authorities.add( new SimpleGrantedAuthority( "ROLE_DEFAULT" ) );
        return authorities;
    }

    @JsonIgnore
    @Override
    public String getUsername(){
        return email;
    }

    @JsonIgnore
    @Override
    public String getPassword(){
        return password;
    }

    @Override
    public boolean isAccountNonExpired(){
        return true;
    }

    @Override
    public boolean isAccountNonLocked(){
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired(){
        return true;
    }

    @Override
    public boolean isEnabled(){
        return active != null ? active : false;
    }

}
