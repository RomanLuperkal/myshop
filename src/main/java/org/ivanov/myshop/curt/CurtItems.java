package org.ivanov.myshop.curt;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "curt_items")
@Getter
@Setter
public class CurtItems {
    private Long curtItemId;
    private Curt curt;

}
