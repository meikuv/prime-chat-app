package com.meikuv.chatapp.repository;

import com.meikuv.chatapp.controller.response.StatusType;
import com.meikuv.chatapp.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel, Long> {
    Optional<UserModel> findByEmail(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    UserModel findByUsername(String username);

    List<UserModel> findAllByStatusType(StatusType statusType);
}
