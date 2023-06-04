package server.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import server.service.AdminService;

import static org.junit.jupiter.api.Assertions.*;

class AdminServiceImplTest {

    private AdminService adminService = new AdminServiceImpl();

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(adminService, "adminPassword",
                "3d7b557f4b2b3b7737f671ebb80b69b1eec19fe835e8ad5ac6d23a52483ce605");
    }

    @Test
    void checkPassword() {
        assertTrue(adminService.checkPassword("oopp77"));
        assertFalse(adminService.checkPassword("oopp"));
    }
}