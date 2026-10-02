package com.devicelife.entity;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long userId;

    @Column(nullable = false,unique = true,length=50)
    private String username;

    @Column(nullable = false,length = 100)
    private String password;

    @Column(length = 50)
    private String name;

    @Column(nullable = false,unique = true,length = 100)
    private String email;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist(){
        if(createdAt==null)createdAt=LocalDateTime.now();
    }
}
