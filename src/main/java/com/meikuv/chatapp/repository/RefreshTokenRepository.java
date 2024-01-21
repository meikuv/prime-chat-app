package com.meikuv.chatapp.repository;

import com.meikuv.chatapp.model.RefreshTokenModel;
import com.meikuv.chatapp.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenModel, Long> {
    Optional<RefreshTokenModel> findByToken(String token);

    int deleteByUser(UserModel userModel);
}
