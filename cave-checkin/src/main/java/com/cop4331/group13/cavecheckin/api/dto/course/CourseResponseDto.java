package com.cop4331.group13.cavecheckin.api.dto.course;

public class CourseResponseDto {
    private long courseId;
    private long userId;
    private String courseCode;
    private String courseName;
    private long year;
    private String semester;
    private boolean isActive;

    public CourseResponseDto() {
    }

    public CourseResponseDto(long courseId, long userId, String courseCode, String courseName, long year, String semester, boolean isActive) {
        this.courseId = courseId;
        this.userId = userId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.year = year;
        this.semester = semester;
        this.isActive = isActive;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public long getYear() {
        return year;
    }

    public void setYear(long year) {
        this.year = year;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
