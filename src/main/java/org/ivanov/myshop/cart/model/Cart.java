package org.ivanov.myshop.cart.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.ivanov.myshop.cart.enums.Status;
import org.ivanov.myshop.cart_item.model.CartItems;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Cart {
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
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItems> orderedProducts = new HashSet<>();
}
