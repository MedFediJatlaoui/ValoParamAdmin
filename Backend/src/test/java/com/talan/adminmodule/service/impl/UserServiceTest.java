package com.talan.adminmodule.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sun.security.auth.UserPrincipal;
import com.talan.adminmodule.dto.ChangePassword;
import com.talan.adminmodule.dto.RegisterDto;
import com.talan.adminmodule.dto.UserDto;
import com.talan.adminmodule.entity.Role;
import com.talan.adminmodule.entity.User;
import com.talan.adminmodule.repository.UserRepository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

@ContextConfiguration(classes = {UserService.class, PasswordEncoder.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class UserServiceTest {
    @MockBean
    private ModelMapper modelMapper;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserService userService;


    @Test
    void testMapUserToDto() {

        // Arrange
        ModelMapper modelMapper = new ModelMapper();
        UserService userService = new UserService(modelMapper, new BCryptPasswordEncoder(), mock(UserRepository.class));

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

        // Act
        UserDto actualMapUserToDtoResult = userService.mapUserToDto(user);

        // Assert
        assertEquals("6625550144", actualMapUserToDtoResult.getPhone());
        assertEquals("Company", actualMapUserToDtoResult.getCompany());
        assertEquals("Doe", actualMapUserToDtoResult.getLastname());
        assertEquals("Jane", actualMapUserToDtoResult.getFirstname());
        assertEquals("Profile Image Path", actualMapUserToDtoResult.getProfileImagePath());
        assertEquals("jane.doe@example.org", actualMapUserToDtoResult.getEmail());
        assertEquals(1L, actualMapUserToDtoResult.getId().longValue());
        assertEquals(Role.BUSINESSEXPERT, actualMapUserToDtoResult.getRole());
        assertTrue(actualMapUserToDtoResult.isActive());
        assertTrue(actualMapUserToDtoResult.isNonExpired());
    }


    @Test
    void testMapUserToDto2() {

        // Arrange
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
        UserService userService = new UserService(modelMapper, new BCryptPasswordEncoder(), mock(UserRepository.class));

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

        // Act
        userService.mapUserToDto(user);

        // Assert
        verify(modelMapper).map(isA(Object.class), isA(Class.class));
    }

    @Test
    void testStoreProfileImage() throws IOException {

        // Arrange
        ModelMapper modelMapper = new ModelMapper();

        // Act and Assert
        assertEquals("", (new UserService(modelMapper, new BCryptPasswordEncoder(), mock(UserRepository.class)))
                .storeProfileImage(null));
    }


    @Test
    void testStoreProfileImage2() throws IOException {
        try (MockedStatic<Files> mockFiles = mockStatic(Files.class)) {

            // Arrange
            mockFiles.when(() -> Files.exists(Mockito.<Path>any(), isA(LinkOption[].class))).thenReturn(true);
            mockFiles.when(() -> Files.createDirectories(Mockito.<Path>any(), isA(FileAttribute[].class)))
                    .thenReturn(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));
            mockFiles.when(() -> Files.copy(Mockito.<InputStream>any(), Mockito.<Path>any(), isA(CopyOption[].class)))
                    .thenReturn(1L);
            ModelMapper modelMapper = new ModelMapper();
            UserService userService = new UserService(modelMapper, new BCryptPasswordEncoder(), mock(UserRepository.class));

            // Act
            String actualStoreProfileImageResult = userService
                    .storeProfileImage(new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))));

            // Assert
            mockFiles.verify(() -> Files.copy(Mockito.<InputStream>any(), Mockito.<Path>any(), isA(CopyOption[].class)));
            mockFiles.verify(() -> Files.exists(Mockito.<Path>any(), isA(LinkOption[].class)));
            assertEquals("assets\\demo\\images\\user-profiles", actualStoreProfileImageResult);
        }
    }


    @Test
    void testStoreProfileImage3() throws IOException {
        try (MockedStatic<Files> mockFiles = mockStatic(Files.class)) {

            // Arrange
            mockFiles.when(() -> Files.exists(Mockito.<Path>any(), isA(LinkOption[].class))).thenReturn(false);
            mockFiles.when(() -> Files.createDirectories(Mockito.<Path>any(), isA(FileAttribute[].class)))
                    .thenReturn(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));
            mockFiles.when(() -> Files.copy(Mockito.<InputStream>any(), Mockito.<Path>any(), isA(CopyOption[].class)))
                    .thenReturn(1L);
            ModelMapper modelMapper = new ModelMapper();
            UserService userService = new UserService(modelMapper, new BCryptPasswordEncoder(), mock(UserRepository.class));

            // Act
            String actualStoreProfileImageResult = userService
                    .storeProfileImage(new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))));

            // Assert
            mockFiles.verify(() -> Files.copy(Mockito.<InputStream>any(), Mockito.<Path>any(), isA(CopyOption[].class)));
            mockFiles.verify(() -> Files.createDirectories(Mockito.<Path>any(), isA(FileAttribute[].class)));
            mockFiles.verify(() -> Files.exists(Mockito.<Path>any(), isA(LinkOption[].class)));
            assertEquals("assets\\demo\\images\\user-profiles", actualStoreProfileImageResult);
        }
    }


    @Test
    void testStoreProfileImage4() throws IOException {
        try (MockedStatic<Files> mockFiles = mockStatic(Files.class)) {

            // Arrange
            mockFiles.when(() -> Files.exists(Mockito.<Path>any(), isA(LinkOption[].class))).thenReturn(true);
            mockFiles.when(() -> Files.createDirectories(Mockito.<Path>any(), isA(FileAttribute[].class)))
                    .thenReturn(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));
            mockFiles.when(() -> Files.copy(Mockito.<InputStream>any(), Mockito.<Path>any(), isA(CopyOption[].class)))
                    .thenReturn(1L);
            ModelMapper modelMapper = new ModelMapper();
            UserService userService = new UserService(modelMapper, new BCryptPasswordEncoder(), mock(UserRepository.class));

            // Act and Assert
            assertEquals("",
                    userService.storeProfileImage(new MockMultipartFile("Name", new ByteArrayInputStream(new byte[]{}))));
        }
    }


    @Test
    void testStoreProfileImage5() throws IOException {
        try (MockedStatic<Files> mockFiles = mockStatic(Files.class)) {

            // Arrange
            mockFiles.when(() -> Files.exists(Mockito.<Path>any(), isA(LinkOption[].class))).thenReturn(true);
            mockFiles.when(() -> Files.createDirectories(Mockito.<Path>any(), isA(FileAttribute[].class)))
                    .thenReturn(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));
            mockFiles.when(() -> Files.copy(Mockito.<InputStream>any(), Mockito.<Path>any(), isA(CopyOption[].class)))
                    .thenReturn(1L);
            ModelMapper modelMapper = new ModelMapper();
            UserService userService = new UserService(modelMapper, new BCryptPasswordEncoder(), mock(UserRepository.class));

            // Act
            String actualStoreProfileImageResult = userService.storeProfileImage(new MockMultipartFile("user.dir", "foo.txt",
                    "text/plain", new ByteArrayInputStream(new byte[]{'A', 7, 'A', 7, 'A', 7, 'A', 7})));

            // Assert
            mockFiles.verify(() -> Files.copy(Mockito.<InputStream>any(), Mockito.<Path>any(), isA(CopyOption[].class)));
            mockFiles.verify(() -> Files.exists(Mockito.<Path>any(), isA(LinkOption[].class)));
            assertEquals("assets\\demo\\images\\user-profiles\\foo.txt", actualStoreProfileImageResult);
        }
    }

    @Test
    void testStoreProfileImage6() throws IOException {
        try (MockedStatic<Files> mockFiles = mockStatic(Files.class)) {

            // Arrange
            mockFiles.when(() -> Files.exists(Mockito.<Path>any(), isA(LinkOption[].class))).thenReturn(true);
            mockFiles.when(() -> Files.createDirectories(Mockito.<Path>any(), isA(FileAttribute[].class)))
                    .thenReturn(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));
            mockFiles.when(() -> Files.copy(Mockito.<InputStream>any(), Mockito.<Path>any(), isA(CopyOption[].class)))
                    .thenReturn(1L);
            ModelMapper modelMapper = new ModelMapper();
            UserService userService = new UserService(modelMapper, new BCryptPasswordEncoder(), mock(UserRepository.class));
            MultipartFile profileImage = mock(MultipartFile.class);
            when(profileImage.getInputStream()).thenReturn(null);
            when(profileImage.isEmpty()).thenReturn(false);
            when(profileImage.getOriginalFilename()).thenReturn("foo.txt");

            // Act
            String actualStoreProfileImageResult = userService.storeProfileImage(profileImage);

            // Assert
            mockFiles.verify(() -> Files.copy(Mockito.<InputStream>any(), Mockito.<Path>any(), isA(CopyOption[].class)));
            mockFiles.verify(() -> Files.exists(Mockito.<Path>any(), isA(LinkOption[].class)));
            verify(profileImage).getInputStream();
            verify(profileImage).getOriginalFilename();
            verify(profileImage).isEmpty();
            assertEquals("assets\\demo\\images\\user-profiles\\foo.txt", actualStoreProfileImageResult);
        }
    }

    @Test
    void testStoreProfileImage7() throws IOException {
        try (MockedStatic<Files> mockFiles = mockStatic(Files.class)) {

            // Arrange
            mockFiles.when(() -> Files.exists(Mockito.<Path>any(), isA(LinkOption[].class))).thenReturn(true);
            mockFiles.when(() -> Files.createDirectories(Mockito.<Path>any(), isA(FileAttribute[].class)))
                    .thenReturn(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));
            mockFiles.when(() -> Files.copy(Mockito.<InputStream>any(), Mockito.<Path>any(), isA(CopyOption[].class)))
                    .thenReturn(1L);
            ModelMapper modelMapper = new ModelMapper();
            UserService userService = new UserService(modelMapper, new BCryptPasswordEncoder(), mock(UserRepository.class));
            MultipartFile profileImage = mock(MultipartFile.class);
            when(profileImage.getInputStream()).thenReturn(new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8")));
            when(profileImage.isEmpty()).thenReturn(false);
            when(profileImage.getOriginalFilename()).thenReturn("foo");

            // Act
            String actualStoreProfileImageResult = userService.storeProfileImage(profileImage);

            // Assert
            mockFiles.verify(() -> Files.copy(Mockito.<InputStream>any(), Mockito.<Path>any(), isA(CopyOption[].class)));
            mockFiles.verify(() -> Files.exists(Mockito.<Path>any(), isA(LinkOption[].class)));
            verify(profileImage).getInputStream();
            verify(profileImage).getOriginalFilename();
            verify(profileImage).isEmpty();
            assertEquals("assets\\demo\\images\\user-profiles\\foo", actualStoreProfileImageResult);
        }
    }


    @Test
    void testStoreProfileImage8() throws IOException {
        try (MockedStatic<Files> mockFiles = mockStatic(Files.class)) {

            // Arrange
            mockFiles.when(() -> Files.exists(Mockito.<Path>any(), isA(LinkOption[].class))).thenReturn(true);
            mockFiles.when(() -> Files.createDirectories(Mockito.<Path>any(), isA(FileAttribute[].class)))
                    .thenReturn(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));
            mockFiles.when(() -> Files.copy(Mockito.<InputStream>any(), Mockito.<Path>any(), isA(CopyOption[].class)))
                    .thenReturn(1L);
            ModelMapper modelMapper = new ModelMapper();
            UserService userService = new UserService(modelMapper, new BCryptPasswordEncoder(), mock(UserRepository.class));
            MultipartFile profileImage = mock(MultipartFile.class);
            when(profileImage.getInputStream()).thenThrow(new IOException("user.dir"));
            when(profileImage.isEmpty()).thenReturn(false);
            when(profileImage.getOriginalFilename()).thenReturn("foo.txt");

            // Act and Assert
            assertThrows(IOException.class, () -> userService.storeProfileImage(profileImage));
            mockFiles.verify(() -> Files.exists(Mockito.<Path>any(), isA(LinkOption[].class)));
            verify(profileImage).getInputStream();
            verify(profileImage).getOriginalFilename();
            verify(profileImage).isEmpty();
        }
    }

    @Test
    void testStoreProfileImage9() throws IOException {
        try (MockedStatic<Files> mockFiles = mockStatic(Files.class)) {

            // Arrange
            mockFiles.when(() -> Files.exists(Mockito.<Path>any(), isA(LinkOption[].class))).thenReturn(true);
            mockFiles.when(() -> Files.createDirectories(Mockito.<Path>any(), isA(FileAttribute[].class)))
                    .thenReturn(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));
            mockFiles.when(() -> Files.copy(Mockito.<InputStream>any(), Mockito.<Path>any(), isA(CopyOption[].class)))
                    .thenReturn(1L);
            ModelMapper modelMapper = new ModelMapper();
            UserService userService = new UserService(modelMapper, new BCryptPasswordEncoder(), mock(UserRepository.class));
            MultipartFile profileImage = mock(MultipartFile.class);
            when(profileImage.getInputStream()).thenThrow(new IOException("user.dir"));
            when(profileImage.isEmpty()).thenReturn(false);
            when(profileImage.getOriginalFilename()).thenReturn("..");

            // Act and Assert
            assertThrows(IOException.class, () -> userService.storeProfileImage(profileImage));
            mockFiles.verify(() -> Files.exists(Mockito.<Path>any(), isA(LinkOption[].class)));
            verify(profileImage).getInputStream();
            verify(profileImage).getOriginalFilename();
            verify(profileImage).isEmpty();
        }
    }

    @Test
    void testAddUser() throws IOException {
        try (MockedStatic<Files> mockFiles = mockStatic(Files.class)) {
            // Arrange
            mockFiles.when(() -> Files.exists(Mockito.<Path>any(), isA(LinkOption[].class))).thenReturn(true);
            mockFiles.when(() -> Files.createDirectories(Mockito.<Path>any(), isA(FileAttribute[].class)))
                    .thenReturn(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));
            mockFiles.when(() -> Files.copy(Mockito.<InputStream>any(), Mockito.<Path>any(), isA(CopyOption[].class)))
                    .thenReturn(1L);
            when(passwordEncoder.encode(Mockito.<CharSequence>any())).thenReturn("secret");
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
            when(userRepository.save(Mockito.<User>any())).thenReturn(user);
            RegisterDto dto = new RegisterDto("Jane", "Doe", "iloveyou", "jane.doe@example.org", "Company", "6625550144",
                    Role.BUSINESSEXPERT);

            // Act
            userService.addUser(dto, new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))));

            // Assert
            mockFiles.verify(() -> Files.copy(Mockito.<InputStream>any(), Mockito.<Path>any(), isA(CopyOption[].class)));
            mockFiles.verify(() -> Files.exists(Mockito.<Path>any(), isA(LinkOption[].class)));
            verify(modelMapper).map(isA(Object.class), isA(Class.class));
            verify(userRepository).save(isA(User.class));
            verify(passwordEncoder).encode(isA(CharSequence.class));
        }
    }

    @Test
    void testGetAll() {

        // Arrange
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        ModelMapper modelMapper = new ModelMapper();

        // Act
        List<UserDto> actualAll = (new UserService(modelMapper, new BCryptPasswordEncoder(), userRepository)).getAll();

        // Assert
        verify(userRepository).findAll();
        assertTrue(actualAll.isEmpty());
    }

    @Test
    void testGetAll2() {

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

        ArrayList<User> userList = new ArrayList<>();
        userList.add(user);
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findAll()).thenReturn(userList);
        ModelMapper modelMapper = new ModelMapper();

        // Act
        List<UserDto> actualAll = (new UserService(modelMapper, new BCryptPasswordEncoder(), userRepository)).getAll();

        // Assert
        verify(userRepository).findAll();
        assertEquals(1, actualAll.size());
    }


    @Test
    void testGetAll3() {

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

        User user2 = new User();
        user2.setActive(false);
        user2.setCompany("com.talan.adminmodule.entity.User");
        user2.setEmail("john.smith@example.org");
        user2.setFirstname("John");
        user2.setId(2);
        user2.setLastname("Smith");
        user2.setNonExpired(false);
        user2.setPassword("Password");
        user2.setPhone("8605550118");
        user2.setProfileImagePath("com.talan.adminmodule.entity.User");
        user2.setRole(Role.ADMIN);

        ArrayList<User> userList = new ArrayList<>();
        userList.add(user2);
        userList.add(user);
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findAll()).thenReturn(userList);
        ModelMapper modelMapper = new ModelMapper();

        // Act
        List<UserDto> actualAll = (new UserService(modelMapper, new BCryptPasswordEncoder(), userRepository)).getAll();

        // Assert
        verify(userRepository).findAll();
        assertEquals(2, actualAll.size());
    }

    @Test
    void testGetAll4() {

        // Arrange
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

        ArrayList<User> userList = new ArrayList<>();
        userList.add(user);
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findAll()).thenReturn(userList);

        // Act
        List<UserDto> actualAll = (new UserService(modelMapper, new BCryptPasswordEncoder(), userRepository)).getAll();

        // Assert
        verify(modelMapper).map(isA(Object.class), isA(Class.class));
        verify(userRepository).findAll();
        assertEquals(1, actualAll.size());
    }


    @Test
    void testUpdate() throws IOException {
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
            Optional<User> ofResult = Optional.of(user);

            User user2 = new User();
            user2.setActive(true);
            user2.setCompany("Company");
            user2.setEmail("jane.doe@example.org");
            user2.setFirstname("Jane");
            user2.setId(1);
            user2.setLastname("Doe");
            user2.setNonExpired(true);
            user2.setPassword("iloveyou");
            user2.setPhone("6625550144");
            user2.setProfileImagePath("Profile Image Path");
            user2.setRole(Role.BUSINESSEXPERT);
            UserRepository userRepository = mock(UserRepository.class);
            when(userRepository.save(Mockito.<User>any())).thenReturn(user2);
            when(userRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);
            ModelMapper modelMapper = new ModelMapper();
            UserService userService = new UserService(modelMapper, new BCryptPasswordEncoder(), userRepository);
            RegisterDto dto = new RegisterDto("Jane", "Doe", "iloveyou", "jane.doe@example.org", "Company", "6625550144",
                    Role.BUSINESSEXPERT);

            // Act
            UserDto actualUpdateResult = userService.update(1, dto,
                    new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))));

            // Assert
            mockFiles.verify(() -> Files.copy(Mockito.<InputStream>any(), Mockito.<Path>any(), isA(CopyOption[].class)));
            mockFiles.verify(() -> Files.exists(Mockito.<Path>any(), isA(LinkOption[].class)));
            verify(userRepository).findById(1);
            verify(userRepository).save(isA(User.class));
            assertEquals("6625550144", actualUpdateResult.getPhone());
            assertEquals("Company", actualUpdateResult.getCompany());
            assertEquals("Doe", actualUpdateResult.getLastname());
            assertEquals("Jane", actualUpdateResult.getFirstname());
            assertEquals("assets\\demo\\images\\user-profiles", actualUpdateResult.getProfileImagePath());
            assertEquals("jane.doe@example.org", actualUpdateResult.getEmail());
            assertEquals(1L, actualUpdateResult.getId().longValue());
            assertEquals(Role.BUSINESSEXPERT, actualUpdateResult.getRole());
            assertTrue(actualUpdateResult.isActive());
            assertTrue(actualUpdateResult.isNonExpired());
        }
    }

    @Test
    void testUpdate2() throws IOException {
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
            Optional<User> ofResult = Optional.of(user);

            User user2 = new User();
            user2.setActive(true);
            user2.setCompany("Company");
            user2.setEmail("jane.doe@example.org");
            user2.setFirstname("Jane");
            user2.setId(1);
            user2.setLastname("Doe");
            user2.setNonExpired(true);
            user2.setPassword("iloveyou");
            user2.setPhone("6625550144");
            user2.setProfileImagePath("Profile Image Path");
            user2.setRole(Role.BUSINESSEXPERT);
            UserRepository userRepository = mock(UserRepository.class);
            when(userRepository.save(Mockito.<User>any())).thenReturn(user2);
            when(userRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);
            ModelMapper modelMapper = new ModelMapper();
            UserService userService = new UserService(modelMapper, new BCryptPasswordEncoder(), userRepository);
            RegisterDto dto = new RegisterDto("Jane", "Doe", "iloveyou", "jane.doe@example.org", "Company", "6625550144",
                    Role.BUSINESSEXPERT);

            // Act
            UserDto actualUpdateResult = userService.update(1, dto,
                    new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))));

            // Assert
            mockFiles.verify(() -> Files.copy(Mockito.<InputStream>any(), Mockito.<Path>any(), isA(CopyOption[].class)));
            mockFiles.verify(() -> Files.createDirectories(Mockito.<Path>any(), isA(FileAttribute[].class)));
            mockFiles.verify(() -> Files.exists(Mockito.<Path>any(), isA(LinkOption[].class)));
            verify(userRepository).findById(1);
            verify(userRepository).save(isA(User.class));
            assertEquals("6625550144", actualUpdateResult.getPhone());
            assertEquals("Company", actualUpdateResult.getCompany());
            assertEquals("Doe", actualUpdateResult.getLastname());
            assertEquals("Jane", actualUpdateResult.getFirstname());
            assertEquals("assets\\demo\\images\\user-profiles", actualUpdateResult.getProfileImagePath());
            assertEquals("jane.doe@example.org", actualUpdateResult.getEmail());
            assertEquals(1L, actualUpdateResult.getId().longValue());
            assertEquals(Role.BUSINESSEXPERT, actualUpdateResult.getRole());
            assertTrue(actualUpdateResult.isActive());
            assertTrue(actualUpdateResult.isNonExpired());
        }
    }


    @Test
    void testUpdate3() throws IOException {
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
            Optional<User> ofResult = Optional.of(user);

            User user2 = new User();
            user2.setActive(true);
            user2.setCompany("Company");
            user2.setEmail("jane.doe@example.org");
            user2.setFirstname("Jane");
            user2.setId(1);
            user2.setLastname("Doe");
            user2.setNonExpired(true);
            user2.setPassword("iloveyou");
            user2.setPhone("6625550144");
            user2.setProfileImagePath("Profile Image Path");
            user2.setRole(Role.BUSINESSEXPERT);
            UserRepository userRepository = mock(UserRepository.class);
            when(userRepository.save(Mockito.<User>any())).thenReturn(user2);
            when(userRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);
            UserService userService = new UserService(modelMapper, new BCryptPasswordEncoder(), userRepository);
            RegisterDto dto = new RegisterDto("Jane", "Doe", "iloveyou", "jane.doe@example.org", "Company", "6625550144",
                    Role.BUSINESSEXPERT);

            // Act
            userService.update(1, dto, new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))));

            // Assert
            mockFiles.verify(() -> Files.copy(Mockito.<InputStream>any(), Mockito.<Path>any(), isA(CopyOption[].class)));
            mockFiles.verify(() -> Files.exists(Mockito.<Path>any(), isA(LinkOption[].class)));
            verify(modelMapper).map(isA(Object.class), isA(Class.class));
            verify(userRepository).findById(1);
            verify(userRepository).save(isA(User.class));
        }
    }
    @Test
    void testFindbyemail() {

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
        Optional<User> ofResult = Optional.of(user);
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findByEmail(Mockito.<String>any())).thenReturn(ofResult);
        ModelMapper modelMapper = new ModelMapper();

        // Act
        User actualFindbyemailResult = (new UserService(modelMapper, new BCryptPasswordEncoder(), userRepository))
                .findbyemail("jane.doe@example.org");

        // Assert
        verify(userRepository).findByEmail("jane.doe@example.org");
        assertSame(user, actualFindbyemailResult);
    }


    @Test
    void testDelete() {

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
        Optional<User> ofResult = Optional.of(user);

        User user2 = new User();
        user2.setActive(true);
        user2.setCompany("Company");
        user2.setEmail("jane.doe@example.org");
        user2.setFirstname("Jane");
        user2.setId(1);
        user2.setLastname("Doe");
        user2.setNonExpired(true);
        user2.setPassword("iloveyou");
        user2.setPhone("6625550144");
        user2.setProfileImagePath("Profile Image Path");
        user2.setRole(Role.BUSINESSEXPERT);
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.save(Mockito.<User>any())).thenReturn(user2);
        when(userRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);
        ModelMapper modelMapper = new ModelMapper();

        // Act
        (new UserService(modelMapper, new BCryptPasswordEncoder(), userRepository)).delete(1);

        // Assert
        verify(userRepository).findById(1);
        verify(userRepository).save(isA(User.class));
    }


    @Test
    void testDelete2() {

        // Arrange
        User user = mock(User.class);
        doNothing().when(user).setActive(anyBoolean());
        doNothing().when(user).setCompany(Mockito.<String>any());
        doNothing().when(user).setEmail(Mockito.<String>any());
        doNothing().when(user).setFirstname(Mockito.<String>any());
        doNothing().when(user).setId(Mockito.<Integer>any());
        doNothing().when(user).setLastname(Mockito.<String>any());
        doNothing().when(user).setNonExpired(anyBoolean());
        doNothing().when(user).setPassword(Mockito.<String>any());
        doNothing().when(user).setPhone(Mockito.<String>any());
        doNothing().when(user).setProfileImagePath(Mockito.<String>any());
        doNothing().when(user).setRole(Mockito.<Role>any());
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

        User user2 = new User();
        user2.setActive(true);
        user2.setCompany("Company");
        user2.setEmail("jane.doe@example.org");
        user2.setFirstname("Jane");
        user2.setId(1);
        user2.setLastname("Doe");
        user2.setNonExpired(true);
        user2.setPassword("iloveyou");
        user2.setPhone("6625550144");
        user2.setProfileImagePath("Profile Image Path");
        user2.setRole(Role.BUSINESSEXPERT);
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.save(Mockito.<User>any())).thenReturn(user2);
        when(userRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);
        ModelMapper modelMapper = new ModelMapper();

        // Act
        (new UserService(modelMapper, new BCryptPasswordEncoder(), userRepository)).delete(1);

        // Assert
        verify(user, atLeast(1)).setActive(anyBoolean());
        verify(user).setCompany("Company");
        verify(user).setEmail("jane.doe@example.org");
        verify(user).setFirstname("Jane");
        verify(user).setId(1);
        verify(user).setLastname("Doe");
        verify(user).setNonExpired(true);
        verify(user).setPassword("iloveyou");
        verify(user).setPhone("6625550144");
        verify(user).setProfileImagePath("Profile Image Path");
        verify(user).setRole(Role.BUSINESSEXPERT);
        verify(userRepository).findById(1);
        verify(userRepository).save(isA(User.class));
    }

    @Test
    void testDelete3() {

        // Arrange
        UserRepository userRepository = mock(UserRepository.class);
        Optional<User> emptyResult = Optional.empty();
        when(userRepository.findById(Mockito.<Integer>any())).thenReturn(emptyResult);
        ModelMapper modelMapper = new ModelMapper();

        // Act
        (new UserService(modelMapper, new BCryptPasswordEncoder(), userRepository)).delete(1);

        // Assert
        verify(userRepository).findById(1);
    }

    @Test
    void testExpireUser() {

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
        Optional<User> ofResult = Optional.of(user);

        User user2 = new User();
        user2.setActive(true);
        user2.setCompany("Company");
        user2.setEmail("jane.doe@example.org");
        user2.setFirstname("Jane");
        user2.setId(1);
        user2.setLastname("Doe");
        user2.setNonExpired(true);
        user2.setPassword("iloveyou");
        user2.setPhone("6625550144");
        user2.setProfileImagePath("Profile Image Path");
        user2.setRole(Role.BUSINESSEXPERT);
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.save(Mockito.<User>any())).thenReturn(user2);
        when(userRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);
        ModelMapper modelMapper = new ModelMapper();

        // Act
        (new UserService(modelMapper, new BCryptPasswordEncoder(), userRepository)).expireUser(1);

        // Assert
        verify(userRepository).findById(1);
        verify(userRepository).save(isA(User.class));
    }


    @Test
    void testExpireUser2() {

        // Arrange
        User user = mock(User.class);
        doNothing().when(user).setActive(anyBoolean());
        doNothing().when(user).setCompany(Mockito.<String>any());
        doNothing().when(user).setEmail(Mockito.<String>any());
        doNothing().when(user).setFirstname(Mockito.<String>any());
        doNothing().when(user).setId(Mockito.<Integer>any());
        doNothing().when(user).setLastname(Mockito.<String>any());
        doNothing().when(user).setNonExpired(anyBoolean());
        doNothing().when(user).setPassword(Mockito.<String>any());
        doNothing().when(user).setPhone(Mockito.<String>any());
        doNothing().when(user).setProfileImagePath(Mockito.<String>any());
        doNothing().when(user).setRole(Mockito.<Role>any());
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

        User user2 = new User();
        user2.setActive(true);
        user2.setCompany("Company");
        user2.setEmail("jane.doe@example.org");
        user2.setFirstname("Jane");
        user2.setId(1);
        user2.setLastname("Doe");
        user2.setNonExpired(true);
        user2.setPassword("iloveyou");
        user2.setPhone("6625550144");
        user2.setProfileImagePath("Profile Image Path");
        user2.setRole(Role.BUSINESSEXPERT);
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.save(Mockito.<User>any())).thenReturn(user2);
        when(userRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);
        ModelMapper modelMapper = new ModelMapper();

        // Act
        (new UserService(modelMapper, new BCryptPasswordEncoder(), userRepository)).expireUser(1);

        // Assert
        verify(user).setActive(true);
        verify(user).setCompany("Company");
        verify(user).setEmail("jane.doe@example.org");
        verify(user).setFirstname("Jane");
        verify(user).setId(1);
        verify(user).setLastname("Doe");
        verify(user, atLeast(1)).setNonExpired(anyBoolean());
        verify(user).setPassword("iloveyou");
        verify(user).setPhone("6625550144");
        verify(user).setProfileImagePath("Profile Image Path");
        verify(user).setRole(Role.BUSINESSEXPERT);
        verify(userRepository).findById(1);
        verify(userRepository).save(isA(User.class));
    }
    @Test
    void testExpireUser3() {

        // Arrange
        UserRepository userRepository = mock(UserRepository.class);
        Optional<User> emptyResult = Optional.empty();
        when(userRepository.findById(Mockito.<Integer>any())).thenReturn(emptyResult);
        ModelMapper modelMapper = new ModelMapper();

        // Act
        (new UserService(modelMapper, new BCryptPasswordEncoder(), userRepository)).expireUser(1);

        // Assert
        verify(userRepository).findById(1);
    }



    @Test
    void testUnexpireUser() {

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
        Optional<User> ofResult = Optional.of(user);

        User user2 = new User();
        user2.setActive(true);
        user2.setCompany("Company");
        user2.setEmail("jane.doe@example.org");
        user2.setFirstname("Jane");
        user2.setId(1);
        user2.setLastname("Doe");
        user2.setNonExpired(true);
        user2.setPassword("iloveyou");
        user2.setPhone("6625550144");
        user2.setProfileImagePath("Profile Image Path");
        user2.setRole(Role.BUSINESSEXPERT);
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.save(Mockito.<User>any())).thenReturn(user2);
        when(userRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);
        ModelMapper modelMapper = new ModelMapper();

        // Act
        (new UserService(modelMapper, new BCryptPasswordEncoder(), userRepository)).unexpireUser(1);

        // Assert
        verify(userRepository).findById(1);
        verify(userRepository).save(isA(User.class));
    }


    @Test
    void testUnexpireUser2() {

        // Arrange
        User user = mock(User.class);
        doNothing().when(user).setActive(anyBoolean());
        doNothing().when(user).setCompany(Mockito.<String>any());
        doNothing().when(user).setEmail(Mockito.<String>any());
        doNothing().when(user).setFirstname(Mockito.<String>any());
        doNothing().when(user).setId(Mockito.<Integer>any());
        doNothing().when(user).setLastname(Mockito.<String>any());
        doNothing().when(user).setNonExpired(anyBoolean());
        doNothing().when(user).setPassword(Mockito.<String>any());
        doNothing().when(user).setPhone(Mockito.<String>any());
        doNothing().when(user).setProfileImagePath(Mockito.<String>any());
        doNothing().when(user).setRole(Mockito.<Role>any());
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

        User user2 = new User();
        user2.setActive(true);
        user2.setCompany("Company");
        user2.setEmail("jane.doe@example.org");
        user2.setFirstname("Jane");
        user2.setId(1);
        user2.setLastname("Doe");
        user2.setNonExpired(true);
        user2.setPassword("iloveyou");
        user2.setPhone("6625550144");
        user2.setProfileImagePath("Profile Image Path");
        user2.setRole(Role.BUSINESSEXPERT);
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.save(Mockito.<User>any())).thenReturn(user2);
        when(userRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);
        ModelMapper modelMapper = new ModelMapper();

        // Act
        (new UserService(modelMapper, new BCryptPasswordEncoder(), userRepository)).unexpireUser(1);

        // Assert
        verify(user).setActive(true);
        verify(user).setCompany("Company");
        verify(user).setEmail("jane.doe@example.org");
        verify(user).setFirstname("Jane");
        verify(user).setId(1);
        verify(user).setLastname("Doe");
        verify(user, atLeast(1)).setNonExpired(true);
        verify(user).setPassword("iloveyou");
        verify(user).setPhone("6625550144");
        verify(user).setProfileImagePath("Profile Image Path");
        verify(user).setRole(Role.BUSINESSEXPERT);
        verify(userRepository).findById(1);
        verify(userRepository).save(isA(User.class));
    }

    @Test
    void testUnexpireUser3() {
        // Arrange
        UserRepository userRepository = mock(UserRepository.class);
        Optional<User> emptyResult = Optional.empty();
        when(userRepository.findById(Mockito.<Integer>any())).thenReturn(emptyResult);
        ModelMapper modelMapper = new ModelMapper();

        // Act
        (new UserService(modelMapper, new BCryptPasswordEncoder(), userRepository)).unexpireUser(1);

        // Assert
        verify(userRepository).findById(1);
    }



}