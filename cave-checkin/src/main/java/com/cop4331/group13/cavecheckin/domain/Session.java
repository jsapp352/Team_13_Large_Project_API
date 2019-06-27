package com.cop4331.group13.cavecheckin.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long sessionId;
    private String studentName;
    private long userId;
    private long courseId;
    private Date startTime;
    private Date helpTime;
    private Date endTime;

    public Session() {
    }

    public Session(long sessionId, String studentName, long userId, long courseId, Date startTime, Date helpTime, Date endTime) {
        this.sessionId = sessionId;
        this.studentName = studentName;
        this.userId = userId;
        this.courseId = courseId;
        this.startTime = startTime;
        this.helpTime = helpTime;
        this.endTime = endTime;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getHelpTime() {
        return helpTime;
    }

    public void setHelpTime(Date helpTime) {
        this.helpTime = helpTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
