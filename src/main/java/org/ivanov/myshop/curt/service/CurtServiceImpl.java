package org.ivanov.myshop.curt.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ivanov.myshop.curt.dto.CreateCurtDto;
import org.ivanov.myshop.curt.enums.Status;
import org.ivanov.myshop.curt.model.Curt;
import org.ivanov.myshop.curt.repository.CurtRepository;
import org.ivanov.myshop.curt_items.model.CurtItems;
import org.ivanov.myshop.product.model.Product;
import org.ivanov.myshop.product.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CurtServiceImpl implements CurtService {
    private final CurtRepository curtRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void addToCurt(CreateCurtDto dto) {
        Curt curt = curtRepository.findByUserIpAndStatus(dto.userIp(), Status.CREATED).orElseGet(Curt::new);
        Product product = productRepository.findById(dto.productId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Продукта с id = " + dto.productId() + " не существует"));
        CurtItems curtItems;
        if (curt.getOrderedProducts().isEmpty()) {
            curt.setUserIp(dto.userIp());
            curtItems = new CurtItems();
            curtItems.setCurt(curt);
            curtItems.setProduct(product);
            product.setCount(product.getCount() - dto.count());
            curtItems.setCount(dto.count());
            curt.getOrderedProducts().add(curtItems);
            productRepository.save(product);
            curtRepository.save(curt);
            System.out.println();
        }
    }
}
