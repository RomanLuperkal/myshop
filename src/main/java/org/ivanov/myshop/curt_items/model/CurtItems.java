package org.ivanov.myshop.curt_items.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.ivanov.myshop.curt.model.Curt;
import org.ivanov.myshop.product.model.Product;

@Entity
@Table(name = "curt_items")
@Getter
@Setter
public class CurtItems {
    @Id
    @Column(name = "curt_item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long curtItemId;
    @ManyToOne
    @JoinColumn(name = "curt_id")
    private Curt curt;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    private Integer count;

}
