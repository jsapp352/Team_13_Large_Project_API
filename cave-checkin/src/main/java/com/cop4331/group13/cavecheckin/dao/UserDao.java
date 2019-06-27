package com.cop4331.group13.cavecheckin.dao;

import com.cop4331.group13.cavecheckin.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserDao extends CrudRepository<User, Long> {

    User findByUsername(String username);

    User findByKioskPin(long kioskPin);

    @Query("SELECT u.kioskPin FROM User u")
    List<Long> findAllKioskPins();
}
