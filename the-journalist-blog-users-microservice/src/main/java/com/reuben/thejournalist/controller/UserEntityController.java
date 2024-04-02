package com.reuben.thejournalist.controller;


import com.reuben.thejournalist.auth.AuthenticationResponse;
import com.reuben.thejournalist.config.JwtService;
import com.reuben.thejournalist.model.UserEntity;
import com.reuben.thejournalist.repository.UserEntityRepository;
import com.reuben.thejournalist.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserEntityController {
    private final JwtService jwtService;
    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AuthenticationService authenticationService;
    private static final Logger LOGGER = Logger.getLogger(UserEntityController.class.getName());

//    @RequestMapping(value = "/")
//    public void redirect(HttpServletResponse response) throws IOException {
//        response.sendRedirect("/swagger-ui.html");
//    }

    // Get request to get all UserEntities
    @GetMapping("/list")
    public ResponseEntity<List<UserEntity>> getAllUserEntities() {
        LOGGER.info("Getting all User Entity's");
        List<UserEntity> userEntities = userEntityRepository.findAll();
        return ResponseEntity.ok(userEntities);
    }

    @GetMapping("/email/{emailId}")
    public ResponseEntity<Optional<UserEntity>> getUserEntityByEmail(@PathVariable String emailId) {
        LOGGER.info("Getting UserEntity by email: " + emailId);
        Optional<UserEntity> UserEntity = userEntityRepository.findUserByEmail(emailId);
        if (UserEntity.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(UserEntity);
    }

    @PostMapping("/register")
    public ResponseEntity<?> addUserEntity(
            @Valid @RequestBody UserEntity newUserEntity,
            BindingResult result) {
        LOGGER.info("Adding new UserEntity");
        LOGGER.info("New UserEntity: " + newUserEntity);
        LOGGER.info("Errors: " + result.getAllErrors());
        UserEntity userEntity = new UserEntity(newUserEntity.getName(), newUserEntity.getEmail(), newUserEntity.getPassword());
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        } else {
            Optional<UserEntity> existingUserEntity = userEntityRepository.findUserByEmail(userEntity.getEmail());
            if (existingUserEntity.isPresent()) {
                // Prepare a structured response for the conflict case
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "UserEntity with email " + userEntity.getEmail() + " already exists");
                LOGGER.warning("UserEntity with email " + userEntity.getEmail() + " already exists");
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(errorResponse);
            }
            userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
            var jwtToken = jwtService.generateToken(userEntity,userEntity);
            var refreshToken = jwtService.generateRefreshToken(userEntity);
            // Save the new UserEntity to the database
            UserEntity savedUserEntity = userEntityRepository.save(userEntity);

            AuthenticationResponse authenticationResponse = new AuthenticationResponse(savedUserEntity,jwtToken, refreshToken);
            // Return the saved UserEntity and a status of 201 (Created)
            return ResponseEntity.status(HttpStatus.CREATED).body(authenticationResponse);
        }
        // Before saving, you might want to check if a UserEntity with the same email already exists

    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
            @RequestBody UserEntity request
    ) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userEntityRepository.findUserByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user,user);
        var refreshToken = jwtService.generateRefreshToken(user);
//        authenticationService.revokeAllUserTokens(user);
//        authenticationService.saveUserToken(user, jwtToken);
        AuthenticationResponse authenticationResponse = new AuthenticationResponse(user,jwtToken, refreshToken);
        // Return the saved UserEntity and a status of 201 (Created)
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationResponse);
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authenticationService.refreshToken(request, response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserEntity(@PathVariable ObjectId id, @RequestBody UserEntity UserEntityUpdates) {
        return userEntityRepository.findById(id)
                .map(UserEntity -> {
                    System.out.println("UserEntity to update: " + UserEntity);
                    // Update UserEntity details only if they are not null
                    if (UserEntityUpdates.getName() != null) UserEntity.setName(UserEntityUpdates.getName());
                    if (UserEntityUpdates.getEmail() != null) UserEntity.setEmail(UserEntityUpdates.getEmail());
                    if (UserEntityUpdates.getPassword() != null) UserEntity.setPassword(UserEntityUpdates.getPassword());

                    // Save the updated UserEntity
                    UserEntity updatedUserEntity = userEntityRepository.save(UserEntity);
                    return ResponseEntity.ok(updatedUserEntity);
                })
                .orElseGet(() -> ResponseEntity.notFound().build()); // UserEntity not found
    }

}
