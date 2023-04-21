package com.raf.si.Banka2Backend.models.mariadb;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users_options")
public class UserOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @ManyToOne
    @JoinColumn(name = "option_id")
    @NotNull
    private Option option;

    private Double premium;

    @NotNull
    private Integer amount;

    @NotNull
    private String type;

    @NotNull
    private LocalDate expirationDate;

    @NotNull
    private Double strike;
}
