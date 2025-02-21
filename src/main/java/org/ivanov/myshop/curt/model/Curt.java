package org.ivanov.myshop.curt.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.ivanov.myshop.curt.enums.Status;
import org.ivanov.myshop.curt_items.model.CurtItems;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
    @Column(name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.CREATED;
    @OneToMany(mappedBy = "curt", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CurtItems> orderedProducts = new HashSet<>();
}
