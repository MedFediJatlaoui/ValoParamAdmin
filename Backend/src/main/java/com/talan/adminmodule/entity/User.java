package com.talan.adminmodule.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String firstname;
  private String lastname;
  @Column(unique=true)
  private String email;
  private String password;
  private String profileImagePath;
  private String company;
  private String phone;
  @Enumerated(EnumType.STRING)
  private Role role;
  @Builder.Default
  private Boolean active = true;
  @Builder.Default
  private Boolean nonExpired = true;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "user_authorities", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "authority")
  private List<String> authorities = new ArrayList<>();

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
    grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
    if (role == Role.EXPERT) {
      // Add CAN_CANCEL authority to EXPERT role if specified
      if (authorities.contains("CAN_CANCEL")) {
        grantedAuthorities.add(new SimpleGrantedAuthority("CAN_CANCEL"));
      }
    }
    // Add any additional authorities defined for the user
    for (String authority : authorities) {
      grantedAuthorities.add(new SimpleGrantedAuthority(authority));
    }
    return grantedAuthorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
