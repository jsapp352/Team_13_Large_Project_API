package com.cop4331.group13.cavecheckin.service;

import com.cop4331.group13.cavecheckin.api.dto.user.UserAddRequestDto;
import com.cop4331.group13.cavecheckin.api.dto.user.UserAddResponseDto;
import com.cop4331.group13.cavecheckin.api.dto.user.UserGetResponseDto;
import com.cop4331.group13.cavecheckin.dao.UserDao;
import com.cop4331.group13.cavecheckin.domain.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class UserService {

    @Autowired
    private UserDao dao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper mapper;

    private HashSet<Long> kioskPins = null;

    public UserAddResponseDto addUser(UserAddRequestDto userDto, String role) {
        // Map the dto to a regular user object and generate a random kioskPin
        User user = mapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(role);
        user.setKioskPin(generatePin());

        // Save the user if the username is unique
        try {
            user = dao.save(user);
        } catch (DataIntegrityViolationException e) {
            return null;
        }

        // If the add was successful return a mapped response dto
        return mapper.map(user, UserAddResponseDto.class);
    }

    // Generate random pins until we find one that hasn't been used already
    private long generatePin() {
        // Pull the kiosk pins if we haven't already;
        if (kioskPins == null) {
            kioskPins = new HashSet<>();
            kioskPins.addAll(dao.findAllKioskPins());
        }

        // Generate a unique pin
        long pin = (long) (Math.random() * 1000000);
        while (kioskPins.contains(pin)) {
            pin = (long) (Math.random() * 1000000);
        }

        // Add the pin to the set
        kioskPins.add(pin);

        return pin;
    }

    public UserGetResponseDto getUser(long userId) {
        User user = dao.findById(userId).orElse(null);
        if (user != null) {
            return mapper.map(user, UserGetResponseDto.class);
        } else {
            return null;
        }
    }
}
