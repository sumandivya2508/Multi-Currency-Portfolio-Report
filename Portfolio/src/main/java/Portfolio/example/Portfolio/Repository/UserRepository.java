package Portfolio.example.Portfolio.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import Portfolio.example.Portfolio.Entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(
            value = "SELECT * FROM users WHERE username = :username",
            nativeQuery = true
    )
    Optional<User> findByUsername(@Param("username") String username);

    @Query(
            value = "SELECT * FROM users WHERE email = :email",
            nativeQuery = true
    )
    Optional<User> findByEmail(@Param("email") String email);

    @Query(
            value = "SELECT EXISTS (" +
                    "SELECT 1 FROM users WHERE username = :username" +
                    ")",
            nativeQuery = true
    )
    boolean existsByUsername(@Param("username") String username);

    @Query(
            value = "SELECT EXISTS (SELECT 1 FROM users WHERE email = :email)",
            nativeQuery = true
    )
    boolean existsByEmail(@Param("email") String email);

}
