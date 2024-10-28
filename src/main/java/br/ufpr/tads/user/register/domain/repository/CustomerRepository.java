package br.ufpr.tads.user.register.domain.repository;

import br.ufpr.tads.user.register.domain.model.Address;

import java.util.UUID;

import br.ufpr.tads.user.register.domain.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Address, UUID> {
}