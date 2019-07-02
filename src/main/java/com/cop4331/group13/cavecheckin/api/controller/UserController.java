package com.cop4331.group13.cavecheckin.api.controller;

import com.cop4331.group13.cavecheckin.api.dto.user.UserAddRequestDto;
import com.cop4331.group13.cavecheckin.api.dto.user.UserResponseDto;
import com.cop4331.group13.cavecheckin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService service;

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

    @RequestMapping(value = "/admin/{userId}/", method = RequestMethod.DELETE)
    public UserResponseDto deactivateTeacher(@PathVariable long userId) {
        return service.deactivateUser(userId);
    }

    @RequestMapping(value = "/teacher/", method = RequestMethod.POST)
    public UserResponseDto addTa(@RequestBody UserAddRequestDto user) {
        return service.addUser(user, "TA");
    }

    @RequestMapping(value = "/teacher/course/{courseId}/", method = RequestMethod.GET)
    public List<UserResponseDto> findTasByCourseId(@PathVariable long courseId) {
        return service.findTasByCourseId(courseId);
    }

}
