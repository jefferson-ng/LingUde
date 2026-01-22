package com.sep.sep_backend.referral.repository;

import com.sep.sep_backend.referral.entity.Referral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReferralRepository extends JpaRepository<Referral, UUID> {

    Optional<Referral> findByUserId(UUID userId);

    Optional<Referral> findByReferralCode(String referralCode);

    boolean existsByUserId(UUID userId);

    boolean existsByReferralCode(String referralCode);
}
