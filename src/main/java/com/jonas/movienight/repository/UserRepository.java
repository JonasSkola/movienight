package com.jonas.movienight.repository;

import com.jonas.movienight.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Jonas Karlsson on 2019-01-09.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

   UserEntity findByEmail(String email);

   UserEntity findByUsername(String username);

   UserEntity getById(Long id);

}
