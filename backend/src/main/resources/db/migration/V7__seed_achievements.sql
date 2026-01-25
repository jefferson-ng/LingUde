-- V7__seed_achievements.sql
-- Seeds the achievements table with initial achievements

-- Achievement: Reach 100 XP
INSERT INTO achievements (id, code, title, description, icon_url, type)
VALUES (
    'a1b2c3d4-e5f6-7890-abcd-100000000001',
    'XP_100',
    '100 XP',
    'Erreiche 100 XP',
    NULL,
    'XP_MILESTONE'
) ON CONFLICT (code) DO NOTHING;

-- Achievement: Reach 600 XP
INSERT INTO achievements (id, code, title, description, icon_url, type)
VALUES (
    'a1b2c3d4-e5f6-7890-abcd-600000000002',
    'XP_600',
    '600 XP',
    'Erreiche 600 XP',
    NULL,
    'XP_MILESTONE'
) ON CONFLICT (code) DO NOTHING;

-- Achievement: 7 Day Streak
INSERT INTO achievements (id, code, title, description, icon_url, type)
VALUES (
    'a1b2c3d4-e5f6-7890-abcd-700000000003',
    'STREAK_7',
    '7-Tage-Streak',
    'Lerne 7 Tage hintereinander',
    NULL,
    'STREAK'
) ON CONFLICT (code) DO NOTHING;
