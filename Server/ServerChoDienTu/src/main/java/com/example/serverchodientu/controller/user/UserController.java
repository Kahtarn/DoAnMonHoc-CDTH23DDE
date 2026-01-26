package com.example.serverchodientu.controller.user;

import com.example.serverchodientu.dto.ApiResponse;
import com.example.serverchodientu.dto.user.publicUser.DetailsUser;
import com.example.serverchodientu.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/detailsUser/{id}")
    public ResponseEntity<ApiResponse<DetailsUser>> getDetailsUser(@PathVariable Integer id) {
        DetailsUser detailsUser = userService.getDetailsUser(id);

        return ResponseEntity.ok(ApiResponse.ok(detailsUser));
    }
}
