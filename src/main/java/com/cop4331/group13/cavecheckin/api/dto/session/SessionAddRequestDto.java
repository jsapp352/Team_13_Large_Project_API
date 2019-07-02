package com.cop4331.group13.cavecheckin.api.dto.session;

import java.util.Date;

public class SessionAddRequestDto {
    private String studentName;
    private long courseId;

    public SessionAddRequestDto() {}

    public SessionAddRequestDto(String studentName, long courseId) {
        this.studentName = studentName;
        this.courseId = courseId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }
}
