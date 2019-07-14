package com.cop4331.group13.cavecheckin.service;

import com.cop4331.group13.cavecheckin.api.dto.session.*;
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

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.InvalidKeyException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
            // Set help time and end time to the current time to indicate a canceled session.
            Date currentTime = new Date();
            session.setHelpTime(currentTime);
            session.setEndTime(currentTime);
            session.setUserId(user.getUserId());

            session = dao.save(session);

            responseDto = mapper.map(session, SessionResponseDto.class);
        }

        return responseDto;
    }

    public SessionHistoryResponseDto getSessionHistoryByCourseIdAndTaId(SessionHistoryRequestDto requestDto)
    {
        Course course = courseDao.findById(requestDto.getCourseId()).orElse(null);

        if (course == null)
            return null;

        long userId = requestDto.getUserId();
        long taId = requestDto.getTaId();

        User user = userDao.findById(userId).orElse(null);

        if (user == null)
            return null;

        long courseInstructorUserId = course.getUserId();

        // Verify that any "TEACHER" users are authorized to request TA session data.
        if (user.getRole() == "TEACHER" && userId != courseInstructorUserId)
            return null;

        // Verify that any "TA" users are requesting to view their own data.
        if (user.getRole() == "TA" && userId != taId)
            return null;

        // Get a list of sessions for the specified course and TA
        List<Session> sessions = dao.findAllByCourseIdAndUserId(requestDto.getCourseId(), taId);

        // Build a list of SessionResponseDtos from the list of sessions
        List<SessionResponseDto> sessionResponseDtos = new ArrayList<>();

        for (Session session : sessions)
            sessionResponseDtos.add(new SessionResponseDto(session));

        // Calculate the average duration of a session (in seconds)
        int averageSessionDuration = averageSessionDuration(sessions);

        return new SessionHistoryResponseDto(sessionResponseDtos, averageSessionDuration);
    }

    public SessionHistoryResponseDto getSessionHistoryByCourseId(SessionHistoryRequestDto requestDto)
    {
        Course course = courseDao.findById(requestDto.getCourseId()).orElse(null);

        if (course == null)
            return null;

        User user = userDao.findById(requestDto.getUserId()).orElse(null);

        if (user == null)
            return null;

        long userId = user.getUserId();
        long courseInstructorUserId = course.getUserId();

        // Verify that any "TEACHER" users are authorized to view course data.
        if (user.getRole() == "TEACHER" && userId != courseInstructorUserId)
            return null;

        // Get a list of sessions for the specified course.
        List<Session> sessions = dao.findAllByCourseId(requestDto.getCourseId());

        // Build a list of SessionResponseDtos from the list of sessions.
        List<SessionResponseDto> sessionResponseDtos = new ArrayList<>();

        for (Session session : sessions)
            sessionResponseDtos.add(new SessionResponseDto(session));

        // Calculate the average duration of a session (in seconds).
        int averageSessionDuration = averageSessionDuration(sessions);

        return new SessionHistoryResponseDto(sessionResponseDtos, averageSessionDuration);
    }

    public int getCurrentEstimatedWaitTime(long courseId)
    {
        int sampleSize = 5;

        // Get sorted list of sessions
        List<Session> sessions = findSessionsByDate(courseId, new Date());

        //DEBUG
        System.out.println("Session IDs found by date:");
        for (Session session : sessions)
            System.out.println(session.getSessionId());

        // Build the sample data list for the rolling average
        List<Session> sampleSessions = new ArrayList<>();

        for (int i = 0; (i < sessions.size()) && (sampleSessions.size() < sampleSize); i++)
        {
            Session session = sessions.get(i);

            // Omit sessions that are either canceled or still waiting for help.
            if (session.getStartTime() == null ||
                    session.getHelpTime() == null ||
                    session.getHelpTime().equals(session.getEndTime()))
                continue;

            sampleSessions.add(session);
        }

        // Return -1 if no valid sample data is available
        if (sampleSessions.isEmpty())
            return -1;
        // Otherwise, return the average wait duration
        else
            return averageWaitDuration(sampleSessions);
    }

    private List<Session> findSessionsByDate(long courseId, Date date)
    {
        // A quick and dirty way to get the proper date range (11:59:59.999 yesterday to midnight tomorrow).
        // Adapted from code by StackOverflow user Andrei Volgin.
        Long time = new Date().getTime();

        Date midnight = new Date(time - time % (24 * 60 * 60 * 1000));

        Date startTimeStart = new Date(midnight.getTime() - 1);
        Date startTimeEnd = new Date(midnight.getTime() + 24 * 60 * 60 * 1000);

        return dao.findAllByCourseIdAndStartTimeBetweenOrderByStartTimeDesc(courseId, startTimeStart, startTimeEnd);
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

    private int averageSessionDuration(List<Session> sessions) {
        // Could use an int for one TA, but long is definitely safe for multiple TAs' course session histories.
        long totalDuration = 0;
        int recordCount = 0;

        // Calculate the total duration of all valid session records
        for (Session session : sessions)
        {
            if (session == null)
                continue;

            Date helpTime = session.getHelpTime();
            Date endTime = session.getEndTime();

            // Check for missing or invalid help/end time values.
            if (helpTime == null || endTime == null || !endTime.after(helpTime))
                continue;

            totalDuration += (endTime.getTime() - helpTime.getTime()) / 1000;
            recordCount++;
        }

        // Return the average session duration in seconds
        return (int)(totalDuration / recordCount);
    }

    private int averageWaitDuration(List<Session> sessions) {
        long totalDuration = 0;
        int recordCount = 0;

        // Calculate the total wait duration of all valid session records
        for (Session session : sessions)
        {
            if (session == null)
                continue;

            Date startTime = session.getStartTime();
            Date helpTime = session.getHelpTime();
            Date endTime = session.getEndTime();

            // Check for missing/invalid time values.
            if (startTime == null || helpTime == null || !helpTime.after(startTime))
                continue;

            totalDuration += (helpTime.getTime() - startTime.getTime()) / 1000;
            recordCount++;
        }

        // Return the average session duration in seconds
        return (int)(totalDuration / recordCount);
    }
}
