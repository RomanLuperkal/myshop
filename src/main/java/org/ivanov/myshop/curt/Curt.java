package org.ivanov.myshop.curt;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Curt {
    @Id
    @Column(name = "curt_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long curtId;
    @Column(name = "user_ip")
    private String userIp;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
