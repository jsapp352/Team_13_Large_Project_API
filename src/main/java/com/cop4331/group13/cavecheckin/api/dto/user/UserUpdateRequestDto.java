package com.cop4331.group13.cavecheckin.api.dto.user;

public class UserUpdateRequestDto {
    private String firstName;
    private String lastName;
    private String email;
    private String courses;

    public UserUpdateRequestDto() {
    }

    public UserUpdateRequestDto(String firstName, String lastName, String email, String courses) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.courses = courses;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourses() {
        return courses;
    }

    public void setCourses(String courses) {
        this.courses = courses;
    }
}
