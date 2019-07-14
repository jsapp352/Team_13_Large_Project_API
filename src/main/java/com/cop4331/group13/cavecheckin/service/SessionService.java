package com.cop4331.group13.cavecheckin.service;

import com.cop4331.group13.cavecheckin.api.dto.session.SessionAddRequestDto;
import com.cop4331.group13.cavecheckin.api.dto.session.SessionHistoryRequestDto;
import com.cop4331.group13.cavecheckin.api.dto.session.SessionResponseDto;
import com.cop4331.group13.cavecheckin.api.dto.session.SessionUpdateRequestDto;
import com.cop4331.group13.cavecheckin.api.dto.user.UserResponseDto;
import com.cop4331.group13.cavecheckin.dao.CourseDao;
import com.cop4331.group13.cavecheckin.dao.SessionDao;
import com.cop4331.group13.cavecheckin.dao.UserDao;
import com.cop4331.group13.cavecheckin.domain.Course;
import com.cop4331.group13.cavecheckin.domain.Session;
import com.cop4331.group13.cavecheckin.domain.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SessionService {
    @Autowired
    private SessionDao dao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private CourseDao courseDao;

    @Autowired
    private ModelMapper mapper;

    public SessionResponseDto getSession(long sessionId) {
        Session session = dao.findBySessionId(sessionId);
        if (session != null) {
            return mapper.map(session, SessionResponseDto.class);
        } else {
            return null;
        }
    }

    public SessionResponseDto addSession(SessionAddRequestDto sessionDto) {
        Session session = mapper.map(sessionDto, Session.class);

        session.setStartTime(new Date());

        try {
            session = dao.save(session);
        }
        catch (DataIntegrityViolationException e) {
            return null;
        }

        if (session != null) {
            return mapper.map(session, SessionResponseDto.class);
        }
        else {
            return null;
        }
    }

    public SessionResponseDto startTutor(SessionUpdateRequestDto sessionDto) {
        // Validate request's user PIN.
        User user = getUserByEncryptedPin(sessionDto.getEncryptedPin());

        // If user is nonexistent or inactive, return null.
        if (user == null || user.isActive() == false) {
            return null;
        }

        // If user was found, get the specified session
        Session session = dao.findBySessionId(sessionDto.getSessionId());

        // If session was found, update the session and create a response DTO
        SessionResponseDto responseDto = null;

        if (session != null)
        {
            session.setHelpTime(new Date());
            session.setUserId(user.getUserId());

            session = dao.save(session);

            responseDto = mapper.map(session, SessionResponseDto.class);
        }

        return responseDto;
    }

    public SessionResponseDto endTutor(SessionUpdateRequestDto sessionDto) {
        // Validate request's user PIN.
        User user = getUserByEncryptedPin(sessionDto.getEncryptedPin());

        // If user is nonexistent or inactive, return null.
        if (user == null || user.isActive() == false) {
            return null;
        }

        // If user was found, get the specified session
        Session session = dao.findBySessionId(sessionDto.getSessionId());

        // If session was found, update the session and create a response DTO
        SessionResponseDto responseDto = null;

        if (session != null)
        {
            session.setEndTime(new Date());
            session.setUserId(user.getUserId());

            session = dao.save(session);

            responseDto = mapper.map(session, SessionResponseDto.class);
        }

        return responseDto;
    }

    public SessionResponseDto cancelTutor(SessionUpdateRequestDto sessionDto) {
        // Validate request's user PIN.
        User user = getUserByEncryptedPin(sessionDto.getEncryptedPin());

        // If user is nonexistent or inactive, return null.
        if (user == null || user.isActive() == false) {
            return null;
        }

        // If user was found, get the specified session
        Session session = dao.findBySessionId(sessionDto.getSessionId());

        // If session was found, update the session and create a response DTO
        SessionResponseDto responseDto = null;

        if (session != null)
        {
            Date currentTime = new Date();
            session.setStartTime(currentTime);
            session.setEndTime(currentTime);
            session.setUserId(user.getUserId());

            session = dao.save(session);

            responseDto = mapper.map(session, SessionResponseDto.class);
        }

        return responseDto;
    }

    public List<SessionResponseDto> getSessionHistoryByCourseIdAndTaId(SessionHistoryRequestDto requestDto)
    {
        // Verify that the user is either the course instructor, the TA whose
        // history is being queried, or an admin.
        Optional<Course> course = courseDao.findById(requestDto.getCourseId());

        if (course.isEmpty())
            return null;

        Optional<User> user = userDao.findById(requestDto.getUserId());

        if (user.isEmpty())
            return null;

        long userId = user.get().getUserId();

        if (
                course.get().getUserId() != userId ||
                requestDto.getTaId() != userId ||
                user.get().getRole() != "ADMIN")
            return null;

        // Get a list of sessions for the specified course and TA
        List<Session> sessions = dao.findAllByCourseIdAndUserId(requestDto.getCourseId(), requestDto.getTaId());

        // Build a list of SessionResponseDtos from the list of sessions
        List<SessionResponseDto> sessionResponseDtos = new ArrayList<>();

        for (Session session : sessions)
            sessionResponseDtos.add(new SessionResponseDto(session));

        return sessionResponseDtos;
    }

    public List<SessionResponseDto> getSessionHistoryByCourseId(SessionHistoryRequestDto requestDto)
    {
        // Verify that the user is either the course instructor or an admin.
        Optional<Course> course = courseDao.findById(requestDto.getCourseId());

        if (course.isEmpty())
            return null;

        Optional<User> user = userDao.findById(requestDto.getUserId());

        if (user.isEmpty())
            return null;

        long userId = user.get().getUserId();

        if (course.get().getUserId() != userId || user.get().getRole() != "ADMIN")
            return null;

        // Get a list of sessions for the specified course.
        List<Session> sessions = dao.findAllByCourseId(requestDto.getCourseId());

        // Build a list of SessionResponseDtos from the list of sessions
        List<SessionResponseDto> sessionResponseDtos = new ArrayList<>();

        for (Session session : sessions)
            sessionResponseDtos.add(new SessionResponseDto(session));

        return sessionResponseDtos;
    }

    private User getUserByEncryptedPin(String encryptedPin) {
        // Attempt to decrypt the kiosk PIN
        String decryptedPin;
        try {
            decryptedPin = decryptPin(encryptedPin);
        }
        catch (Exception e) {
            return null;
        }

        // Find user by PIN.
        return userDao.findByKioskPin(decryptedPin);
    }

    //DEV Not sure if this should move to another class.
    private String decryptPin(String encryptedPin)
            throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        String keyGenString = System.getenv("AES_SECRET");

        // Convert keyGenString to a byte array.
        byte[] encryptionKeyBytes = keyGenString.getBytes();

        // Convert encryptedPin string to a byte array.
        byte[] encryptedPinBytes = DatatypeConverter.parseHexBinary(encryptedPin);

        // Instantiate the Cipher and encryption key objects.
        Cipher cipher;
        SecretKey secretKey;
        try
        {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            secretKey = new SecretKeySpec(encryptionKeyBytes, "AES");
        }
        catch (Exception e)
        {
            System.out.println(e);
            return null;
        }

        // Initialize the Cipher object with the secret key value in decryption mode.
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        // Decrypt the PIN byte array.
        byte[] pinBytes = cipher.doFinal(encryptedPinBytes);

        // Return the decrypted PIN byte array as a string.
        return new String(pinBytes);
    }
}
