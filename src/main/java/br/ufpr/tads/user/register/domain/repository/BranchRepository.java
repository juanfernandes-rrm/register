package br.ufpr.tads.user.register.domain.repository;

import br.ufpr.tads.user.register.domain.model.Branch;
import br.ufpr.tads.user.register.domain.model.Store;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BranchRepository extends JpaRepository<Branch, UUID> {
    Optional<Branch> findByCorrelationId(UUID id);

    SliceImpl<Branch> findByStore(Store storeId, Pageable pageable);
}
