package com.cop4331.group13.cavecheckin.api.controller;

import com.cop4331.group13.cavecheckin.api.dto.user.UserAddRequestDto;
import com.cop4331.group13.cavecheckin.api.dto.user.UserAddResponseDto;
import com.cop4331.group13.cavecheckin.api.dto.user.UserGetResponseDto;
import com.cop4331.group13.cavecheckin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService service;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public UserAddResponseDto addUser(@RequestBody UserAddRequestDto user) {
        return service.addUser(user, "ADMIN");
    }

    @RequestMapping(value = "/admin/{userId}", method = RequestMethod.GET)
    public UserGetResponseDto getUser(@PathVariable long userId) {
        return service.getUser(userId);
    }

    @RequestMapping(value = "/admin/", method = RequestMethod.POST)
    public UserAddResponseDto addTeacher(@RequestBody UserAddRequestDto user) {
        return service.addUser(user, "TEACHER");
    }

    @RequestMapping(value = "/teacher/", method = RequestMethod.POST)
    public UserAddResponseDto addTa(@RequestBody UserAddRequestDto user) {
        return service.addUser(user, "TA");
    }

}
