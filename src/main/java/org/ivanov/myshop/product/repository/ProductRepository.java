package org.ivanov.myshop.product.repository;

import org.ivanov.myshop.product.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Long>, CrudRepository<Product, Long> {
    @Query("""
    SELECT p FROM Product p
""")
    List<Product> getProducts(Pageable pageable);
}
