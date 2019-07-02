package com.cop4331.group13.cavecheckin.api.dto.course;

public class CourseRequestDto {
    private String courseCode;
    private String courseName;
    private long year;
    private String semester;

    public CourseRequestDto() {
    }

    public CourseRequestDto(String courseCode, String courseName, long year, String semester) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.year = year;
        this.semester = semester;
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
}
