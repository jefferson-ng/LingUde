-- ============================================================================
-- EXERCISE SEED DATA - English & German Language Learning
-- ============================================================================
-- 24 Exercises: 12 English + 12 German (6 MCQ + 6 Fill-Blank per language)
-- Languages: English (EN), German (DE)
-- Total XP Available: 540 (270 per language)
-- ============================================================================

-- ============================================================================
-- ENGLISH EXERCISES - MULTIPLE CHOICE QUESTIONS (MCQ)
-- ============================================================================

-- A1 Level - Basic Greetings
INSERT INTO exercise_mcq (
    id, 
    target_language, 
    difficulty_level, 
    topic, 
    xp_reward, 
    question_text, 
    correct_answer, 
    wrong_option_1, 
    wrong_option_2, 
    wrong_option_3, 
    created_at
) VALUES (
    gen_random_uuid(),
    'EN',
    'A1',
    'greetings',
    10,
    'Hello! How ___ you?',
    'are',
    'is',
    'am',
    'be',
    CURRENT_TIMESTAMP
);

-- A2 Level - Daily Routines
INSERT INTO exercise_mcq (
    id, 
    target_language, 
    difficulty_level, 
    topic, 
    xp_reward, 
    question_text, 
    correct_answer, 
    wrong_option_1, 
    wrong_option_2, 
    wrong_option_3, 
    created_at
) VALUES (
    gen_random_uuid(),
    'EN',
    'A2',
    'daily_routines',
    15,
    'I ___ breakfast at 7 AM every day.',
    'eat',
    'eats',
    'eating',
    'ate',
    CURRENT_TIMESTAMP
);

-- B1 Level - Travel
INSERT INTO exercise_mcq (
    id, 
    target_language, 
    difficulty_level, 
    topic, 
    xp_reward, 
    question_text, 
    correct_answer, 
    wrong_option_1, 
    wrong_option_2, 
    wrong_option_3, 
    created_at
) VALUES (
    gen_random_uuid(),
    'EN',
    'B1',
    'travel',
    20,
    'Last summer, I ___ to Paris for vacation.',
    'went',
    'go',
    'going',
    'gone',
    CURRENT_TIMESTAMP
);

-- B2 Level - Opinions
INSERT INTO exercise_mcq (
    id, 
    target_language, 
    difficulty_level, 
    topic, 
    xp_reward, 
    question_text, 
    correct_answer, 
    wrong_option_1, 
    wrong_option_2, 
    wrong_option_3, 
    created_at
) VALUES (
    gen_random_uuid(),
    'EN',
    'B2',
    'opinions',
    25,
    'In my opinion, technology has ___ our lives significantly.',
    'improved',
    'improving',
    'improves',
    'improve',
    CURRENT_TIMESTAMP
);

-- C1 Level - Complex Ideas
INSERT INTO exercise_mcq (
    id, 
    target_language, 
    difficulty_level, 
    topic, 
    xp_reward, 
    question_text, 
    correct_answer, 
    wrong_option_1, 
    wrong_option_2, 
    wrong_option_3, 
    created_at
) VALUES (
    gen_random_uuid(),
    'EN',
    'C1',
    'complex_ideas',
    30,
    'The research findings ___ that climate change is accelerating.',
    'indicate',
    'indicates',
    'indicating',
    'indicated',
    CURRENT_TIMESTAMP
);

-- C2 Level - Idiomatic Expressions
INSERT INTO exercise_mcq (
    id, 
    target_language, 
    difficulty_level, 
    topic, 
    xp_reward, 
    question_text, 
    correct_answer, 
    wrong_option_1, 
    wrong_option_2, 
    wrong_option_3, 
    created_at
) VALUES (
    gen_random_uuid(),
    'EN',
    'C2',
    'idioms',
    35,
    'After months of preparation, the project finally came to ___.',
    'fruition',
    'completion',
    'ending',
    'conclusion',
    CURRENT_TIMESTAMP
);

