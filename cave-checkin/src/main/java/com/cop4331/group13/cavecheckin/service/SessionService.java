package com.cop4331.group13.cavecheckin.service;

import com.cop4331.group13.cavecheckin.dao.SessionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    @Autowired
    private SessionDao dao;
}
