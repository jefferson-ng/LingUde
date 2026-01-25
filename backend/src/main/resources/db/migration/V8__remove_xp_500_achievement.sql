-- V8__remove_xp_500_achievement.sql
-- Removes the XP_500 achievement that was added by AchievementDataInitializer
-- This achievement should not exist in the system

-- First remove any user_achievements referencing this achievement
DELETE FROM user_achievements 
WHERE achievement_id IN (SELECT id FROM achievements WHERE code = 'XP_500');

-- Then remove the achievement itself
DELETE FROM achievements WHERE code = 'XP_500';
