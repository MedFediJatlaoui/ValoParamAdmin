package com.talan.adminmodule.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.talan.adminmodule.dto.RegisterDto;
import com.talan.adminmodule.dto.UserDto;
import com.talan.adminmodule.entity.Role;
import com.talan.adminmodule.entity.User;
import com.talan.adminmodule.repository.UserRepository;
import com.talan.adminmodule.service.impl.UserService;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

@ContextConfiguration(classes = {UserController.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class UserControllerTest {
    @MockBean
    private ModelMapper modelMapper;

    @Autowired
    private UserController userController;

    @MockBean
    private UserService userService;


    @Test
    void testAddUser() throws IOException {
        try (MockedStatic<Files> mockFiles = mockStatic(Files.class)) {

            // Arrange
            mockFiles.when(() -> Files.exists(Mockito.<Path>any(), isA(LinkOption[].class))).thenReturn(true);
            mockFiles.when(() -> Files.createDirectories(Mockito.<Path>any(), isA(FileAttribute[].class)))
                    .thenReturn(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));
            mockFiles.when(() -> Files.copy(Mockito.<InputStream>any(), Mockito.<Path>any(), isA(CopyOption[].class)))
                    .thenReturn(1L);
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
            UserRepository userRepository = mock(UserRepository.class);
            when(userRepository.save(Mockito.<User>any())).thenReturn(user);
            ModelMapper modelMapper = new ModelMapper();
            UserService userservice = new UserService(modelMapper, new BCryptPasswordEncoder(), userRepository);

            UserController userController = new UserController(userservice, new ModelMapper());
            RegisterDto dto = new RegisterDto("Jane", "Doe", "iloveyou", "jane.doe@example.org", "Company", "6625550144",
                    Role.BUSINESSEXPERT);

            // Act
            ResponseEntity<UserDto> actualAddUserResult = userController.addUser(dto,
                    new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))));

            // Assert
            mockFiles.verify(() -> Files.copy(Mockito.<InputStream>any(), Mockito.<Path>any(), isA(CopyOption[].class)));
            mockFiles.verify(() -> Files.exists(Mockito.<Path>any(), isA(LinkOption[].class)));
            verify(userRepository).save(isA(User.class));
            UserDto body = actualAddUserResult.getBody();
            assertEquals("6625550144", body.getPhone());
            assertEquals("Company", body.getCompany());
            assertEquals("Doe", body.getLastname());
            assertEquals("Jane", body.getFirstname());
            assertEquals("assets\\demo\\images\\user-profiles", body.getProfileImagePath());
            assertEquals("jane.doe@example.org", body.getEmail());
            assertNull(body.getId());
            assertEquals(201, actualAddUserResult.getStatusCodeValue());
            assertEquals(Role.BUSINESSEXPERT, body.getRole());
            assertTrue(body.isActive());
            assertTrue(body.isNonExpired());
            assertTrue(actualAddUserResult.hasBody());
            assertTrue(actualAddUserResult.getHeaders().isEmpty());
        }
    }


    @Test
    void testAddUser2() throws IOException {
        try (MockedStatic<Files> mockFiles = mockStatic(Files.class)) {

            // Arrange
            mockFiles.when(() -> Files.exists(Mockito.<Path>any(), isA(LinkOption[].class))).thenReturn(false);
            mockFiles.when(() -> Files.createDirectories(Mockito.<Path>any(), isA(FileAttribute[].class)))
                    .thenReturn(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));
            mockFiles.when(() -> Files.copy(Mockito.<InputStream>any(), Mockito.<Path>any(), isA(CopyOption[].class)))
                    .thenReturn(1L);

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
            UserRepository userRepository = mock(UserRepository.class);
            when(userRepository.save(Mockito.<User>any())).thenReturn(user);
            ModelMapper modelMapper = new ModelMapper();
            UserService userservice = new UserService(modelMapper, new BCryptPasswordEncoder(), userRepository);

            UserController userController = new UserController(userservice, new ModelMapper());
            RegisterDto dto = new RegisterDto("Jane", "Doe", "iloveyou", "jane.doe@example.org", "Company", "6625550144",
                    Role.BUSINESSEXPERT);

            // Act
            ResponseEntity<UserDto> actualAddUserResult = userController.addUser(dto,
                    new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))));

            // Assert
            mockFiles.verify(() -> Files.copy(Mockito.<InputStream>any(), Mockito.<Path>any(), isA(CopyOption[].class)));
            mockFiles.verify(() -> Files.createDirectories(Mockito.<Path>any(), isA(FileAttribute[].class)));
            mockFiles.verify(() -> Files.exists(Mockito.<Path>any(), isA(LinkOption[].class)));
            verify(userRepository).save(isA(User.class));
            UserDto body = actualAddUserResult.getBody();
            assertEquals("6625550144", body.getPhone());
            assertEquals("Company", body.getCompany());
            assertEquals("Doe", body.getLastname());
            assertEquals("Jane", body.getFirstname());
            assertEquals("assets\\demo\\images\\user-profiles", body.getProfileImagePath());
            assertEquals("jane.doe@example.org", body.getEmail());
            assertNull(body.getId());
            assertEquals(201, actualAddUserResult.getStatusCodeValue());
            assertEquals(Role.BUSINESSEXPERT, body.getRole());
            assertTrue(body.isActive());
            assertTrue(body.isNonExpired());
            assertTrue(actualAddUserResult.hasBody());
            assertTrue(actualAddUserResult.getHeaders().isEmpty());
        }
    }


    @Test
    void testAddUser3() throws IOException {
        try (MockedStatic<Files> mockFiles = mockStatic(Files.class)) {

            // Arrange
            mockFiles.when(() -> Files.exists(Mockito.<Path>any(), isA(LinkOption[].class))).thenReturn(true);
            mockFiles.when(() -> Files.createDirectories(Mockito.<Path>any(), isA(FileAttribute[].class)))
                    .thenReturn(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));
            mockFiles.when(() -> Files.copy(Mockito.<InputStream>any(), Mockito.<Path>any(), isA(CopyOption[].class)))
                    .thenReturn(1L);
            ModelMapper modelMapper = mock(ModelMapper.class);
            UserDto buildResult = UserDto.builder()
                    .active(true)
                    .company("Company")
                    .email("jane.doe@example.org")
                    .error("An error occurred")
                    .firstname("Jane")
                    .id(1L)
                    .lastname("Doe")
                    .nonExpired(true)
                    .phone("6625550144")
                    .profileImagePath("Profile Image Path")
                    .role(Role.BUSINESSEXPERT)
                    .build();
            when(modelMapper.map(Mockito.<Object>any(), Mockito.<Class<UserDto>>any())).thenReturn(buildResult);

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
            UserRepository userRepository = mock(UserRepository.class);
            when(userRepository.save(Mockito.<User>any())).thenReturn(user);
            UserService userservice = new UserService(modelMapper, new BCryptPasswordEncoder(), userRepository);

            UserController userController = new UserController(userservice, new ModelMapper());
            RegisterDto dto = new RegisterDto("Jane", "Doe", "iloveyou", "jane.doe@example.org", "Company", "6625550144",
                    Role.BUSINESSEXPERT);

            // Act
            ResponseEntity<UserDto> actualAddUserResult = userController.addUser(dto,
                    new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))));

            // Assert
            mockFiles.verify(() -> Files.copy(Mockito.<InputStream>any(), Mockito.<Path>any(), isA(CopyOption[].class)));
            mockFiles.verify(() -> Files.exists(Mockito.<Path>any(), isA(LinkOption[].class)));
            verify(modelMapper).map(isA(Object.class), isA(Class.class));
            verify(userRepository).save(isA(User.class));
            assertEquals(201, actualAddUserResult.getStatusCodeValue());
            assertTrue(actualAddUserResult.hasBody());
            assertTrue(actualAddUserResult.getHeaders().isEmpty());
        }
    }


    @Test
    void testAddUser4() throws IOException {
        try (MockedStatic<Files> mockFiles = mockStatic(Files.class)) {

            // Arrange
            mockFiles.when(() -> Files.exists(Mockito.<Path>any(), isA(LinkOption[].class))).thenReturn(true);
            mockFiles.when(() -> Files.createDirectories(Mockito.<Path>any(), isA(FileAttribute[].class)))
                    .thenReturn(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));
            mockFiles.when(() -> Files.copy(Mockito.<InputStream>any(), Mockito.<Path>any(), isA(CopyOption[].class)))
                    .thenReturn(1L);
            UserService userservice = mock(UserService.class);
            UserDto buildResult = UserDto.builder()
                    .active(true)
                    .company("Company")
                    .email("jane.doe@example.org")
                    .error("An error occurred")
                    .firstname("Jane")
                    .id(1L)
                    .lastname("Doe")
                    .nonExpired(true)
                    .phone("6625550144")
                    .profileImagePath("Profile Image Path")
                    .role(Role.BUSINESSEXPERT)
                    .build();
            when(userservice.addUser(Mockito.<RegisterDto>any(), Mockito.<MultipartFile>any())).thenReturn(buildResult);
            UserController userController = new UserController(userservice, new ModelMapper());
            RegisterDto dto = new RegisterDto("Jane", "Doe", "iloveyou", "jane.doe@example.org", "Company", "6625550144",
                    Role.BUSINESSEXPERT);

            // Act
            ResponseEntity<UserDto> actualAddUserResult = userController.addUser(dto,
                    new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))));

            // Assert
            verify(userservice).addUser(isA(RegisterDto.class), isA(MultipartFile.class));
            assertEquals(201, actualAddUserResult.getStatusCodeValue());
            assertTrue(actualAddUserResult.hasBody());
            assertTrue(actualAddUserResult.getHeaders().isEmpty());
        }
    }


    @Test
    void testAddUser5() throws IOException {
        try (MockedStatic<Files> mockFiles = mockStatic(Files.class)) {

            // Arrange
            mockFiles.when(() -> Files.exists(Mockito.<Path>any(), isA(LinkOption[].class))).thenReturn(true);
            mockFiles.when(() -> Files.createDirectories(Mockito.<Path>any(), isA(FileAttribute[].class)))
                    .thenReturn(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));
            mockFiles.when(() -> Files.copy(Mockito.<InputStream>any(), Mockito.<Path>any(), isA(CopyOption[].class)))
                    .thenReturn(1L);

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
            UserRepository userRepository = mock(UserRepository.class);
            when(userRepository.save(Mockito.<User>any())).thenReturn(user);
            ModelMapper modelMapper = new ModelMapper();

            UserService userservice = new UserService(modelMapper, new BCryptPasswordEncoder(), userRepository);
            RegisterDto dto = RegisterDto.builder()
                    .company("Company")
                    .email("jane.doe@example.org")
                    .firstname("Jane")
                    .lastname("Doe")
                    .password("iloveyou")
                    .phone("6625550144")
                    .role(Role.BUSINESSEXPERT)
                    .build();
            userservice.addUser(dto, new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))));
            UserController userController = new UserController(userservice, new ModelMapper());
            RegisterDto dto2 = RegisterDto.builder()
                    .company("Company")
                    .email("jane.doe@example.org")
                    .firstname("Jane")
                    .lastname("Doe")
                    .password("iloveyou")
                    .phone("6625550144")
                    .role(Role.BUSINESSEXPERT)
                    .build();

            // Act
            ResponseEntity<UserDto> actualAddUserResult = userController.addUser(dto2,
                    new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))));

            // Assert
            mockFiles.verify(() -> Files.copy(Mockito.<InputStream>any(), Mockito.<Path>any(), isA(CopyOption[].class)),
                    atLeast(1));
            mockFiles.verify(() -> Files.exists(Mockito.<Path>any(), isA(LinkOption[].class)), atLeast(1));
            verify(userRepository, atLeast(1)).save(Mockito.<User>any());
            UserDto body = actualAddUserResult.getBody();
            assertEquals("6625550144", body.getPhone());
            assertEquals("Company", body.getCompany());
            assertEquals("Doe", body.getLastname());
            assertEquals("Jane", body.getFirstname());
            assertEquals("assets\\demo\\images\\user-profiles", body.getProfileImagePath());
            assertEquals("jane.doe@example.org", body.getEmail());
            assertNull(body.getId());
            assertEquals(201, actualAddUserResult.getStatusCodeValue());
            assertEquals(Role.BUSINESSEXPERT, body.getRole());
            assertTrue(body.isActive());
            assertTrue(body.isNonExpired());
            assertTrue(actualAddUserResult.hasBody());
            assertTrue(actualAddUserResult.getHeaders().isEmpty());
        }
    }

    @Test
    void testDelete() throws Exception {
        // Arrange
        doNothing().when(userService).delete(Mockito.<Integer>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/users/{id}", 1);

        // Act and Assert
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testDelete2() throws Exception {
        // Arrange
        doNothing().when(userService).delete(Mockito.<Integer>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/users/{id}", "Uri Variables",
                "Uri Variables");

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(userController).build().perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(400));
    }

    @Test
    void testDelete3() throws Exception {
        // Arrange
        doNothing().when(userService).delete(Mockito.<Integer>any());
        SecurityMockMvcRequestBuilders.FormLoginRequestBuilder requestBuilder = SecurityMockMvcRequestBuilders.formLogin();

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(userController).build().perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }


    @Test
    void testDelete4() throws Exception {
        // Arrange
        doNothing().when(userService).delete(Mockito.<Integer>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/users/{id}", 1);
        requestBuilder.accept("https://example.org/example");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testDelete5() throws Exception {
        // Arrange
        doNothing().when(userService).delete(Mockito.<Integer>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/users/{id}", "",
                "Uri Variables");

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(userController).build().perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(405));
    }


    @Test
    void testDelete6() throws Exception {
        // Arrange
        doNothing().when(userService).delete(Mockito.<Integer>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/users/{id}", 1);
        requestBuilder.accept("");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    void testExpireUser() throws Exception {
        // Arrange
        doNothing().when(userService).expireUser(Mockito.<Integer>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/api/users/{id}/expire", 1);

        // Act and Assert
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("User expired successfully"));
    }

    @Test
    void testExpireUser2() throws Exception {
        // Arrange
        doNothing().when(userService).expireUser(Mockito.<Integer>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/api/users/{id}/expire",
                "Uri Variables", "Uri Variables");

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(userController).build().perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(400));
    }

    @Test
    void testExpireUser3() throws Exception {
        // Arrange
        doNothing().when(userService).expireUser(Mockito.<Integer>any());
        SecurityMockMvcRequestBuilders.FormLoginRequestBuilder requestBuilder = SecurityMockMvcRequestBuilders.formLogin();

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(userController).build().perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }


    @Test
    void testExpireUser4() throws Exception {
        // Arrange
        doNothing().when(userService).expireUser(Mockito.<Integer>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/api/users/{id}/expire", 1);
        requestBuilder.accept("https://example.org/example");

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(userController).build().perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(406));
    }


    @Test
    void testExpireUser5() throws Exception {
        // Arrange
        doNothing().when(userService).expireUser(Mockito.<Integer>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/api/users/{id}/expire", 1);
        requestBuilder.accept("");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("User expired successfully"));
    }


    @Test
    void testUnexpireUser() throws Exception {
        // Arrange
        doNothing().when(userService).unexpireUser(Mockito.<Integer>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/api/users/{id}/unexpire", 1);

        // Act and Assert
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("User unexpired successfully"));
    }


    @Test
    void testUnexpireUser2() throws Exception {
        // Arrange
        doNothing().when(userService).unexpireUser(Mockito.<Integer>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/api/users/{id}/unexpire",
                "Uri Variables", "Uri Variables");

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(userController).build().perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(400));
    }


    @Test
    void testUnexpireUser3() throws Exception {
        // Arrange
        doNothing().when(userService).unexpireUser(Mockito.<Integer>any());
        SecurityMockMvcRequestBuilders.FormLoginRequestBuilder requestBuilder = SecurityMockMvcRequestBuilders.formLogin();

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(userController).build().perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }


    @Test
    void testUnexpireUser4() throws Exception {
        // Arrange
        doNothing().when(userService).unexpireUser(Mockito.<Integer>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/api/users/{id}/unexpire", 1);
        requestBuilder.accept("https://example.org/example");

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(userController).build().perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(406));
    }

    @Test
    void testUnexpireUser5() throws Exception {
        // Arrange
        doNothing().when(userService).unexpireUser(Mockito.<Integer>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/api/users/{id}/unexpire", 1);
        requestBuilder.accept("");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("User unexpired successfully"));
    }

    @Test
    void testGetUser() throws Exception {
        // Arrange
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
        when(userService.findbyemail(Mockito.<String>any())).thenReturn(user);
        UserDto buildResult = UserDto.builder()
                .active(true)
                .company("Company")
                .email("jane.doe@example.org")
                .error("An error occurred")
                .firstname("Jane")
                .id(1L)
                .lastname("Doe")
                .nonExpired(true)
                .phone("6625550144")
                .profileImagePath("Profile Image Path")
                .role(Role.BUSINESSEXPERT)
                .build();
        when(modelMapper.map(Mockito.<Object>any(), Mockito.<Class<UserDto>>any())).thenReturn(buildResult);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/users/{email}",
                "jane.doe@example.org");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "{\"id\":1,\"firstname\":\"Jane\",\"lastname\":\"Doe\",\"email\":\"jane.doe@example.org\",\"active\":true,\"nonExpired"
                                        + "\":true,\"profileImagePath\":\"Profile Image Path\",\"phone\":\"6625550144\",\"company\":\"Company\",\"role\":"
                                        + "\"BUSINESSEXPERT\",\"error\":\"An error occurred\"}"));
    }


    @Test
    void testGetUser2() throws Exception {
        // Arrange
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
        when(userService.findbyemail(Mockito.<String>any())).thenReturn(user);
        UserDto buildResult = UserDto.builder()
                .active(true)
                .company("Company")
                .email("jane.doe@example.org")
                .error("An error occurred")
                .firstname("Jane")
                .id(1L)
                .lastname("Doe")
                .nonExpired(true)
                .phone("6625550144")
                .profileImagePath("Profile Image Path")
                .role(Role.BUSINESSEXPERT)
                .build();
        when(modelMapper.map(Mockito.<Object>any(), Mockito.<Class<UserDto>>any())).thenReturn(buildResult);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/users/{email}", "Uri Variables",
                "Uri Variables");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "{\"id\":1,\"firstname\":\"Jane\",\"lastname\":\"Doe\",\"email\":\"jane.doe@example.org\",\"active\":true,\"nonExpired"
                                        + "\":true,\"profileImagePath\":\"Profile Image Path\",\"phone\":\"6625550144\",\"company\":\"Company\",\"role\":"
                                        + "\"BUSINESSEXPERT\",\"error\":\"An error occurred\"}"));
    }


    @Test
    void testGetusers() throws Exception {
        // Arrange
        when(userService.getAll()).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/users");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }


    @Test
    void testGetusers2() throws Exception {
        // Arrange
        ArrayList<UserDto> userDtoList = new ArrayList<>();
        UserDto buildResult = UserDto.builder()
                .active(true)
                .company("Company")
                .email("jane.doe@example.org")
                .error("An error occurred")
                .firstname("Jane")
                .id(1L)
                .lastname("Doe")
                .nonExpired(true)
                .phone("6625550144")
                .profileImagePath("Profile Image Path")
                .role(Role.BUSINESSEXPERT)
                .build();
        userDtoList.add(buildResult);
        when(userService.getAll()).thenReturn(userDtoList);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/users");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "[{\"id\":1,\"firstname\":\"Jane\",\"lastname\":\"Doe\",\"email\":\"jane.doe@example.org\",\"active\":true,\"nonExpired"
                                        + "\":true,\"profileImagePath\":\"Profile Image Path\",\"phone\":\"6625550144\",\"company\":\"Company\",\"role\":"
                                        + "\"BUSINESSEXPERT\",\"error\":\"An error occurred\"}]"));
    }

    @Test
    void testGetusers3() throws Exception {
        // Arrange
        ArrayList<UserDto> userDtoList = new ArrayList<>();
        UserDto buildResult = UserDto.builder()
                .active(true)
                .company("Company")
                .email("jane.doe@example.org")
                .error("An error occurred")
                .firstname("Jane")
                .id(1L)
                .lastname("Doe")
                .nonExpired(true)
                .phone("6625550144")
                .profileImagePath("Profile Image Path")
                .role(Role.BUSINESSEXPERT)
                .build();
        userDtoList.add(buildResult);
        UserDto buildResult2 = UserDto.builder()
                .active(true)
                .company("Company")
                .email("jane.doe@example.org")
                .error("An error occurred")
                .firstname("Jane")
                .id(1L)
                .lastname("Doe")
                .nonExpired(true)
                .phone("6625550144")
                .profileImagePath("Profile Image Path")
                .role(Role.BUSINESSEXPERT)
                .build();
        userDtoList.add(buildResult2);
        when(userService.getAll()).thenReturn(userDtoList);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/users");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "[{\"id\":1,\"firstname\":\"Jane\",\"lastname\":\"Doe\",\"email\":\"jane.doe@example.org\",\"active\":true,\"nonExpired"
                                        + "\":true,\"profileImagePath\":\"Profile Image Path\",\"phone\":\"6625550144\",\"company\":\"Company\",\"role\":"
                                        + "\"BUSINESSEXPERT\",\"error\":\"An error occurred\"},{\"id\":1,\"firstname\":\"Jane\",\"lastname\":\"Doe\",\"email\":"
                                        + "\"jane.doe@example.org\",\"active\":true,\"nonExpired\":true,\"profileImagePath\":\"Profile Image Path\",\"phone"
                                        + "\":\"6625550144\",\"company\":\"Company\",\"role\":\"BUSINESSEXPERT\",\"error\":\"An error occurred\"}]"));
    }

    @Test
    void testGetusers4() throws Exception {
        // Arrange
        when(userService.getAll()).thenReturn(new ArrayList<>());
        SecurityMockMvcRequestBuilders.FormLoginRequestBuilder requestBuilder = SecurityMockMvcRequestBuilders.formLogin();

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(userController).build().perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }


    @Test
    void testGetusers5() throws Exception {
        // Arrange
        when(userService.getAll()).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/users");
        requestBuilder.accept("https://example.org/example");

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(userController).build().perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(406));
    }

    @Test
    void testGetusers6() throws Exception {
        // Arrange
        when(userService.getAll()).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/users");
        requestBuilder.accept("");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }


    @Test
    void testUpdateUser() throws Exception {
        // Arrange
        MockHttpServletRequestBuilder paramResult = MockMvcRequestBuilders
                .put("/api/users/{id}", "Uri Variables", "Uri Variables")
                .param("dto", "foo");
        MockHttpServletRequestBuilder requestBuilder = paramResult.param("file",
                String.valueOf(new MockMultipartFile("Name", (InputStream) null)));

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(userController).build().perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(400));
    }


    @Test
    void testUpdateUser2() throws Exception {
        // Arrange
        DataInputStream contentStream = mock(DataInputStream.class);
        when(contentStream.readAllBytes()).thenReturn("AXAXAXAX".getBytes("UTF-8"));
        doNothing().when(contentStream).close();
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/api/users/{id}", "Uri Variables", "Uri Variables")
                .param("dto", "foo")
                .param("file", String.valueOf(new MockMultipartFile("Name", contentStream)));

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(userController).build().perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(400));
    }
}
