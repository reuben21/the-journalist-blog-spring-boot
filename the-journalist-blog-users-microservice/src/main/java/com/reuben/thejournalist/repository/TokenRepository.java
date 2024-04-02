package com.reuben.thejournalist.repository;

import com.reuben.thejournalist.model.Token;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends MongoRepository<Token,ObjectId> {
    @Query("{'user._id': ?0, '$or': [{'expired': false}, {'revoked': false}]}")
    List<Token> findValidTokenByUser(ObjectId userId); // Find all valid tokens for a user

    Optional<Token> findByToken(String token);
}
