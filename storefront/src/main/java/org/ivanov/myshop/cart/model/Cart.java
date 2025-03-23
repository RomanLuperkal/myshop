package org.ivanov.myshop.cart.model;

import lombok.Getter;
import lombok.Setter;
import org.ivanov.myshop.cart.enums.Status;
import org.ivanov.myshop.cart_item.model.CartItems;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class Cart {
    @Id
    @Column("curt_id")
    private Long cartId;
    @Column("user_ip")
    private String userIp;
    @Column("creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();
    @Column("confirmed_date")
    private LocalDateTime confirmedDate;
    private Status status = Status.CREATED;
    @Transient
    private Set<CartItems> orderedProducts = new HashSet<>();
}
