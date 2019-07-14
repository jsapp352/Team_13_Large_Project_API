package com.cop4331.group13.cavecheckin.dao;

import com.cop4331.group13.cavecheckin.api.dto.session.SessionAddRequestDto;
import com.cop4331.group13.cavecheckin.api.dto.session.SessionResponseDto;
import com.cop4331.group13.cavecheckin.domain.Session;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

public interface SessionDao extends CrudRepository<Session, Long> {
    Session findBySessionId(long sessionId);
    List<Session> findAllByCourseIdAndUserId(long courseId, long userId);
    List<Session> findAllByCourseId(long courseId);
    List<Session> findAllByCourseIdAndStartTimeBetweenOrderByStartTimeDesc(long courseId, Date startTimeStart, Date startTimeEnd);
}
