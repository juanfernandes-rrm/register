package br.ufpr.tads.user.register.domain.repository;

import br.ufpr.tads.user.register.domain.model.Branch;
import br.ufpr.tads.user.register.domain.model.Store;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BranchRepository extends JpaRepository<Branch, UUID> {
    Optional<Branch> findByCorrelationId(UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Branch b WHERE b.correlationId = :correlationId")
    Optional<Branch> findByCorrelationIdWithLock(@Param("correlationId") UUID correlationId);


    SliceImpl<Branch> findByStore(Store storeId, Pageable pageable);
}
