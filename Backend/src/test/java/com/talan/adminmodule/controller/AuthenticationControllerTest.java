package com.talan.adminmodule.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talan.adminmodule.dto.AuthenticationRequest;
import com.talan.adminmodule.dto.AuthenticationResponse;
import com.talan.adminmodule.service.impl.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ContextConfiguration(classes = {AuthenticationController.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class AuthenticationControllerTest {
    @Autowired
    private AuthenticationController authenticationController;

    @MockBean
    private AuthenticationService authenticationService;


    @Test
    void testAuthenticate() throws Exception {
        // Arrange
        AuthenticationResponse buildResult = AuthenticationResponse.builder()
                .accessToken("ABC123")
                .error("An error occurred")
                .refreshToken("ABC123")
                .build();
        when(authenticationService.authenticate(Mockito.<AuthenticationRequest>any())).thenReturn(buildResult);

        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("jane.doe@example.org");
        authenticationRequest.setPassword("iloveyou");
        String content = (new ObjectMapper()).writeValueAsString(authenticationRequest);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        // Act and Assert
        MockMvcBuilders.standaloneSetup(authenticationController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string("{\"access_token\":\"ABC123\",\"refresh_token\":\"ABC123\",\"error\":\"An error occurred\"}"));
    }


    @Test
    void testAuthenticate2() throws Exception {
        // Arrange
        when(authenticationService.authenticate(Mockito.<AuthenticationRequest>any()))
                .thenThrow(new BadCredentialsException("Msg"));

        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("jane.doe@example.org");
        authenticationRequest.setPassword("iloveyou");
        String content = (new ObjectMapper()).writeValueAsString(authenticationRequest);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(authenticationController)
                .build()
                .perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "{\"error\":\"Credentials provided are incorrect. Please check your credentials and try again.\"}"));
    }


    @Test
    void testAuthenticate3() throws Exception {
        // Arrange
        when(authenticationService.authenticate(Mockito.<AuthenticationRequest>any()))
                .thenThrow(new AccountExpiredException("Msg"));

        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("jane.doe@example.org");
        authenticationRequest.setPassword("iloveyou");
        String content = (new ObjectMapper()).writeValueAsString(authenticationRequest);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(authenticationController)
                .build()
                .perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string("{\"error\":\"An error occurred during authentication. Please try again later.\"}"));
    }


    @Test
    void testRefreshToken() throws Exception {
        // Arrange
        doNothing().when(authenticationService)
                .refreshToken(Mockito.<HttpServletRequest>any(), Mockito.<HttpServletResponse>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/auth/refresh-token");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(authenticationController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
