package com.epam.test_generator.entities;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.codehaus.jackson.annotate.JsonIgnore;

@Entity
public class User {

    public User() {
    }

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @JsonIgnore
    private String password;

    private Role role;

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }


    public void setPassword(String password) {

        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", password='" + password + '\'' +
            ", role='" + role + '\'' +
            '}';
    }

}
