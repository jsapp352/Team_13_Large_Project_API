package com.cop4331.group13.cavecheckin.api.controller;

import com.cop4331.group13.cavecheckin.api.dto.session.SessionAddRequestDto;
import com.cop4331.group13.cavecheckin.api.dto.session.SessionResponseDto;
import com.cop4331.group13.cavecheckin.api.dto.user.UserAddRequestDto;
import com.cop4331.group13.cavecheckin.api.dto.user.UserResponseDto;
import com.cop4331.group13.cavecheckin.service.SessionService;
import com.cop4331.group13.cavecheckin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/session")
public class SessionController {
    @Autowired
    private SessionService service;

//    GET /ta/{sessionId}/ (Gets a session by ID)
    @RequestMapping(value = "/ta/{sessionId}/", method = RequestMethod.GET)
    public SessionResponseDto getSession(@PathVariable long sessionId) {
        return service.getSession(sessionId);
    }

//    POST / (Add a new session to the list)
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public SessionResponseDto addSession(@RequestBody SessionAddRequestDto session) {
        return service.addSession(session, "KIOSK");
    }

//    PUT /ta/startTutor/{sessionId}/ (Move a session from waiting to in progress)
    @RequestMapping(value = "/ta/startTutor/{sessionId}/", method = RequestMethod.PUT)
    public SessionResponseDto startTutor(@PathVariable long sessionId) {
        return service.startTutor(sessionId);
    }

//    PUT /ta/endTutor/{sessionId}/ (Move session from in progress to closed)
    @RequestMapping(value = "/ta/endTutor/{sessionId}/", method = RequestMethod.PUT)
    public SessionResponseDto endTutor(@PathVariable long sessionId) {
        return service.endTutor(sessionId);
    }

//    @RequestMapping(value = "/", method = RequestMethod.POST)
//    public UserResponseDto addUser(@RequestBody UserAddRequestDto user) {
//        return service.addUser(user, "ADMIN");
//    }
//
//    @RequestMapping(value = "/admin/", method = RequestMethod.GET)
//    public List<UserResponseDto> findTeachers() {
//        return service.getUsersByRole("TEACHER");
//    }
//
//    @RequestMapping(value = "/admin/{userId}", method = RequestMethod.GET)
//    public UserResponseDto getUser(@PathVariable long userId) {
//        return service.getUser(userId);
//    }
//
//    @RequestMapping(value = "/admin/", method = RequestMethod.POST)
//    public UserResponseDto addTeacher(@RequestBody UserAddRequestDto user) {
//        return service.addUser(user, "TEACHER");
//    }
}
