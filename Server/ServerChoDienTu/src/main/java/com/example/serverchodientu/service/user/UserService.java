package com.example.serverchodientu.service.user;

import com.example.serverchodientu.dto.ApiResponse;
import com.example.serverchodientu.dto.user.publicUser.DetailsUser;
import com.example.serverchodientu.entity.User;
import com.example.serverchodientu.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public DetailsUser getDetailsUser(Integer id) {
        User u = userRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));
        DetailsUser detailsUser = new DetailsUser();
        detailsUser.setFullName(u.getFullName());
        detailsUser.setEmail(u.getEmail());
        detailsUser.setPhone(u.getPhone());
        detailsUser.setGender(u.isGender());
        detailsUser.setProvinceName(u.getProvinceName());
        detailsUser.setWardName(u.getWardName());
        detailsUser.setCreateAt(u.getCreateAt());

        return detailsUser;
    }
}
