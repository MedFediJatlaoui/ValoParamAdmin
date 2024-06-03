package com.talan.adminmodule.service.impl;


import com.talan.adminmodule.dto.RegisterDto;
import com.talan.adminmodule.dto.UserDto;
import com.talan.adminmodule.entity.User;
import com.talan.adminmodule.repository.UserRepository;
import io.micrometer.common.lang.Nullable;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

@Service
public class UserService {
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public UserService(ModelMapper modelMapper, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public UserDto mapUserToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }
    public String storeProfileImage(@Nullable MultipartFile profileImage) throws IOException {
        String path = "";
        if (profileImage != null && !profileImage.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(profileImage.getOriginalFilename()));
            String currentDir = System.getProperty("user.dir");
            Path uploadDir = Paths.get(currentDir, "..", "Front", "src", "assets", "demo", "images", "user-profiles");
            Path storeDir = Paths.get("assets", "demo", "images", "user-profiles", fileName);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            try (InputStream inputStream = profileImage.getInputStream()) {
                Path filePath = uploadDir.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                path = storeDir.toString();
            } catch (IOException ex) {
                throw new IOException("Could not store file " + fileName + ". Please try again!", ex);
            }
        }
        return path;
    }
    public UserDto addUser(RegisterDto dto, @Nullable MultipartFile file) throws IOException {
        String profileImagePath = null;
        if (file != null && !file.isEmpty()) {
            profileImagePath = storeProfileImage(file);
        }
        User user = User.builder()
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .email(dto.getEmail())
                .company(dto.getCompany())
                .phone(dto.getPhone())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole())
                .profileImagePath(profileImagePath)
                .active(true)
                .nonExpired(true)
                .build();

        userRepository.save(user);
        return mapUserToDto(user);
    }
    public List<UserDto> getAll(){
        List<User>  users= userRepository.findAll();
        return users.stream()
                .map(this::mapUserToDto)
                .toList();
    }
    public UserDto update(int id, RegisterDto dto, MultipartFile file) throws IOException {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return null;
        }

        if (dto.getFirstname() != null) {
            user.setFirstname(dto.getFirstname());
        }
        if (dto.getLastname() != null) {
            user.setLastname(dto.getLastname());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getRole() != null) {
            user.setRole(dto.getRole());
        }
        if (dto.getCompany() != null) {
            user.setCompany(dto.getCompany());
        }
        if (file != null && !file.isEmpty()) {
            String profileImagePath = storeProfileImage(file);
            user.setProfileImagePath(profileImagePath);
        }

        userRepository.save(user);
        return mapUserToDto(user);
    }

    public User findbyemail(String email)
    {
        return userRepository.findByEmail(email).orElse(null);
    }

    public void delete(Integer id)
    {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setActive(false);
            userRepository.save(user);
        }
    }

    public void expireUser(Integer id)
    {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setNonExpired(false);
            userRepository.save(user);
        }
    }

    public void unexpireUser(Integer id)
    {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setNonExpired(true);
            userRepository.save(user);
        }
    }


}
