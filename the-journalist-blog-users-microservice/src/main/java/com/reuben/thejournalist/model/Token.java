package com.reuben.thejournalist.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "token")
public class Token {

    private @MongoId ObjectId _id;

    private String token;

    private TokenType tokenType = TokenType.BEARER;

    public boolean revoked;

    public boolean expired;

    @Field("user")
    @DBRef
    private UserEntity user;
}
