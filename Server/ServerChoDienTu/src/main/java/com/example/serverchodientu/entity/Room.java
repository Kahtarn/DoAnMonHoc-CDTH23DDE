package com.example.serverchodientu.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "room")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User senderId;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiverId;

    public Room(Integer id, String name, User senderId, User receiverId) {
        this.id = id;
        this.name = name;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public Room() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getSenderId() {
        return senderId;
    }

    public void setSenderId(User senderId) {
        this.senderId = senderId;
    }

    public User getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(User receiverId) {
        this.receiverId = receiverId;
    }
}
