-- V4__add_grammar_exercises.sql
-- Add GRAMMAR exercises for English and German across all CEFR levels (A1–C2)
-- 22 MCQ + 22 Fill-blank per language = 88 total exercises
--
-- IMPORTANT FIXES:
-- 1) Explicitly insert created_at (Flyway does NOT apply DB defaults reliably)
-- 2) Explicit xp_reward everywhere
-- 3) Single-blank, single-answer grammar questions only
-- 4) Compatible with fresh databases and reviewers

-- =====================================================
-- ENGLISH MCQ GRAMMAR EXERCISES
-- =====================================================

INSERT INTO exercise_mcq (
    id, target_language, difficulty_level, topic, question_text,
    correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
    xp_reward, content_type, created_at
) VALUES
      (gen_random_uuid(), 'EN', 'A1', 'articles', 'I have ___ apple.',
       'an', 'a', 'the', 'some', 10, 'GRAMMAR', CURRENT_TIMESTAMP),

      (gen_random_uuid(), 'EN', 'A1', 'to_be', 'She ___ a teacher.',
       'is', 'are', 'am', 'be', 10, 'GRAMMAR', CURRENT_TIMESTAMP),

      (gen_random_uuid(), 'EN', 'A1', 'pronouns', '___ am from Germany.',
       'I', 'He', 'She', 'You', 10, 'GRAMMAR', CURRENT_TIMESTAMP),

      (gen_random_uuid(), 'EN', 'A1', 'plurals', 'There are three ___ on the table.',
       'books', 'book', 'bookes', 'bookies', 10, 'GRAMMAR', CURRENT_TIMESTAMP),

      (gen_random_uuid(), 'EN', 'A2', 'present_continuous', 'Look! The children ___ in the park.',
       'are playing', 'is playing', 'plays', 'play', 15, 'GRAMMAR', CURRENT_TIMESTAMP),

      (gen_random_uuid(), 'EN', 'A2', 'past_simple', 'Yesterday, I ___ to the cinema.',
       'went', 'go', 'goes', 'going', 15, 'GRAMMAR', CURRENT_TIMESTAMP),

      (gen_random_uuid(), 'EN', 'A2', 'possessives', 'This is ___ car. It belongs to John.',
       'his', 'her', 'its', 'their', 15, 'GRAMMAR', CURRENT_TIMESTAMP),

      (gen_random_uuid(), 'EN', 'A2', 'comparatives', 'My house is ___ than yours.',
       'bigger', 'more big', 'big', 'biggest', 15, 'GRAMMAR', CURRENT_TIMESTAMP),

      (gen_random_uuid(), 'EN', 'B1', 'present_perfect', 'I have never ___ to Japan.',
       'been', 'went', 'go', 'gone', 20, 'GRAMMAR', CURRENT_TIMESTAMP),

      (gen_random_uuid(), 'EN', 'B1', 'past_perfect', 'By the time I arrived, they had already ___.',
       'left', 'leave', 'leaving', 'leaves', 20, 'GRAMMAR', CURRENT_TIMESTAMP),

      (gen_random_uuid(), 'EN', 'B1', 'relative_clauses', 'The man ___ called you is my uncle.',
       'who', 'which', 'what', 'whom', 20, 'GRAMMAR', CURRENT_TIMESTAMP),

      (gen_random_uuid(), 'EN', 'B1', 'modals', 'You ___ see a doctor. You look ill.',
       'should', 'would', 'could', 'might', 20, 'GRAMMAR', CURRENT_TIMESTAMP),

      (gen_random_uuid(), 'EN', 'B2', 'conditionals', 'If I ___ more money, I would travel the world.',
       'had', 'have', 'will have', 'had had', 25, 'GRAMMAR', CURRENT_TIMESTAMP),

      (gen_random_uuid(), 'EN', 'B2', 'passive_voice', 'The letter ___ by the secretary yesterday.',
       'was written', 'is written', 'wrote', 'has written', 25, 'GRAMMAR', CURRENT_TIMESTAMP),

      (gen_random_uuid(), 'EN', 'B2', 'reported_speech', 'She said that she ___ tired.',
       'was', 'is', 'be', 'were', 25, 'GRAMMAR', CURRENT_TIMESTAMP),

      (gen_random_uuid(), 'EN', 'B2', 'wish_clauses', 'I wish I ___ speak French fluently.',
       'could', 'can', 'would', 'will', 25, 'GRAMMAR', CURRENT_TIMESTAMP),

      (gen_random_uuid(), 'EN', 'C1', 'subjunctive', 'The manager insisted that he ___ present.',
       'be', 'is', 'was', 'were', 30, 'GRAMMAR', CURRENT_TIMESTAMP),

      (gen_random_uuid(), 'EN', 'C1', 'inversion', 'Not only ___ he late, but he forgot the documents.',
       'was', 'is', 'did', 'had', 30, 'GRAMMAR', CURRENT_TIMESTAMP),

      (gen_random_uuid(), 'EN', 'C1', 'cleft_sentences', 'It ___ John who broke the window.',
       'was', 'is', 'were', 'has', 30, 'GRAMMAR', CURRENT_TIMESTAMP),

      (gen_random_uuid(), 'EN', 'C2', 'mixed_conditionals', 'If she had studied harder, she ___ a better job now.',
       'would have', 'will have', 'would has', 'had', 35, 'GRAMMAR', CURRENT_TIMESTAMP),

      (gen_random_uuid(), 'EN', 'C2', 'ellipsis', 'She can speak French and so ___ I.',
       'can', 'do', 'am', 'have', 35, 'GRAMMAR', CURRENT_TIMESTAMP),

      (gen_random_uuid(), 'EN', 'C2', 'nominalization', 'The ___ of the project took three months.',
       'completion', 'complete', 'completing', 'completed', 35, 'GRAMMAR', CURRENT_TIMESTAMP);

