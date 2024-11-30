package br.ufpr.tads.user.register.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@Entity
@Table(name = "BRANCH")
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID correlationId;

    private String cpnj;

    @ManyToOne
    @JoinColumn(name = "STORE_ID", nullable = false)
    @ToString.Exclude
    private Store store;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ADDRESS_ID", referencedColumnName = "id")
    private Address address;

}