-- ============================================================================
-- ENGLISH EXERCISES - FILL IN THE BLANK
-- ============================================================================

-- A1 Level - Introductions
INSERT INTO exercise_fill_blank (
    id, 
    target_language, 
    difficulty_level, 
    topic, 
    xp_reward, 
    sentence_with_blank, 
    correct_answer, 
    created_at
) VALUES (
    gen_random_uuid(),
    'EN',
    'A1',
    'introductions',
    10,
    'My name ___ Sarah.',
    'is',
    CURRENT_TIMESTAMP
);

-- A2 Level - Descriptions
INSERT INTO exercise_fill_blank (
    id, 
    target_language, 
    difficulty_level, 
    topic, 
    xp_reward, 
    sentence_with_blank, 
    correct_answer, 
    created_at
) VALUES (
    gen_random_uuid(),
    'EN',
    'A2',
    'descriptions',
    15,
    'The weather today is very ___.',
    'nice',
    CURRENT_TIMESTAMP
);

-- B1 Level - Past Experiences
INSERT INTO exercise_fill_blank (
    id, 
    target_language, 
    difficulty_level, 
    topic, 
    xp_reward, 
    sentence_with_blank, 
    correct_answer, 
    created_at
) VALUES (
    gen_random_uuid(),
    'EN',
    'B1',
    'experiences',
    20,
    'I have ___ visited that museum before.',
    'never',
    CURRENT_TIMESTAMP
);

-- B2 Level - Abstract Concepts
INSERT INTO exercise_fill_blank (
    id, 
    target_language, 
    difficulty_level, 
    topic, 
    xp_reward, 
    sentence_with_blank, 
    correct_answer, 
    created_at
) VALUES (
    gen_random_uuid(),
    'EN',
    'B2',
    'abstract_concepts',
    25,
    'Success requires both talent and ___.',
    'perseverance',
    CURRENT_TIMESTAMP
);

-- C1 Level - Nuanced Language
INSERT INTO exercise_fill_blank (
    id, 
    target_language, 
    difficulty_level, 
    topic, 
    xp_reward, 
    sentence_with_blank, 
    correct_answer, 
    created_at
) VALUES (
    gen_random_uuid(),
    'EN',
    'C1',
    'nuanced_language',
    30,
    'Her argument was ___ despite the opposing evidence.',
    'compelling',
    CURRENT_TIMESTAMP
);

-- C2 Level - Sophisticated Language
INSERT INTO exercise_fill_blank (
    id, 
    target_language, 
    difficulty_level, 
    topic, 
    xp_reward, 
    sentence_with_blank, 
    correct_answer, 
    created_at
) VALUES (
    gen_random_uuid(),
    'EN',
    'C2',
    'sophisticated_language',
    35,
    'The author''s prose was characterized by its ___ eloquence.',
    'unparalleled',
    CURRENT_TIMESTAMP
);

-- ============================================================================
-- GERMAN EXERCISES - MULTIPLE CHOICE QUESTIONS (MCQ)
-- ============================================================================

-- A1 Level - GrüÃŸe (Greetings)
INSERT INTO exercise_mcq (
    id, 
    target_language, 
    difficulty_level, 
    topic, 
    xp_reward, 
    question_text, 
    correct_answer, 
    wrong_option_1, 
    wrong_option_2, 
    wrong_option_3, 
    created_at
) VALUES (
    gen_random_uuid(),
    'DE',
    'A1',
    'greetings',
    10,
    'Guten Tag! Wie ___ es Ihnen?',
    'geht',
    'gehen',
    'gehst',
    'ging',
    CURRENT_TIMESTAMP
);

