package com.cop4331.group13.cavecheckin.dao;

import com.cop4331.group13.cavecheckin.domain.Session;
import org.springframework.data.repository.CrudRepository;

public interface SessionDao extends CrudRepository<Session, Long> {
}
