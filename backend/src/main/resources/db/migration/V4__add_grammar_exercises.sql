-- V4__add_grammar_exercises.sql
-- Add GRAMMAR exercises for English and German across all CEFR levels (A1–C2)
-- 22 MCQ + 22 Fill-blank per language = 88 total exercises
--
-- Notes / fixes applied:
-- 1) Use correct MCQ column names: wrong_option_1 / wrong_option_2 / wrong_option_3
-- 2) Do NOT insert created_at (handled by DB/Hibernate strategy)
-- 3) Avoid “two blanks with comma answers” like 'have, been' -> use a single-blank question/answer
-- 4) Keep gen_random_uuid() for id (assumes pgcrypto is available, as in your earlier scripts)

-- =====================================================
-- ENGLISH MCQ GRAMMAR EXERCISES (22 total)
-- =====================================================

-- EN A1 MCQ (4)
INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'A1', 'articles', 'I have ___ apple.',
     'an', 'a', 'the', 'some', 10, 'GRAMMAR');

INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'A1', 'to_be', 'She ___ a teacher.',
     'is', 'are', 'am', 'be', 10, 'GRAMMAR');

INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'A1', 'pronouns', '___ am from Germany.',
     'I', 'He', 'She', 'You', 10, 'GRAMMAR');

INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'A1', 'plurals', 'There are three ___ on the table.',
     'books', 'book', 'bookes', 'bookies', 10, 'GRAMMAR');

-- EN A2 MCQ (4)
INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'A2', 'present_continuous', 'Look! The children ___ in the park.',
     'are playing', 'is playing', 'plays', 'play', 15, 'GRAMMAR');

INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'A2', 'past_simple', 'Yesterday, I ___ to the cinema.',
     'went', 'go', 'goes', 'going', 15, 'GRAMMAR');

INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'A2', 'possessives', 'This is ___ car. It belongs to John.',
     'his', 'her', 'its', 'their', 15, 'GRAMMAR');

INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'A2', 'comparatives', 'My house is ___ than yours.',
     'bigger', 'more big', 'big', 'biggest', 15, 'GRAMMAR');

-- EN B1 MCQ (4)
-- FIX: single blank, single answer (no "have, been")
INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'B1', 'present_perfect', 'I have never ___ to Japan.',
     'been', 'went', 'go', 'gone', 20, 'GRAMMAR');

-- FIX: single blank, single answer (no "had, left")
INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'B1', 'past_perfect', 'By the time I arrived, they had already ___.',
     'left', 'leave', 'leaving', 'leaves', 20, 'GRAMMAR');

INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'B1', 'relative_clauses', 'The man ___ called you is my uncle.',
     'who', 'which', 'what', 'whom', 20, 'GRAMMAR');

INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'B1', 'modals', 'You ___ see a doctor. You look ill.',
     'should', 'would', 'could', 'might', 20, 'GRAMMAR');

-- EN B2 MCQ (4)
-- FIX: keep it MCQ-friendly by making the option a single string
INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'B2', 'conditionals', 'If I ___ more money, I would travel the world.',
     'had', 'have', 'will have', 'had had', 25, 'GRAMMAR');

INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'B2', 'passive_voice', 'The letter ___ by the secretary yesterday.',
     'was written', 'is written', 'wrote', 'has written', 25, 'GRAMMAR');

INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'B2', 'reported_speech', 'She said that she ___ tired.',
     'was', 'is', 'be', 'were', 25, 'GRAMMAR');

INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'B2', 'wish_clauses', 'I wish I ___ speak French fluently.',
     'could', 'can', 'would', 'will', 25, 'GRAMMAR');

-- EN C1 MCQ (3)
INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'C1', 'subjunctive', 'The manager insisted that he ___ present at the meeting.',
     'be', 'is', 'was', 'were', 30, 'GRAMMAR');

INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'C1', 'inversion', 'Not only ___ he late, but he also forgot the documents.',
     'was', 'is', 'did', 'had', 30, 'GRAMMAR');

INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'C1', 'cleft_sentences', 'It ___ John who broke the window.',
     'was', 'is', 'were', 'has', 30, 'GRAMMAR');

-- EN C2 MCQ (3)
-- FIX: make it one blank and one correct answer
INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'C2', 'mixed_conditionals', 'If she had studied harder, she ___ a better job now.',
     'would have', 'will have', 'would has', 'had', 35, 'GRAMMAR');

INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'C2', 'ellipsis', 'She can speak French and so ___ I.',
     'can', 'do', 'am', 'have', 35, 'GRAMMAR');

INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'C2', 'nominalization', 'The ___ of the project took three months.',
     'completion', 'complete', 'completing', 'completed', 35, 'GRAMMAR');

-- =====================================================
-- ENGLISH FILL-BLANK GRAMMAR EXERCISES (22 total)
-- =====================================================

-- EN A1 Fill-blank (4)
INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'A1', 'to_be', 'I ___ a student.',
     'am', 10, 'GRAMMAR');

INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'A1', 'to_be', 'They ___ my friends.',
     'are', 10, 'GRAMMAR');

INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'A1', 'articles', 'She has ___ cat.',
     'a', 10, 'GRAMMAR');

INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'A1', 'pronouns', '___ is my brother Tom.',
     'This', 10, 'GRAMMAR');

-- EN A2 Fill-blank (4)
INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'A2', 'past_simple', 'She ___ to school yesterday.',
     'went', 15, 'GRAMMAR');

INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'A2', 'possessives', 'This book is mine. That book is ___.',
     'yours', 15, 'GRAMMAR');

INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'A2', 'present_continuous', 'She ___ cooking dinner right now.',
     'is', 15, 'GRAMMAR');

INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'A2', 'prepositions', 'The book is ___ the table.',
     'on', 15, 'GRAMMAR');

-- EN B1 Fill-blank (4)
INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'B1', 'present_perfect', 'I have ___ finished my homework.',
     'just', 20, 'GRAMMAR');

INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'B1', 'relative_clauses', 'The woman ___ lives next door is a doctor.',
     'who', 20, 'GRAMMAR');

INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'B1', 'modals', 'You ___ wear a seatbelt. It is the law.',
     'must', 20, 'GRAMMAR');

INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'B1', 'connectors', 'I stayed home ___ I was feeling sick.',
     'because', 20, 'GRAMMAR');

-- EN B2 Fill-blank (4)
INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'B2', 'passive_voice', 'The report ___ written by the team last week.',
     'was', 25, 'GRAMMAR');

INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'B2', 'conditionals', 'If I were you, I ___ accept the offer.',
     'would', 25, 'GRAMMAR');

INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'B2', 'reported_speech', 'He told me that he ___ busy the day before.',
     'had been', 25, 'GRAMMAR');

INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'B2', 'gerunds_infinitives', 'She suggested ___ to the beach.',
     'going', 25, 'GRAMMAR');

-- EN C1 Fill-blank (3)
INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'C1', 'inversion', 'Never ___ I seen such a beautiful sunset.',
     'have', 30, 'GRAMMAR');

INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'C1', 'participle_clauses', '___ finished the exam, she left the room.',
     'Having', 30, 'GRAMMAR');

INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'C1', 'subjunctive', 'It is essential that she ___ on time.',
     'be', 30, 'GRAMMAR');

-- EN C2 Fill-blank (3)
INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'C2', 'ellipsis', 'She wanted to help, but she could ___.',
     'not', 35, 'GRAMMAR');

INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'C2', 'fronting', 'Strange ___ it may seem, he was right.',
     'as', 35, 'GRAMMAR');

INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'EN', 'C2', 'emphasis', 'What I need ___ a good rest.',
     'is', 35, 'GRAMMAR');

-- =====================================================
-- GERMAN MCQ GRAMMAR EXERCISES (22 total)
-- =====================================================

-- DE A1 MCQ (4)
INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'A1', 'artikel', 'Das ist ___ Buch.',
     'ein', 'eine', 'einen', 'einer', 10, 'GRAMMAR');

INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'A1', 'verben', 'Ich ___ Deutsch.',
     'lerne', 'lernst', 'lernt', 'lernen', 10, 'GRAMMAR');

INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'A1', 'sein', 'Wir ___ aus Deutschland.',
     'sind', 'ist', 'bin', 'seid', 10, 'GRAMMAR');

INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'A1', 'haben', 'Er ___ einen Hund.',
     'hat', 'habe', 'hast', 'haben', 10, 'GRAMMAR');

-- DE A2 MCQ (4)
INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'A2', 'akkusativ', 'Ich sehe ___ Mann.',
     'den', 'der', 'dem', 'des', 15, 'GRAMMAR');

INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'A2', 'trennbare_verben', 'Ich ___ jeden Tag um 7 Uhr ___.',
     'stehe, auf', 'stehe, an', 'aufstehe, -', 'stehen, auf', 15, 'GRAMMAR');

INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'A2', 'perfekt', 'Ich ___ gestern ins Kino ___.',
     'bin, gegangen', 'habe, gegangen', 'bin, gehen', 'habe, geht', 15, 'GRAMMAR');

INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'A2', 'modalverben', 'Ich ___ Deutsch sprechen.',
     'kann', 'können', 'kannst', 'konnte', 15, 'GRAMMAR');

-- DE B1 MCQ (4)
INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'B1', 'dativ', 'Ich gebe ___ Frau das Geschenk.',
     'der', 'die', 'den', 'das', 20, 'GRAMMAR');

INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'B1', 'nebensaetze', 'Ich bleibe zu Hause, ___ es regnet.',
     'weil', 'denn', 'aber', 'und', 20, 'GRAMMAR');

INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'B1', 'relativsaetze', 'Der Mann, ___ dort steht, ist mein Vater.',
     'der', 'den', 'dem', 'dessen', 20, 'GRAMMAR');

INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'B1', 'praeteritum', 'Als ich jung ___, spielte ich oft Fußball.',
     'war', 'bin', 'sei', 'wäre', 20, 'GRAMMAR');

-- DE B2 MCQ (4)
INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'B2', 'konjunktiv_ii', 'Wenn ich reich ___, würde ich reisen.',
     'wäre', 'bin', 'war', 'sei', 25, 'GRAMMAR');

INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'B2', 'passiv', 'Das Haus ___ letztes Jahr gebaut.',
     'wurde', 'wird', 'war', 'worden', 25, 'GRAMMAR');

INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'B2', 'indirekte_rede', 'Er sagte, er ___ keine Zeit.',
     'habe', 'hat', 'hätte', 'haben', 25, 'GRAMMAR');

INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'B2', 'infinitiv_mit_zu', 'Es ist wichtig, pünktlich ___ sein.',
     'zu', 'um', 'für', '-', 25, 'GRAMMAR');

-- DE C1 MCQ (3)
INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'C1', 'genitiv', 'Das ist das Auto ___ Mannes.',
     'des', 'dem', 'den', 'der', 30, 'GRAMMAR');

INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'C1', 'partizipial', 'Die ___ Arbeit wurde anerkannt.',
     'geleistete', 'leistende', 'geleistet', 'leisten', 30, 'GRAMMAR');

INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'C1', 'nominalisierung', 'Das ___ der Aufgabe dauerte lange.',
     'Lösen', 'Lösung', 'Gelöst', 'Lösend', 30, 'GRAMMAR');

-- DE C2 MCQ (3)
INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'C2', 'konjunktiv_i', 'Er behauptete, er ___ unschuldig.',
     'sei', 'ist', 'wäre', 'war', 35, 'GRAMMAR');

INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'C2', 'erweitertes_partizip', 'Der ___ Mann ist mein Kollege.',
     'dort stehende', 'dort steht', 'dort gestanden', 'dort stehen', 35, 'GRAMMAR');