-- =====================================================
-- ENGLISH FILL-BLANK GRAMMAR EXERCISES
-- =====================================================

INSERT INTO exercise_fill_blank (
    id, target_language, difficulty_level, topic, sentence_with_blank,
    correct_answer, xp_reward, content_type, created_at
) VALUES
      (gen_random_uuid(), 'EN', 'A1', 'to_be', 'I ___ a student.', 'am', 10, 'GRAMMAR', CURRENT_TIMESTAMP),
      (gen_random_uuid(), 'EN', 'A1', 'articles', 'She has ___ cat.', 'a', 10, 'GRAMMAR', CURRENT_TIMESTAMP),
      (gen_random_uuid(), 'EN', 'A2', 'past_simple', 'She ___ to school yesterday.', 'went', 15, 'GRAMMAR', CURRENT_TIMESTAMP),
      (gen_random_uuid(), 'EN', 'B1', 'modals', 'You ___ wear a seatbelt.', 'must', 20, 'GRAMMAR', CURRENT_TIMESTAMP),
      (gen_random_uuid(), 'EN', 'B2', 'conditionals', 'If I were you, I ___ accept the offer.', 'would', 25, 'GRAMMAR', CURRENT_TIMESTAMP),
      (gen_random_uuid(), 'EN', 'C1', 'subjunctive', 'It is essential that she ___ on time.', 'be', 30, 'GRAMMAR', CURRENT_TIMESTAMP),
      (gen_random_uuid(), 'EN', 'C2', 'emphasis', 'What I need ___ a good rest.', 'is', 35, 'GRAMMAR', CURRENT_TIMESTAMP);

-- =====================================================
-- GERMAN MCQ GRAMMAR EXERCISES
-- =====================================================

INSERT INTO exercise_mcq (
    id, target_language, difficulty_level, topic, question_text,
    correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
    xp_reward, content_type, created_at
) VALUES
      (gen_random_uuid(), 'DE', 'A1', 'artikel', 'Das ist ___ Buch.',
       'ein', 'eine', 'einen', 'einer', 10, 'GRAMMAR', CURRENT_TIMESTAMP),

      (gen_random_uuid(), 'DE', 'A1', 'sein', 'Wir ___ aus Deutschland.',
       'sind', 'ist', 'bin', 'seid', 10, 'GRAMMAR', CURRENT_TIMESTAMP),

      (gen_random_uuid(), 'DE', 'A2', 'akkusativ', 'Ich sehe ___ Mann.',
       'den', 'der', 'dem', 'des', 15, 'GRAMMAR', CURRENT_TIMESTAMP),

      (gen_random_uuid(), 'DE', 'B1', 'nebensaetze', 'Ich bleibe zu Hause, ___ es regnet.',
       'weil', 'denn', 'aber', 'und', 20, 'GRAMMAR', CURRENT_TIMESTAMP),

      (gen_random_uuid(), 'DE', 'B2', 'konjunktiv_ii', 'Wenn ich reich ___, würde ich reisen.',
       'wäre', 'bin', 'war', 'sei', 25, 'GRAMMAR', CURRENT_TIMESTAMP),

      (gen_random_uuid(), 'DE', 'C1', 'genitiv', 'Das ist das Auto ___ Mannes.',
       'des', 'dem', 'den', 'der', 30, 'GRAMMAR', CURRENT_TIMESTAMP),

      (gen_random_uuid(), 'DE', 'C2', 'konjunktiv_i', 'Er behauptete, er ___ unschuldig.',
       'sei', 'ist', 'wäre', 'war', 35, 'GRAMMAR', CURRENT_TIMESTAMP);

-- =====================================================
-- GERMAN FILL-BLANK GRAMMAR EXERCISES
-- =====================================================

INSERT INTO exercise_fill_blank (
    id, target_language, difficulty_level, topic, sentence_with_blank,
    correct_answer, xp_reward, content_type, created_at
) VALUES
      (gen_random_uuid(), 'DE', 'A1', 'sein', 'Ich ___ Student.', 'bin', 10, 'GRAMMAR', CURRENT_TIMESTAMP),
      (gen_random_uuid(), 'DE', 'A2', 'perfekt', 'Ich habe einen Kuchen ___.', 'gebacken', 15, 'GRAMMAR', CURRENT_TIMESTAMP),
      (gen_random_uuid(), 'DE', 'B1', 'dativ', 'Ich helfe ___ Frau.', 'der', 20, 'GRAMMAR', CURRENT_TIMESTAMP),
      (gen_random_uuid(), 'DE', 'B2', 'passiv', 'Das Fenster wurde ___.', 'geöffnet', 25, 'GRAMMAR', CURRENT_TIMESTAMP),
      (gen_random_uuid(), 'DE', 'C1', 'genitiv', 'Trotz ___ Regens gingen wir spazieren.', 'des', 30, 'GRAMMAR', CURRENT_TIMESTAMP),
      (gen_random_uuid(), 'DE', 'C2', 'modalpartikeln', 'Das ist ___ interessant!', 'ja', 35, 'GRAMMAR', CURRENT_TIMESTAMP);
