package org.ivanov.myshop.curt.repository;

import org.ivanov.myshop.curt.enums.Status;
import org.ivanov.myshop.curt.model.Curt;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurtRepository extends CrudRepository<Curt, Long> {
    Optional<Curt> findByUserIpAndStatus(String userIp, Status status);
}
