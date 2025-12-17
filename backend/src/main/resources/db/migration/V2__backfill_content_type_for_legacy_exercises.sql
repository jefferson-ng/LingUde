-- Backfill content_type for legacy seeded exercises (EN + DE)
-- Only touches rows where content_type is NULL

-- 1) MCQ
UPDATE exercise_mcq
SET content_type = CASE
    -- EN legacy (one per level)
                       WHEN target_language = 'EN' AND difficulty_level = 'A1' AND question_text = 'Hello! How ___ you?' THEN 'GRAMMAR'
                       WHEN target_language = 'EN' AND difficulty_level = 'A2' AND question_text = 'I ___ breakfast at 7 AM every day.' THEN 'GRAMMAR'
                       WHEN target_language = 'EN' AND difficulty_level = 'B1' AND question_text = 'Last summer, I ___ to Paris for vacation.' THEN 'GRAMMAR'
                       WHEN target_language = 'EN' AND difficulty_level = 'B2' AND question_text = 'In my opinion, technology has ___ our lives significantly.' THEN 'GRAMMAR'
                       WHEN target_language = 'EN' AND difficulty_level = 'C1' AND question_text = 'The research findings ___ that climate change is accelerating.' THEN 'GRAMMAR'
                       WHEN target_language = 'EN' AND difficulty_level = 'C2' AND question_text = 'After months of preparation, the project finally came to ___.' THEN 'VOCABULARY'

    -- DE legacy (translated, same content type)
                       WHEN target_language = 'DE' AND difficulty_level = 'A1' AND question_text = 'Guten Tag! Wie ___ es Ihnen?' THEN 'GRAMMAR'
                       WHEN target_language = 'DE' AND difficulty_level = 'A2' AND question_text = 'Ich ___ jeden Tag um 7 Uhr Frühstück.' THEN 'GRAMMAR'
                       WHEN target_language = 'DE' AND difficulty_level = 'B1' AND question_text = 'Letzten Sommer ___ ich nach Paris in den Urlaub.' THEN 'GRAMMAR'
                       WHEN target_language = 'DE' AND difficulty_level = 'B2' AND question_text = 'Meiner Meinung nach hat die Technologie unser Leben erheblich ___.' THEN 'GRAMMAR'
                       WHEN target_language = 'DE' AND difficulty_level = 'C1' AND question_text = 'Die Forschungsergebnisse ___ darauf hin, dass sich der Klimawandel beschleunigt.' THEN 'GRAMMAR'
                       WHEN target_language = 'DE' AND difficulty_level = 'C2' AND question_text = 'Nach monatelanger Vorbereitung kam das Projekt endlich zur ___.' THEN 'VOCABULARY'

                       ELSE content_type
    END
WHERE content_type IS NULL;

-- 2) Fill-in-the-Blank
UPDATE exercise_fill_blank
SET content_type = CASE
    -- EN legacy (one per level)
                       WHEN target_language = 'EN' AND difficulty_level = 'A1' AND sentence_with_blank = 'My name ___ Sarah.' THEN 'GRAMMAR'
                       WHEN target_language = 'EN' AND difficulty_level = 'A2' AND sentence_with_blank = 'The weather today is very ___.' THEN 'VOCABULARY'
                       WHEN target_language = 'EN' AND difficulty_level = 'B1' AND sentence_with_blank = 'I have ___ visited that museum before.' THEN 'GRAMMAR'
                       WHEN target_language = 'EN' AND difficulty_level = 'B2' AND sentence_with_blank = 'Success requires both talent and ___.' THEN 'VOCABULARY'
                       WHEN target_language = 'EN' AND difficulty_level = 'C1' AND sentence_with_blank = 'Her argument was ___ despite the opposing evidence.' THEN 'VOCABULARY'
                       WHEN target_language = 'EN' AND difficulty_level = 'C2' AND sentence_with_blank = 'The author''s prose was characterized by its ___ eloquence.' THEN 'VOCABULARY'

    -- DE legacy (translated, same content type)
                       WHEN target_language = 'DE' AND difficulty_level = 'A1' AND sentence_with_blank = 'Mein Name ___ Sarah.' THEN 'GRAMMAR'
                       WHEN target_language = 'DE' AND difficulty_level = 'A2' AND sentence_with_blank = 'Das Wetter heute ist sehr ___.' THEN 'VOCABULARY'
                       WHEN target_language = 'DE' AND difficulty_level = 'B1' AND sentence_with_blank = 'Ich habe dieses Museum noch ___ besucht.' THEN 'GRAMMAR'
                       WHEN target_language = 'DE' AND difficulty_level = 'B2' AND sentence_with_blank = 'Erfolg erfordert sowohl Talent als auch ___.' THEN 'VOCABULARY'
                       WHEN target_language = 'DE' AND difficulty_level = 'C1' AND sentence_with_blank = 'Ihr Argument war trotz der gegenteiligen Beweise ___.' THEN 'VOCABULARY'
                       WHEN target_language = 'DE' AND difficulty_level = 'C2' AND sentence_with_blank = 'Die Prosa des Autors zeichnete sich durch ihre ___ Eloquenz aus.' THEN 'VOCABULARY'

                       ELSE content_type
    END
WHERE content_type IS NULL;
