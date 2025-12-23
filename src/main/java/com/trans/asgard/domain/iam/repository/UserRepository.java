package com.trans.asgard.domain.iam.repository;

import com.trans.asgard.domain.iam.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findUserByLogin(String login);

    boolean existsByLogin(String login);

}
