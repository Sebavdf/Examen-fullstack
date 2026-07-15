package com.fullrepar.auth_service.repository; 

import com.fullrepar.auth_service.model.User; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their username.
     * @param username the username to search for.
     * @return an Optional containing the found user, or empty.
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their email.
     * @param email the email to search for.
     * @return an Optional containing the found user, or empty.
     */
    Optional<User> findByEmail(String email);
}