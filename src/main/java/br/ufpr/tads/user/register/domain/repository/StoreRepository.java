package br.ufpr.tads.user.register.domain.repository;

import br.ufpr.tads.user.register.domain.model.Store;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StoreRepository extends JpaRepository<Store, UUID> {

    Optional<Store> findByKeycloakId(UUID keycloakId);

    Slice<Store> findByNameContainingIgnoreCase(String firstname, Pageable pageable);

    Slice<Store> findByApprovedNullAndKeycloakIdNotNull(Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Store> findByCnpjRoot(String cnpjRoot);
}
