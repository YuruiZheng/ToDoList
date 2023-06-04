package server.service.impl;

import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import server.service.AdminService;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
public class AdminServiceImpl implements AdminService {

    @Value("${server.admin-password}")
    private String adminPassword;
    @Override
    public boolean checkPassword(String password) {
        String sha256hex = Hashing.sha256()
                .hashString(password, StandardCharsets.UTF_8)
                .toString();
        return Objects.equals(adminPassword, sha256hex);
    }
}
