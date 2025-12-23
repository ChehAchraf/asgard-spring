package com.trans.asgard.domain.iam.repository;

import com.trans.asgard.domain.iam.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

}
