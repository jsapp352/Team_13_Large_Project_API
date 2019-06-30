package com.cop4331.group13.cavecheckin.dao;

import com.cop4331.group13.cavecheckin.api.dto.session.SessionAddRequestDto;
import com.cop4331.group13.cavecheckin.api.dto.session.SessionResponseDto;
import com.cop4331.group13.cavecheckin.domain.Session;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface SessionDao extends CrudRepository<Session, Long> {
    Session findBySessionId(long sessionId);
}
