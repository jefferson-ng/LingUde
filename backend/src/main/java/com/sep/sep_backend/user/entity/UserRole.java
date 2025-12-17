package com.sep.sep_backend.user.entity;

/**
 * Represents the role of a user in the system.
 *
 * <p>
 * For now we only distinguish between:
 * <ul>
 *     <li>{@link #USER}  – normal learner using the app</li>
 *     <li>{@link #ADMIN} – administrator who can access the admin panel
 *                          and manage users/content</li>
 * </ul>
 * </p>
 *
 * <p>
 * More roles can be added later if needed (e.g. MODERATOR, TEACHER, ...).
 * </p>
 */
public enum UserRole {

    /**
     * Default role for normal learners.
     */
    USER,

    /**
     * Elevated role for administrators.
     *
     * <p>
     * Users with this role are allowed to access admin-only endpoints
     * under <code>/api/admin/**</code>.
     * </p>
     */
    ADMIN
}
