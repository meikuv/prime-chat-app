package com.meikuv.chatapp.repository;

import com.meikuv.chatapp.model.ChatRoomModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoomModel, Long> {
}
