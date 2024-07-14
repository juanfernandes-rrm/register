package br.ufpr.tads.user.register.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "BRANCH")
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID correlationId;

    @ManyToOne
    @JoinColumn(name = "STORE_ID", nullable = false)
    private Store store;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ADDRESS_ID", referencedColumnName = "id")
    private Address address;

}