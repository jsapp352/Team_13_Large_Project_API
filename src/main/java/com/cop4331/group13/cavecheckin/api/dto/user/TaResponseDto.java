package com.cop4331.group13.cavecheckin.api.dto.user;

import com.cop4331.group13.cavecheckin.api.dto.course.CourseResponseDto;

import java.util.List;

public class TaResponseDto extends UserResponseDto {
    private List<CourseResponseDto> courses;

    public TaResponseDto() {
    }

    public TaResponseDto(long userId, String firstName, String lastName, String email, String username, String password, String role, long kioskPin, boolean isActive, List<CourseResponseDto> courses) {
        super(userId, firstName, lastName, email, username, password, role, kioskPin, isActive);
        this.courses = courses;
    }

    public List<CourseResponseDto> getCourses() {
        return courses;
    }

    public void setCourses(List<CourseResponseDto> courses) {
        this.courses = courses;
    }
}
