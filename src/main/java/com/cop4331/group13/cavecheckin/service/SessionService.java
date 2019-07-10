package com.cop4331.group13.cavecheckin.service;

import com.cop4331.group13.cavecheckin.api.dto.session.SessionAddRequestDto;
import com.cop4331.group13.cavecheckin.api.dto.session.SessionResponseDto;
import com.cop4331.group13.cavecheckin.api.dto.session.SessionUpdateRequestDto;
import com.cop4331.group13.cavecheckin.api.dto.user.UserResponseDto;
import com.cop4331.group13.cavecheckin.dao.SessionDao;
import com.cop4331.group13.cavecheckin.dao.UserDao;
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
import java.util.Date;

@Service
public class SessionService {
    @Autowired
    private SessionDao dao;

    @Autowired
    private UserDao userDao;

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

        dao.save(session);

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
