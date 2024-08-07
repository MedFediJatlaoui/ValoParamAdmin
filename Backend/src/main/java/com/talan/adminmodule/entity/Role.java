package com.talan.adminmodule.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public enum Role {
  EXPERT(List.of()),  // No default authorities here
  ADMIN(List.of()),   // No default authorities here
  CONSULTANT(List.of());  // No default authorities here

  private final List<String> authorities;

  public List<SimpleGrantedAuthority> getAuthorities() {
    List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
    for (String authority : authorities) {
      grantedAuthorities.add(new SimpleGrantedAuthority(authority));
    }
    return grantedAuthorities;
  }
}
