package com.cop4331.group13.cavecheckin.service;

import com.cop4331.group13.cavecheckin.api.dto.course.CourseRequestDto;
import com.cop4331.group13.cavecheckin.api.dto.course.CourseResponseDto;
import com.cop4331.group13.cavecheckin.dao.CourseDao;
import com.cop4331.group13.cavecheckin.dao.TaCourseDao;
import com.cop4331.group13.cavecheckin.dao.UserDao;
import com.cop4331.group13.cavecheckin.domain.Course;
import com.cop4331.group13.cavecheckin.domain.TaCourse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourseService {

    @Autowired
    private CourseDao dao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private TaCourseDao taCourseDao;

    @Autowired
    private ModelMapper mapper;

    public List<CourseResponseDto> findCourseByUserId(long userId) {
        List<CourseResponseDto> dtos = new ArrayList<>();
        dao.findByUserId(userId).forEach(course -> dtos.add(mapper.map(course, CourseResponseDto.class)));

        return dtos;
    }

    public CourseResponseDto findCourseByCourseId(long courseId) {
        Course course = dao.findById(courseId).orElse(null);

        return (course != null) ? mapper.map(course, CourseResponseDto.class) : null;
    }

    public List<CourseResponseDto> findCourseByUsername(String username) {
        Long userId = userDao.findUserIdByUsername(username);
        return (userId != null) ? findCourseByUserId(userId) : null;
    }

    public CourseResponseDto findTeacherCourseByCourseId(long courseId, String username) throws AccessDeniedException {
        Course course = dao.findById(courseId).orElse(null);
        if (course == null) return null;

        verifyCourseAccess(username, course);

        return mapper.map(course, CourseResponseDto.class);
    }

    public CourseResponseDto addCourse(CourseRequestDto courseRequestDto, String username) {
        Long userId = userDao.findUserIdByUsername(username);
        if (userId == null) return null;

        Course course = mapper.map(courseRequestDto, Course.class);
        course.setCourseId(0);
        course.setUserId(userId);
        course.setActive(true);

        course = dao.save(course);

        return (course != null) ? mapper.map(course, CourseResponseDto.class) : null;
    }

    public CourseResponseDto updateCourse(long courseId, CourseRequestDto updatedCourse, String username) throws AccessDeniedException {
        Course course = dao.findById(courseId).orElse(null);
        if (updatedCourse == null || username == null || course == null) {
            return null;
        }

        verifyCourseAccess(username, course);

        course.setCourseCode(updatedCourse.getCourseCode());
        course.setCourseName(updatedCourse.getCourseName());
        course.setYear(updatedCourse.getYear());
        course.setSemester(updatedCourse.getSemester());

        course = dao.save(course);

        return (course != null) ? mapper.map(course, CourseResponseDto.class) : null;
    }

    public CourseResponseDto deactivateCourse(long courseId, String username) throws AccessDeniedException {
        Course course = dao.findById(courseId).orElse(null);
        if (course == null) return null;

        verifyCourseAccess(username, course);
        course.setActive(false);
        course = dao.save(course);

        return mapper.map(course, CourseResponseDto.class);
    }

    public List<CourseResponseDto> findCourseByTa(String username) {
        Long userId = userDao.findUserIdByUsername(username);
        if (userId == null) return null;

        return findCourseByTaId(userId);
    }

    public List<CourseResponseDto> findCourseByTaId(long userId) {
        List<CourseResponseDto> responseDtos = new ArrayList<>();
        taCourseDao.findByUserId(userId).forEach(taCourse -> {
            if (taCourse.isActive()) {
                dao.findById(taCourse.getCourseId()).ifPresent(course -> responseDtos.add(mapper.map(course, CourseResponseDto.class)));
            }
        });

        return responseDtos;
    }

    private void verifyCourseAccess(String username, Course course) throws AccessDeniedException {
        Long userId = userDao.findUserIdByUsername(username);
        if (userId == null || course.getUserId() != userId) throw new AccessDeniedException("You are not authorized to access this course");
    }
}
