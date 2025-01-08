package project.todo.service.security;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class PasswordEncrypt {

    public String toHash(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("비밀번호를 입력해주세요.");
        }

        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-256");
            instance.update(value.getBytes());
            return bytesToHex(instance.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}
