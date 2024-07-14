package br.ufpr.tads.user.register.domain.repository;

import br.ufpr.tads.user.register.domain.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BranchRepository extends JpaRepository<Branch, UUID> {
    Optional<Branch> findByCorrelationId(UUID id);
}
