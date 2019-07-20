package com.cop4331.group13.cavecheckin.api.controller;

import com.cop4331.group13.cavecheckin.api.dto.user.UserAddRequestDto;
import com.cop4331.group13.cavecheckin.api.dto.user.UserPasswordRequestDto;
import com.cop4331.group13.cavecheckin.api.dto.user.UserResponseDto;
import com.cop4331.group13.cavecheckin.api.dto.user.UserUpdateRequestDto;
import com.cop4331.group13.cavecheckin.config.JwtProperties;
import com.cop4331.group13.cavecheckin.config.SecurityConfiguration;
import com.cop4331.group13.cavecheckin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(maxAge = 72000)
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService service;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public UserResponseDto getSelf(@RequestHeader(value = JwtProperties.HEADER_STRING) String token) {
        return service.getSelf(SecurityConfiguration.getAuthSubject(token));
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public UserResponseDto addUser(@RequestBody UserAddRequestDto user) {
        return service.addUser(user, "ADMIN");
    }

    @RequestMapping(value = "/admin/", method = RequestMethod.GET)
    public List<UserResponseDto> findTeachers() {
        return service.getUsersByRole("TEACHER");
    }

    @RequestMapping(value = "/admin/{userId}", method = RequestMethod.GET)
    public UserResponseDto getUser(@PathVariable long userId) {
        return service.getUser(userId);
    }

    @RequestMapping(value = "/admin/", method = RequestMethod.POST)
    public UserResponseDto addTeacher(@RequestBody UserAddRequestDto user) {
        return service.addUser(user, "TEACHER");
    }

    @RequestMapping(value = "/admin/{userId}/", method = RequestMethod.PUT)
    public UserResponseDto updateUser(@PathVariable long userId, @RequestBody UserUpdateRequestDto user) {
        return service.updateUser(userId, user);
    }

    @RequestMapping(value = "/admin/{userId}/", method = RequestMethod.DELETE)
    public UserResponseDto deactivateTeacher(@PathVariable long userId) {
        return service.deactivateUser(userId);
    }

    @RequestMapping(value = "/teacher/", method = RequestMethod.POST)
    public UserResponseDto addTa(@RequestBody UserAddRequestDto user) {
        return service.addUser(user, "TA");
    }

    @RequestMapping(value = "/teacher/course/{courseId}/", method = RequestMethod.GET)
    public List<UserResponseDto> findTasByCourseId(@PathVariable long courseId, @RequestHeader(value = JwtProperties.HEADER_STRING) String token) throws AccessDeniedException {
        return service.findTasByCourseId(courseId, SecurityConfiguration.getAuthSubject(token));
    }

    @RequestMapping(value = "/teacher/{userId}/", method = RequestMethod.GET)
    public UserResponseDto findTaByUserId(@PathVariable long userId, @RequestHeader(value = JwtProperties.HEADER_STRING) String token) throws AccessDeniedException {
        return service.findTaByUserId(userId, SecurityConfiguration.getAuthSubject(token));
    }

    @RequestMapping(value = "/teacher/{userId}/", method = RequestMethod.PUT)
    public UserResponseDto updateTa(@PathVariable long userId, @RequestBody UserUpdateRequestDto user,
                                    @RequestHeader(value = JwtProperties.HEADER_STRING) String token) throws AccessDeniedException {
        return service.updateTa(userId, user, SecurityConfiguration.getAuthSubject(token));
    }

    @RequestMapping(value = "/teacher/{userId}/", method = RequestMethod.DELETE)
    public UserResponseDto deactivateTa(@PathVariable long userId, @RequestHeader(value = JwtProperties.HEADER_STRING) String token) throws AccessDeniedException {
        return service.deactivateTa(userId, SecurityConfiguration.getAuthSubject(token));
    }

    @RequestMapping(value = "/ta/password/", method = RequestMethod.POST)
    public UserResponseDto resetPassword(@RequestBody UserPasswordRequestDto dto, @RequestHeader(value = JwtProperties.HEADER_STRING) String token) throws AccessDeniedException {
        return service.resetPassword(dto, SecurityConfiguration.getAuthSubject(token));
    }

    @RequestMapping(value = "/ta/pin/", method = RequestMethod.POST)
    public UserResponseDto resetPin(@RequestHeader(value = JwtProperties.HEADER_STRING) String token) {
        return service.resetPin(SecurityConfiguration.getAuthSubject(token));
    }

    @RequestMapping(value = "/ta/", method = RequestMethod.PUT)
    public UserResponseDto updateSelf(@RequestBody UserUpdateRequestDto dto, @RequestHeader(value = JwtProperties.HEADER_STRING) String token) {
        return service.updateSelf(dto, SecurityConfiguration.getAuthSubject(token));
    }
}
