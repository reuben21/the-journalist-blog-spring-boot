package com.reuben.thejournalist.repository;

import com.reuben.thejournalist.model.UserEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserEntityRepository extends MongoRepository<UserEntity, ObjectId> {
    Optional<UserEntity> findUserByEmail(String email); // Find a user by email
}
