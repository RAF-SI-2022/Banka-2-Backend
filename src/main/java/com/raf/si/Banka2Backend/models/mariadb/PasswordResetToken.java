package com.raf.si.Banka2Backend.models.mariadb;

import lombok.Data;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Data
@Entity
@Table(
        name = "passwordResetTokens",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"id", "user_id"})})
public class PasswordResetToken {
    private static final int EXPIRATION = 15;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private Date expirationDate;

    public PasswordResetToken(User client, String token) {
        LocalDateTime expiration = LocalDateTime.now().plus(Duration.ofMinutes(this.EXPIRATION));
        this.token = token;
        this.user = client;
        this.expirationDate = Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant());
    }

    public PasswordResetToken() {
    }
}
