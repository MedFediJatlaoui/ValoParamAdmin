package com.talan.adminmodule.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talan.adminmodule.dto.RegisterDto;
import com.talan.adminmodule.dto.UserDto;
import com.talan.adminmodule.entity.User;
import com.talan.adminmodule.service.impl.UserService;
import io.micrometer.common.lang.Nullable;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userservice;
    private final ModelMapper modelMapper;

    @Autowired
    public UserController(UserService userservice, ModelMapper modelMapper) {
        this.userservice = userservice;
        this.modelMapper = modelMapper;
    }


    @GetMapping()
    public List<UserDto> getusers() {
        return userservice.getAll();
    }

    @PostMapping()
    public ResponseEntity<UserDto> addUser(@ModelAttribute RegisterDto dto,@Nullable
                                           @RequestParam("file") MultipartFile file) throws IOException {

        UserDto userDto = this.userservice.addUser(dto, file);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable int id,@Nullable
                                              @RequestParam("file") MultipartFile file,
                                              @RequestParam("dto") String dtoJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            RegisterDto dto = objectMapper.readValue(dtoJson, RegisterDto.class);

            UserDto updatedUser = this.userservice.update(id, dto, file);
            if (updatedUser != null) {
                return ResponseEntity.ok(updatedUser);
            } else {
                return ResponseEntity.notFound().build(); // User with given ID not found
            }
        } catch (IOException e) {
            // Handle IO exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserDto> getUser(@PathVariable String email){
        User user = this.userservice.findbyemail(email);
        UserDto userDto = modelMapper.map(user, UserDto.class) ;
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        userservice.delete(id);
    }

    @PatchMapping("/{id}/expire")
    public ResponseEntity<String> expireUser(@PathVariable int id) {
        userservice.expireUser(id);
        return ResponseEntity.ok("User expired successfully");
    }

    @PatchMapping("/{id}/unexpire")
    public ResponseEntity<String> unexpireUser(@PathVariable int id) {
        userservice.unexpireUser(id);
        return ResponseEntity.ok("User unexpired successfully");
    }


}
