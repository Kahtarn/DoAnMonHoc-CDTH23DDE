package com.example.serverchodientu.service.user;

import com.example.serverchodientu.dto.user.privateUser.EditProfileRequest;
import com.example.serverchodientu.dto.user.privateUser.ProfileUserResponse;
import com.example.serverchodientu.dto.user.publicUser.DetailsUserResponse;
import com.example.serverchodientu.entity.User;
import com.example.serverchodientu.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class UserService {
    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public DetailsUserResponse getDetailsUser(Integer id) {
        User u = userRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        DetailsUserResponse detailsUser = new DetailsUserResponse();
        detailsUser.setFullName(u.getFullName());
        detailsUser.setEmail(u.getEmail());
        detailsUser.setPhone(u.getPhone());
        detailsUser.setGender(u.isGender());
        detailsUser.setProvinceName(u.getProvinceName());
        detailsUser.setWardName(u.getWardName());
        detailsUser.setCreateAt(u.getCreateAt());
        detailsUser.setAvatarUrl(u.getAvatarUrl());

        return detailsUser;
    }

    public ProfileUserResponse getProfileUser(String email) {
        User u = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dữ liệu người dùng!"));
        ProfileUserResponse profile = new ProfileUserResponse();
        profile.setFullName(u.getFullName());
        profile.setEmail(u.getEmail());
        profile.setPhone(u.getPhone());
        profile.setGender(u.isGender());
        profile.setProvinceName(u.getProvinceName());
        profile.setWardName(u.getWardName());
        profile.setAvatarUrl(u.getAvatarUrl());
        profile.setCreateAt(u.getCreateAt());

        return profile;
    }

    public User editProfile(String email, EditProfileRequest edit) {
        User u = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dữ liệu người dùng"));
        u.setFullName(edit.getFullName());
        u.setProvinceName(edit.getProvinceName());
        u.setWardName(edit.getWardName());
        u.setGender(edit.getGender());

        return userRepo.save(u);
    }

    public User updateAvatar(String email, MultipartFile file) {
        User u = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        try {
            String uploadDir = "uploads/avatars/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String avatarUrl = "/uploads/avatars/" + fileName;
            u.setAvatarUrl(avatarUrl);

            return userRepo.save(u);

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu file ảnh: " + e.getMessage());
        }
    }
}
