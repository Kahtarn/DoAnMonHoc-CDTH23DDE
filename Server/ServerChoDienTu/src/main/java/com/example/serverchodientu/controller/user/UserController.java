package com.example.serverchodientu.controller.user;

import com.example.serverchodientu.dto.ApiResponse;
import com.example.serverchodientu.dto.user.UpdateAvatarRequest;
import com.example.serverchodientu.dto.user.privateUser.EditProfileRequest;
import com.example.serverchodientu.dto.user.privateUser.ProfileUserResponse;
import com.example.serverchodientu.dto.user.publicUser.DetailsUserResponse;
import com.example.serverchodientu.entity.User;
import com.example.serverchodientu.service.user.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/details-user/{id}")
    public ResponseEntity<ApiResponse<DetailsUserResponse>> getDetailsUser(@PathVariable Integer id) {
        DetailsUserResponse detailsUser = userService.getDetailsUser(id);
        return ResponseEntity.ok(ApiResponse.ok(detailsUser));
    }

    @GetMapping("/my-profile")
    public ResponseEntity<ApiResponse<ProfileUserResponse>> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getName();

        ProfileUserResponse profile = userService.getProfileUser(currentEmail);
        return ResponseEntity.ok(ApiResponse.ok(profile));
    }

    @PutMapping("/edit-profile")
    public ResponseEntity<ApiResponse<Object>> editMyProfile(@RequestBody EditProfileRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getName();

        User u = userService.editProfile(currentEmail,request);
        return ResponseEntity.ok(ApiResponse.ok(u));
    }

    @PostMapping(value = "/update-avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<User>> updateAvatar(@ModelAttribute UpdateAvatarRequest request) {
        if (request.getAvatarUrl() == null || request.getAvatarUrl().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Vui lòng chọn ảnh đại diện!"));
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getName();

        User updatedUser = userService.updateAvatar(currentEmail, request.getAvatarUrl());

        return ResponseEntity.ok(ApiResponse.ok(updatedUser));
    }
}
