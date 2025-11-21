package com.sep.sep_backend.exercise.repository;

import com.sep.sep_backend.exercise.entity.ExerciseFillBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ExerciseFillBlankRepository
        extends JpaRepository<ExerciseFillBlank, UUID>, JpaSpecificationExecutor<ExerciseFillBlank> {
}
