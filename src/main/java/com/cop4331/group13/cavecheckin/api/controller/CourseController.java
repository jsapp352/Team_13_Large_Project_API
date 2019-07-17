package com.cop4331.group13.cavecheckin.api.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cop4331.group13.cavecheckin.api.dto.course.CourseRequestDto;
import com.cop4331.group13.cavecheckin.api.dto.course.CourseResponseDto;
import com.cop4331.group13.cavecheckin.config.JwtProperties;
import com.cop4331.group13.cavecheckin.config.SecurityConfiguration;
import com.cop4331.group13.cavecheckin.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(maxAge = 72000)
@RequestMapping("/course/")
public class CourseController {

    @Autowired
    private CourseService service;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<CourseResponseDto> findAllCourses() {
        return service.findAllCourses();
    }

    @RequestMapping(value = "/admin/user/{userId}/", method = RequestMethod.GET)
    public List<CourseResponseDto> findCourseByUserId(@PathVariable long userId) {
        return service.findCourseByUserId(userId);
    }

    @RequestMapping(value = "/admin/{courseId}/", method = RequestMethod.GET)
    public CourseResponseDto findCourseByCourseId(@PathVariable long courseId) {
        return service.findCourseByCourseId(courseId);
    }

    @RequestMapping(value = "/teacher/", method = RequestMethod.GET)
    public List<CourseResponseDto> findTeacherCourse(@RequestHeader(value = JwtProperties.HEADER_STRING) String token) {
        return service.findCourseByUsername(SecurityConfiguration.getAuthSubject(token));
    }

    @RequestMapping(value = "/teacher/{courseId}/", method = RequestMethod.GET)
    public CourseResponseDto findTeacherCourseByCourseId(@RequestHeader(value = JwtProperties.HEADER_STRING) String token, @PathVariable long courseId) throws AccessDeniedException {
        return service.findTeacherCourseByCourseId(courseId, SecurityConfiguration.getAuthSubject(token));
    }

    @RequestMapping(value = "/teacher/", method = RequestMethod.POST)
    public CourseResponseDto addCourse(@RequestHeader(value = JwtProperties.HEADER_STRING) String token, @RequestBody CourseRequestDto course) {
        return service.addCourse(course, SecurityConfiguration.getAuthSubject(token));
    }

    @RequestMapping(value = "/teacher/{courseId}/", method = RequestMethod.PUT)
    public CourseResponseDto updateCourse(@RequestHeader(value = JwtProperties.HEADER_STRING) String token, @PathVariable long courseId, @RequestBody CourseRequestDto course) throws AccessDeniedException {
        return service.updateCourse(courseId, course, SecurityConfiguration.getAuthSubject(token));
    }

    @RequestMapping(value = "/teacher/{courseId}/", method = RequestMethod.DELETE)
    public CourseResponseDto deactivateCourse(@RequestHeader(value = JwtProperties.HEADER_STRING) String token, @PathVariable long courseId, @RequestBody CourseRequestDto course) throws AccessDeniedException {
        return service.deactivateCourse(courseId, SecurityConfiguration.getAuthSubject(token));
    }

    @RequestMapping(value = "/teacher/tacourses/{userId}/", method = RequestMethod.GET)
    public List<CourseResponseDto> findCourseByTaId(@PathVariable long userId, @RequestHeader(value = JwtProperties.HEADER_STRING) String token) throws AccessDeniedException {
        return service.findAuthorizedCourseByTaId(userId, SecurityConfiguration.getAuthSubject(token));
    }

    @RequestMapping(value = "/ta/", method = RequestMethod.GET)
    public List<CourseResponseDto> findCourseByTa(@RequestHeader(value = JwtProperties.HEADER_STRING) String token) {
        return service.findCourseByTa(SecurityConfiguration.getAuthSubject(token));
    }
}
