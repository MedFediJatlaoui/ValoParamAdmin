package com.talan.adminmodule.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.talan.adminmodule.config.JwtService;
import com.talan.adminmodule.dto.AuthenticationRequest;
import com.talan.adminmodule.dto.AuthenticationResponse;
import com.talan.adminmodule.entity.Role;
import com.talan.adminmodule.entity.User;
import com.talan.adminmodule.repository.UserRepository;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import org.apache.catalina.connector.Response;
import org.apache.catalina.connector.ResponseFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mock.web.DelegatingServletInputStream;
import org.springframework.mock.web.MockHttpServletMapping;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {AuthenticationService.class})
@ExtendWith(SpringExtension.class)
@PropertySource("classpath:application-test.properties")
@EnableConfigurationProperties
@DisabledInAotMode
class AuthenticationServiceTest {
  @MockBean
  private AuthenticationManager authenticationManager;

  @Autowired
  private AuthenticationService authenticationService;

  @MockBean
  private JwtService jwtService;

  @MockBean
  private UserRepository userRepository;


  @Test
  void testAuthenticate() throws AuthenticationException {
    // Arrange
    when(authenticationManager.authenticate(Mockito.<Authentication>any()))
        .thenReturn(new TestingAuthenticationToken("Principal", "Credentials"));

    User user = new User();
    user.setActive(true);
    user.setCompany("Company");
    user.setEmail("jane.doe@example.org");
    user.setFirstname("Jane");
    user.setId(1);
    user.setLastname("Doe");
    user.setNonExpired(true);
    user.setPassword("iloveyou");
    user.setPhone("6625550144");
    user.setProfileImagePath("Profile Image Path");
    user.setRole(Role.BUSINESSEXPERT);
    Optional<User> ofResult = Optional.of(user);
    when(userRepository.findByEmail(Mockito.<String>any())).thenReturn(ofResult);
    when(jwtService.generateRefreshToken(Mockito.<UserDetails>any())).thenReturn("ABC123");
    when(jwtService.generateToken(Mockito.<UserDetails>any())).thenReturn("ABC123");

    // Act
    AuthenticationResponse actualAuthenticateResult = authenticationService
        .authenticate(new AuthenticationRequest("jane.doe@example.org", "iloveyou"));

    // Assert
    verify(jwtService).generateRefreshToken(isA(UserDetails.class));
    verify(jwtService).generateToken(isA(UserDetails.class));
    verify(userRepository).findByEmail(eq("jane.doe@example.org"));
    verify(authenticationManager).authenticate(isA(Authentication.class));
    assertEquals("ABC123", actualAuthenticateResult.getAccessToken());
    assertEquals("ABC123", actualAuthenticateResult.getRefreshToken());
    assertNull(actualAuthenticateResult.getError());
  }

  @Test
  void testRefreshToken() throws IOException {
    // Arrange
    MockHttpServletRequest request = new MockHttpServletRequest();
    Response response = new Response();

    // Act
    authenticationService.refreshToken(request, response);

    // Assert that nothing has changed
    HttpServletResponse response2 = response.getResponse();
    assertTrue(response2 instanceof ResponseFacade);
    assertTrue(request.getInputStream() instanceof DelegatingServletInputStream);
    assertTrue(request.getHttpServletMapping() instanceof MockHttpServletMapping);
    assertTrue(request.getSession() instanceof MockHttpSession);
    assertEquals("", request.getContextPath());
    assertEquals("", request.getMethod());
    assertEquals("", request.getRequestURI());
    assertEquals("", request.getServletPath());
    assertEquals("HTTP/1.1", request.getProtocol());
    assertEquals("http", request.getScheme());
    assertEquals("localhost", request.getLocalName());
    assertEquals("localhost", request.getRemoteHost());
    assertEquals("localhost", request.getServerName());
    assertEquals(80, request.getLocalPort());
    assertEquals(80, request.getRemotePort());
    assertEquals(80, request.getServerPort());
    assertEquals(DispatcherType.REQUEST, request.getDispatcherType());
    assertFalse(request.isAsyncStarted());
    assertFalse(request.isAsyncSupported());
    assertFalse(request.isRequestedSessionIdFromURL());
    assertTrue(request.isActive());
    assertTrue(request.isRequestedSessionIdFromCookie());
    assertTrue(request.isRequestedSessionIdValid());
    ServletOutputStream expectedOutputStream = response.getOutputStream();
    assertSame(expectedOutputStream, response2.getOutputStream());
  }
}
