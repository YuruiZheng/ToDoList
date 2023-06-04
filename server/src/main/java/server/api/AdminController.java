package server.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.service.AdminService;

@RestController
@RequestMapping("/api/root")
public class AdminController {

    private AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/{password}")
    public ResponseEntity<Boolean> checkPassword(@PathVariable String password) {
        if (adminService.checkPassword(password))
            return ResponseEntity.ok(true);
        else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(false);
    }
}
