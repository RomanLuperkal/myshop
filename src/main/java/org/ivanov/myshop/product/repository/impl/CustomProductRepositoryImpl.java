package org.ivanov.myshop.product.repository.impl;

import lombok.RequiredArgsConstructor;
import org.ivanov.myshop.product.model.Product;
import org.ivanov.myshop.product.repository.CustomProductRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
@RequiredArgsConstructor
public class CustomProductRepositoryImpl implements CustomProductRepository {
    private final R2dbcEntityTemplate entityTemplate;
    @Override
    public Flux<Product> findProducts(String search, Pageable pageable) {
        Criteria criteria = Criteria.empty();

        if (search != null && !search.isBlank()) {
            criteria = criteria.and("product_name").like("%" + search.toLowerCase() + "%");
        }

        Query query = Query.query(criteria)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset()).sort(pageable.getSort());

        /*pageable.getSort().forEach(order -> {
            query.sort(Sort.by(order.getDirection(), order.getProperty()));
        });*/
        //query.sort(pageable.getSort());

        // Выполняем запрос
        return entityTemplate.select(Product.class)
                .matching(query)
                .all();
    }
    }

