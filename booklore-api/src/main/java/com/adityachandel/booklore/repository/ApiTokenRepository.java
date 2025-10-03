package com.adityachandel.booklore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.adityachandel.booklore.model.entity.ApiTokenEntity;
import com.adityachandel.booklore.model.entity.BookLoreUserEntity;

@Repository
public interface ApiTokenRepository extends JpaRepository<ApiTokenEntity, Long> {

    boolean existsByToken(String token);

    List<ApiTokenEntity> findByUser(BookLoreUserEntity user);
    
    ApiTokenEntity findByToken(String token);

    @Query("SELECT t.user FROM ApiTokenEntity t WHERE t.token = :token")
    BookLoreUserEntity findUserByToken(String token);
}
