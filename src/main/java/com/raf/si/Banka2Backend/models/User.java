package com.raf.si.Banka2Backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @Pattern(regexp = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$",
            message = "Must enter a valid email.")
    @Email
    private String email;

    @JsonIgnore
    @NotNull(message="This field is required.")
    @Pattern(regexp = "^(?=.*?[a-zA-Z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{10,}$",
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
    @Pattern(regexp = "^\\d+$", message = "Only numbers are allowed in a JMBG identifier.")
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
