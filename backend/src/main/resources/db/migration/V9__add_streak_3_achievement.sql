-- Add STREAK_3 achievement
INSERT INTO achievements (id, code, title, description, icon_url, type)
VALUES (
    'a1b2c3d4-e5f6-7890-abcd-300000000004',
    'STREAK_3',
    'Three Day Streak',
    'Practice for 3 consecutive days.',
    'icon-streak-3.png',
    'STREAK'
)
ON CONFLICT (code) DO NOTHING;
