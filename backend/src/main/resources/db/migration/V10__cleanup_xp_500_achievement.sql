-- V10__cleanup_xp_500_achievement.sql
-- Final cleanup: Ensures XP_500 achievement is removed
-- This handles cases where the achievement was created after V8 migration ran

-- First remove any user_achievements referencing this achievement
DELETE FROM user_achievements
WHERE achievement_id IN (SELECT id FROM achievements WHERE code = 'XP_500');

-- Then remove the achievement itself
DELETE FROM achievements WHERE code = 'XP_500';