-- A2 Level - Alltag (Daily Routines)
INSERT INTO exercise_mcq (
    id, 
    target_language, 
    difficulty_level, 
    topic, 
    xp_reward, 
    question_text, 
    correct_answer, 
    wrong_option_1, 
    wrong_option_2, 
    wrong_option_3, 
    created_at
) VALUES (
    gen_random_uuid(),
    'DE',
    'A2',
    'daily_routines',
    15,
    'Ich ___ jeden Tag um 7 Uhr FrÃ¼hstÃ¼ck.',
    'esse',
    'isst',
    'essen',
    'aß',
    CURRENT_TIMESTAMP
);

-- B1 Level - Reisen (Travel)
INSERT INTO exercise_mcq (
    id, 
    target_language, 
    difficulty_level, 
    topic, 
    xp_reward, 
    question_text, 
    correct_answer, 
    wrong_option_1, 
    wrong_option_2, 
    wrong_option_3, 
    created_at
) VALUES (
    gen_random_uuid(),
    'DE',
    'B1',
    'travel',
    20,
    'Letzten Sommer ___ ich nach Paris in den Urlaub.',
    'fuhr',
    'fahre',
    'fahren',
    'gefahren',
    CURRENT_TIMESTAMP
);

-- B2 Level - Meinungen (Opinions)
INSERT INTO exercise_mcq (
    id, 
    target_language, 
    difficulty_level, 
    topic, 
    xp_reward, 
    question_text, 
    correct_answer, 
    wrong_option_1, 
    wrong_option_2, 
    wrong_option_3, 
    created_at
) VALUES (
    gen_random_uuid(),
    'DE',
    'B2',
    'opinions',
    25,
    'Meiner Meinung nach hat die Technologie unser Leben erheblich ___.',
    'verbessert',
    'verbessern',
    'verbessernd',
    'verbesserte',
    CURRENT_TIMESTAMP
);

-- C1 Level - Komplexe Ideen (Complex Ideas)
INSERT INTO exercise_mcq (
    id, 
    target_language, 
    difficulty_level, 
    topic, 
    xp_reward, 
    question_text, 
    correct_answer, 
    wrong_option_1, 
    wrong_option_2, 
    wrong_option_3, 
    created_at
) VALUES (
    gen_random_uuid(),
    'DE',
    'C1',
    'complex_ideas',
    30,
    'Die Forschungsergebnisse ___ darauf hin, dass sich der Klimawandel beschleunigt.',
    'deuten',
    'deutet',
    'deutend',
    'gedeutet',
    CURRENT_TIMESTAMP
);

-- C2 Level - Redewendungen (Idiomatic Expressions)
INSERT INTO exercise_mcq (
    id, 
    target_language, 
    difficulty_level, 
    topic, 
    xp_reward, 
    question_text, 
    correct_answer, 
    wrong_option_1, 
    wrong_option_2, 
    wrong_option_3, 
    created_at
) VALUES (
    gen_random_uuid(),
    'DE',
    'C2',
    'idioms',
    35,
    'Nach monatelanger Vorbereitung kam das Projekt endlich zur ___.',
    'Vollendung',
    'Beendigung',
    'Abschluss',
    'Fertigstellung',
    CURRENT_TIMESTAMP
);

-- ============================================================================
-- GERMAN EXERCISES - FILL IN THE BLANK
-- ============================================================================

-- A1 Level - Vorstellungen (Introductions)
INSERT INTO exercise_fill_blank (
    id, 
    target_language, 
    difficulty_level, 
    topic, 
    xp_reward, 
    sentence_with_blank, 
    correct_answer, 
    created_at
) VALUES (
    gen_random_uuid(),
    'DE',
    'A1',
    'introductions',
    10,
    'Mein Name ___ Sarah.',
    'ist',
    CURRENT_TIMESTAMP
);

-- A2 Level - Beschreibungen (Descriptions)
INSERT INTO exercise_fill_blank (
    id, 
    target_language, 
    difficulty_level, 
    topic, 
    xp_reward, 
    sentence_with_blank, 
    correct_answer, 
    created_at
) VALUES (
    gen_random_uuid(),
    'DE',
    'A2',
    'descriptions',
    15,
    'Das Wetter heute ist sehr ___.',
    'schön',
    CURRENT_TIMESTAMP
);

