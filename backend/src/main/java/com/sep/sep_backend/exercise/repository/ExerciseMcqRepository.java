package com.sep.sep_backend.exercise.repository;

import com.sep.sep_backend.exercise.entity.ExerciseMcq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ExerciseMcqRepository
        extends JpaRepository<ExerciseMcq, UUID>, JpaSpecificationExecutor<ExerciseMcq> {
}
