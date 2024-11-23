package br.ufpr.tads.user.register.domain.repository;

import br.ufpr.tads.user.register.domain.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByKeycloakId(UUID keycloakId);

    Page<Customer> findAllByKeycloakIdIn(List<UUID> userIds, Pageable pageable);

    Slice<Customer> findByFirstNameContainingIgnoreCase(String name, Pageable pageable);
}
