package com.sep.sep_backend.exercise.dto;

/**
 * Data Transfer Object (DTO) representing a user's progress
 * within a specific lesson or exercise session.
 *
 * <p>
 * This object is returned by the progress endpoint and is intended
 * to provide the frontend with the minimal information required
 * to render a progress indicator (e.g. "3 / 10 completed" or a
 * progress bar).
 * </p>
 *
 * <p><b>Typical usage:</b></p>
 * <ul>
 *     <li>The service layer calculates how many questions belong
 *         to the current session ({@code totalCount}).</li>
 *     <li>It also determines how many of those questions the user
 *         has already completed ({@code completedCount}).</li>
 *     <li>An instance of this class is created and returned by the
 *         REST controller as JSON.</li>
 * </ul>
 */
public class SessionProgressResponse {

    /**
     * The number of exercises in the current session that the user
     * has already completed.
     *
     * <p>
     * This value is typically derived from {@link com.sep.sep_backend.exercise.entity.UserProgress}
     * entries where {@code isCompleted = true} for the current user
     * and session.
     * </p>
     */
    private int completedCount;

    /**
     * The total number of exercises that belong to the current
     * lesson or session.
     *
     * <p>
     * This includes both completed and not-yet-completed exercises
     * and is used together with {@link #completedCount} to compute
     * the user's progress.
     * </p>
     */
    private int totalCount;

    /**
     * Default no-args constructor.
     *
     * <p>
     * Required for frameworks such as Jackson that instantiate
     * DTOs via reflection.
     * </p>
     */
    public SessionProgressResponse() {
    }

    /**
     * Convenience constructor to create a fully initialized
     * progress response.
     *
     * @param completedCount The number of completed exercises.
     * @param totalCount     The total number of exercises in the session.
     */
    public SessionProgressResponse(int completedCount, int totalCount) {
        this.completedCount = completedCount;
        this.totalCount = totalCount;
    }

    /**
     * Returns how many exercises in the current session have already
     * been completed by the user.
     *
     * @return The completed exercise count.
     */
    public int getCompletedCount() {
        return completedCount;
    }

    /**
     * Sets the number of exercises that the user has completed in
     * the current session.
     *
     * @param completedCount The completed exercise count.
     */
    public void setCompletedCount(int completedCount) {
        this.completedCount = completedCount;
    }

    /**
     * Returns the total number of exercises in the current session.
     *
     * @return The total exercise count.
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * Sets the total number of exercises belonging to the current
     * lesson or session.
     *
     * @param totalCount The total exercise count.
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
