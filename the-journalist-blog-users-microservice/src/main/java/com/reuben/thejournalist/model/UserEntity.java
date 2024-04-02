package com.reuben.thejournalist.model;


import commons.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class UserEntity implements UserDetails {

    @Id
    private @MongoId ObjectId _id;

    @NotNull(message = "Name must not be null")
    @NotBlank(message = "Name is required")
    private String name;

    @Indexed(unique = true)
    @NotNull(message = "Email must not be null")
    @Email(message = "Email is in an invalid format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @NotNull(message = "Password must not be null")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;


    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.PUBLIC)
    private List<Role> role = List.of(Role.USER); // Initialize directly; no setter means it cannot be changed

    //    private List<Token> tokens;
    public UserEntity(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getIdAsString() {
        return _id.toHexString();
    }


    //    public void setRole(Roles role, User requester) {
//        if (requester != null && requester.getRole() == Roles.ADMIN) {
//            this.role = role;
//        } else {
//            throw new IllegalStateException("Only an admin can change the user role.");
//        }
//    }

    @Override
    public String toString() {
        return "User{" + "name=" + name + ", email=" + email + ", password=" + password + ", role=" + role + '}';
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.stream().flatMap(r->r.getAuthorities().stream()).collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
