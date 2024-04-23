package com.modutaxi.api.domain.onboarding.repository;

import com.modutaxi.api.domain.onboarding.entity.Onboarding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OnboardingRepository extends JpaRepository<Onboarding, Long> {

    Optional<Onboarding> findByQuestionId(int questionId);

}