INSERT INTO exercise_mcq (id, target_language, difficulty_level, topic, question_text,
                          correct_answer, wrong_option_1, wrong_option_2, wrong_option_3,
                          xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'C2', 'nomen_verb_verbindungen', 'Er hat eine Entscheidung ___.',
     'getroffen', 'gemacht', 'genommen', 'gegeben', 35, 'GRAMMAR');

-- =====================================================
-- GERMAN FILL-BLANK GRAMMAR EXERCISES (22 total)
-- =====================================================

-- DE A1 Fill-blank (4)
INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'A1', 'sein', 'Ich ___ Student.',
     'bin', 10, 'GRAMMAR');

-- FIX: "Sie" (formal you) would be "haben". To keep it A1-simple and unambiguous:
INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'A1', 'haben', 'Er ___ zwei Kinder.',
     'hat', 10, 'GRAMMAR');

INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'A1', 'artikel', '___ Apfel ist rot.',
     'Der', 10, 'GRAMMAR');

INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'A1', 'verben', 'Du ___ sehr gut Deutsch.',
     'sprichst', 10, 'GRAMMAR');

-- DE A2 Fill-blank (4)
INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'A2', 'akkusativ', 'Ich kaufe ___ Buch.',
     'ein', 15, 'GRAMMAR');

INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'A2', 'trennbare_verben', 'Ich stehe jeden Morgen um 7 Uhr ___.',
     'auf', 15, 'GRAMMAR');

INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'A2', 'perfekt', 'Ich habe einen Kuchen ___.',
     'gebacken', 15, 'GRAMMAR');

INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'A2', 'praepositionen', 'Das Buch liegt ___ dem Tisch.',
     'auf', 15, 'GRAMMAR');

-- DE B1 Fill-blank (4)
INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'B1', 'dativ', 'Ich helfe ___ Frau.',
     'der', 20, 'GRAMMAR');

INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'B1', 'nebensaetze', 'Ich weiß nicht, ___ er kommt.',
     'ob', 20, 'GRAMMAR');

INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'B1', 'reflexive_verben', 'Ich freue ___ auf den Urlaub.',
     'mich', 20, 'GRAMMAR');

INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'B1', 'komparativ', 'Berlin ist ___ als München.',
     'größer', 20, 'GRAMMAR');

-- DE B2 Fill-blank (4)
INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'B2', 'konjunktiv_ii', 'Wenn ich Zeit ___, würde ich kommen.',
     'hätte', 25, 'GRAMMAR');

INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'B2', 'passiv', 'Das Fenster wurde von mir ___.',
     'geöffnet', 25, 'GRAMMAR');

INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'B2', 'plusquamperfekt', 'Nachdem ich gegessen ___, ging ich spazieren.',
     'hatte', 25, 'GRAMMAR');

INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'B2', 'konnektoren', 'Er kam nicht, ___ er krank war.',
     'obwohl', 25, 'GRAMMAR');

-- DE C1 Fill-blank (3)
INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'C1', 'genitiv', 'Trotz ___ Regens gingen wir spazieren.',
     'des', 30, 'GRAMMAR');

INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'C1', 'partizipial', 'Die ___ Aufgabe war sehr schwierig.',
     'gestellte', 30, 'GRAMMAR');

INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'C1', 'futur_ii', 'Bis morgen werde ich die Arbeit ___ haben.',
     'beendet', 30, 'GRAMMAR');

-- DE C2 Fill-blank (3)
INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'C2', 'konjunktiv_i', 'Er sagt, er ___ krank.',
     'sei', 35, 'GRAMMAR');

INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'C2', 'nomen_verb', 'Sie hat großen Einfluss auf ihn ___.',
     'ausgeübt', 35, 'GRAMMAR');

INSERT INTO exercise_fill_blank (id, target_language, difficulty_level, topic, sentence_with_blank,
                                 correct_answer, xp_reward, content_type)
VALUES
    (gen_random_uuid(), 'DE', 'C2', 'modalpartikeln', 'Das ist ___ interessant!',
     'ja', 35, 'GRAMMAR');
