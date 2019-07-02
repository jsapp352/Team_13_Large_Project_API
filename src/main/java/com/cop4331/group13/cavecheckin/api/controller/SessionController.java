package com.cop4331.group13.cavecheckin.api.controller;

import com.cop4331.group13.cavecheckin.api.dto.session.SessionAddRequestDto;
import com.cop4331.group13.cavecheckin.api.dto.session.SessionResponseDto;
import com.cop4331.group13.cavecheckin.api.dto.session.SessionUpdateRequestDto;
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

    @RequestMapping(value = "/ta/{sessionId}/", method = RequestMethod.GET)
    public SessionResponseDto getSession(@PathVariable long sessionId) {
        return service.getSession(sessionId);
    }

    @RequestMapping(value = "/kiosk/", method = RequestMethod.POST)
    public SessionResponseDto addSession(@RequestBody SessionAddRequestDto session) {
        return service.addSession(session);
    }

    @RequestMapping(value = "/kiosk/startTutor/", method = RequestMethod.PUT)
    public SessionResponseDto startTutor(@RequestBody SessionUpdateRequestDto session) {
        return service.startTutor(session);
    }

    @RequestMapping(value = "/kiosk/endTutor/", method = RequestMethod.PUT)
    public SessionResponseDto endTutor(@RequestBody SessionUpdateRequestDto session) {
        return service.endTutor(session);
    }
}