-- B1 Level - Erfahrungen (Experiences)
INSERT INTO exercise_fill_blank (
    id, 
    target_language, 
    difficulty_level, 
    topic, 
    xp_reward, 
    sentence_with_blank, 
    correct_answer, 
    created_at
) VALUES (
    gen_random_uuid(),
    'DE',
    'B1',
    'experiences',
    20,
    'Ich habe dieses Museum noch ___ besucht.',
    'nie',
    CURRENT_TIMESTAMP
);

-- B2 Level - Abstrakte Konzepte (Abstract Concepts)
INSERT INTO exercise_fill_blank (
    id, 
    target_language, 
    difficulty_level, 
    topic, 
    xp_reward, 
    sentence_with_blank, 
    correct_answer, 
    created_at
) VALUES (
    gen_random_uuid(),
    'DE',
    'B2',
    'abstract_concepts',
    25,
    'Erfolg erfordert sowohl Talent als auch ___.',
    'Ausdauer',
    CURRENT_TIMESTAMP
);

-- C1 Level - Nuancierte Sprache (Nuanced Language)
INSERT INTO exercise_fill_blank (
    id, 
    target_language, 
    difficulty_level, 
    topic, 
    xp_reward, 
    sentence_with_blank, 
    correct_answer, 
    created_at
) VALUES (
    gen_random_uuid(),
    'DE',
    'C1',
    'nuanced_language',
    30,
    'Ihr Argument war trotz der gegenteiligen Beweise ___.',
    'überzeugend',
    CURRENT_TIMESTAMP
);

-- C2 Level - Gehobene Sprache (Sophisticated Language)
INSERT INTO exercise_fill_blank (
    id, 
    target_language, 
    difficulty_level, 
    topic, 
    xp_reward, 
    sentence_with_blank, 
    correct_answer, 
    created_at
) VALUES (
    gen_random_uuid(),
    'DE',
    'C2',
    'sophisticated_language',
    35,
    'Die Prosa des Autors zeichnete sich durch ihre ___ Eloquenz aus.',
    'unvergleichliche',
    CURRENT_TIMESTAMP
);

-- ============================================================================
-- VERIFICATION QUERIES
-- ============================================================================
-- Run these after insertion to verify data:

-- Count exercises by type and language
-- SELECT target_language, 'MCQ' as type, COUNT(*) as count 
-- FROM exercise_mcq 
-- GROUP BY target_language
-- UNION ALL
-- SELECT target_language, 'Fill-Blank' as type, COUNT(*) as count 
-- FROM exercise_fill_blank 
-- GROUP BY target_language
-- ORDER BY target_language, type;

-- Count exercises by difficulty level and language
-- SELECT target_language, difficulty_level, COUNT(*) as count 
-- FROM (
--     SELECT target_language, difficulty_level FROM exercise_mcq
--     UNION ALL
--     SELECT target_language, difficulty_level FROM exercise_fill_blank
-- ) as all_exercises
-- GROUP BY target_language, difficulty_level
-- ORDER BY target_language, difficulty_level;

-- Total XP available per language
-- SELECT target_language, SUM(xp_reward) as total_xp
-- FROM (
--     SELECT target_language, xp_reward FROM exercise_mcq
--     UNION ALL
--     SELECT target_language, xp_reward FROM exercise_fill_blank
-- ) as all_exercises
-- GROUP BY target_language
-- ORDER BY target_language;

-- Grand total
-- SELECT COUNT(*) as total_exercises, SUM(xp_reward) as total_xp
-- FROM (
--     SELECT xp_reward FROM exercise_mcq
--     UNION ALL
--     SELECT xp_reward FROM exercise_fill_blank
-- ) as all_exercises;
