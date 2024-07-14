package br.ufpr.tads.user.register.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "COSTUMER")
public class Customer extends User {

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

}
