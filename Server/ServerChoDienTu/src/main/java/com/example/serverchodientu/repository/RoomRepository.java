package com.example.serverchodientu.repository;

import com.example.serverchodientu.entity.Room;
import com.example.serverchodientu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findRoomBySenderId_IdAndReceiverId_Id(int senderId, int receiverId);

    Optional<Room> findBySenderId(User senderId);
}
