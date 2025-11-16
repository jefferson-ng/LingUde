package com.sep.sep_backend.exercise.repository;

import com.sep.sep_backend.exercise.entity.ExerciseType;
import com.sep.sep_backend.exercise.entity.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserProgressRepository extends JpaRepository<UserProgress, UUID> {

    Optional<UserProgress> findByUserIdAndExerciseIdAndExerciseType(
            UUID userId, UUID exerciseId, ExerciseType exerciseType
    );
}
