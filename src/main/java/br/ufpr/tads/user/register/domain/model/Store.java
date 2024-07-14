package br.ufpr.tads.user.register.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "STORE")
public class Store extends User {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String CNPJ;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Branch> branches = new ArrayList<>();
}