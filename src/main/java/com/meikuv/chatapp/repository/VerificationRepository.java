package com.meikuv.chatapp.repository;

import com.meikuv.chatapp.model.VerificationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationRepository extends JpaRepository<VerificationModel, Long> {

    Optional<VerificationModel> findByCodeAndEmail(String code, String email);
}
