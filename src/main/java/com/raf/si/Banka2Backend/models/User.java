package com.raf.si.Banka2Backend.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(
        name = "users",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"email", "jmbg"})})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message="This field is required.")
    @Size(max = 50, message = "Input too long, cannot contain more than 50 characters.")
//    @Pattern(regexp = "^(?=.{1,50}@)[A-Za-z0-9_-]+(\\\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\\\.[A-Za-z0-9-]+)*(\\\\.[A-Za-z]{2,})$",
//            message = "Must enter a valid email.")//todo popravi regex, ubija front requestove
    @Email
    private String email;

    //@JsonIgnore
    @NotNull(message="This field is required.")
    @Pattern(regexp = "^.*(?=.{10,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
            message = "Make sure the password has at least 10 characters, one digit, one lowercase and one uppercase letter and at least one special character.")
    private String password;

    @NotNull(message="This field is required.")
    @Size(max = 20, message = "Input too long, cannot contain more than 20 characters.")
    private String firstName;

    @NotNull(message="This field is required.")
    @Size(max = 20, message = "Input too long, cannot contain more than 20 characters.")
    private String lastName;

    @NotNull(message="This field is required.")
    @Size(max = 13, message = "Input too long, cannot contain more than 13 characters.")
//    @Pattern(regexp = "^\\\\d+$",
//            message = "Invalid input.")//todo popravi regex, ubija front requestove
    private String jmbg;

    @NotNull(message="This field is required.")
    @Size(max = 20, message = "Input too long, cannot contain more than 20 characters.")
    private String phone;

    @NotNull(message="This field is required.")
    @Size(max = 20, message = "Input too long, cannot contain more than 20 characters.")
    private String jobPosition;

    private boolean active;

    @ManyToMany
    @JoinTable(
            name = "users_permissions",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "permission_id", referencedColumnName = "id")}
    )
    private List<Permission> permissions;
}
