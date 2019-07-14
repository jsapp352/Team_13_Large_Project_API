package com.cop4331.group13.cavecheckin.api.controller;

import com.cop4331.group13.cavecheckin.api.dto.session.*;
import com.cop4331.group13.cavecheckin.api.dto.user.UserAddRequestDto;
import com.cop4331.group13.cavecheckin.api.dto.user.UserResponseDto;
import com.cop4331.group13.cavecheckin.service.SessionService;
import com.cop4331.group13.cavecheckin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @RequestMapping(value = "/kiosk/cancelTutor/", method = RequestMethod.PUT)
    public SessionResponseDto cancelTutor(@RequestBody SessionUpdateRequestDto session) {
        return service.cancelTutor(session);
    }

    @RequestMapping(value = "/ta/taSessions/", method = RequestMethod.GET)
    public SessionHistoryResponseDto taSessionHistory(@RequestBody SessionHistoryRequestDto request) {
        return service.getSessionHistoryByCourseIdAndTaId(request);
    }

    @RequestMapping(value = "/teacher/courseSessions/", method = RequestMethod.GET)
    public SessionHistoryResponseDto sessionHistory(@RequestBody SessionHistoryRequestDto request) {
        return service.getSessionHistoryByCourseId(request);
    }

    @RequestMapping(value = "/kiosk/waitTime/{courseId}/", method = RequestMethod.GET)
    public long getEstimatedWaitTime(@PathVariable long courseId) {
        return service.getCurrentEstimatedWaitTime(courseId);
    }

}
