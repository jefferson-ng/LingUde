package com.sep.sep_backend.exercise.dto;

/**
 * Data Transfer Object (DTO) representing a user's progress
 * within a specific lesson or exercise session.
 *
 * <p>
 * This DTO is returned by the progress endpoint and provides
 * the minimal information required by the frontend to render
 * a progress indicator (for example: "3 / 10 completed" or a
 * percentage-based progress bar).
 * </p>
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *     <li>Expose the number of exercises the user has already
 *         completed in the current session.</li>
 *     <li>Expose the total number of exercises that belong to
 *         the current session.</li>
 *     <li>Remain independent of how a "session" is defined
 *         (e.g., by lesson, category such as vocabulary /
 *         synonym / grammar, or a dedicated session entity).</li>
 * </ul>
 *
 * <p>
 * The intention is to keep this DTO stable even if the domain
 * model evolves. Only the service logic that populates this
 * DTO needs to change when new exercise classifications or
 * session concepts are introduced.
 * </p>
 */
public class SessionProgressResponse {

    /**
     * The number of exercises in the current session that the user
     * has already completed.
     *
     * <p>
     * Typically, this value is derived from
     * {@link com.sep.sep_backend.exercise.entity.UserProgress}
     * entries where:
     * </p>
     *
     * <ul>
     *     <li>{@code isCompleted = true}, and</li>
     *     <li>the exercise belongs to the session that is currently
     *         being viewed (e.g., a specific lesson, topic or
     *         future category such as vocabulary, synonym, grammar).</li>
     * </ul>
     */
    private int completedCount;

    /**
     * The total number of exercises that belong to the current session.
     *
     * <p>
     * This includes both completed and not-yet-completed exercises.
     * The difference between {@link #totalCount} and
     * {@link #completedCount} gives the number of remaining exercises.
     * </p>
     */
    private int totalCount;

    /**
     * Default no-argument constructor.
     *
     * <p>
     * Required by frameworks such as Jackson for object construction
     * during JSON (de-)serialization.
     * </p>
     */
    public SessionProgressResponse() {
    }

    /**
     * Convenience constructor that initializes both progress counters.
     *
     * @param completedCount The number of completed exercises in the session.
     * @param totalCount     The total number of exercises in the session.
     */
    public SessionProgressResponse(int completedCount, int totalCount) {
        this.completedCount = completedCount;
        this.totalCount = totalCount;
    }

    /**
     * Returns how many exercises in the current session have been
     * completed by the user.
     *
     * @return The completed exercise count.
     */
    public int getCompletedCount() {
        return completedCount;
    }

    /**
     * Sets how many exercises in the current session have been
     * completed by the user.
     *
     * @param completedCount The completed exercise count.
     */
    public void setCompletedCount(int completedCount) {
        this.completedCount = completedCount;
    }

    /**
     * Returns the total number of exercises that belong to the
     * current session.
     *
     * @return The total exercise count.
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * Sets the total number of exercises that belong to the
     * current session.
     *
     * @param totalCount The total exercise count.
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
