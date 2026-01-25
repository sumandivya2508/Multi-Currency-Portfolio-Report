package Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


    @Entity
    @Table(name = "users")
    @EntityListeners(AuditingEntityListener.class)
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(unique = true, nullable = false, length = 50)
        private String username;

        @Column(nullable = false)
        private String password;

        @Column(unique = true, nullable = false)
        private String email;

        @Column(nullable = false)
        private String fullName;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private UserRole role;

        @Column(nullable = false)
        private Boolean enabled = true;

        @CreatedDate
        @Column(nullable = false, updatable = false)
        private LocalDateTime createdAt;

        @LastModifiedDate
        private LocalDateTime updatedAt;

        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private Set<Portfolio> portfolios = new HashSet<>();
    }
