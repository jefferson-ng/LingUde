package com.sep.sep_backend.exercise.config;

import com.sep.sep_backend.exercise.entity.ExerciseContentType;
import com.sep.sep_backend.exercise.entity.ExerciseFillBlank;
import com.sep.sep_backend.exercise.entity.ExerciseMcq;
import com.sep.sep_backend.exercise.repository.ExerciseFillBlankRepository;
import com.sep.sep_backend.exercise.repository.ExerciseMcqRepository;
import com.sep.sep_backend.user.entity.Language;
import com.sep.sep_backend.user.entity.LanguageLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * CommandLineRunner that automatically seeds the database with exercise data
 * from the database_init directory content.
 *
 * This seeder creates 24 exercises (12 English + 12 German) across all CEFR levels (A1-C2).
 * Data is based on exercises_content.md in the database_init directory.
 */

@Component
public class ExerciseDataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ExerciseDataSeeder.class);

    private final ExerciseMcqRepository mcqRepository;
    private final ExerciseFillBlankRepository fillBlankRepository;

    public ExerciseDataSeeder(ExerciseMcqRepository mcqRepository,
                              ExerciseFillBlankRepository fillBlankRepository) {
        this.mcqRepository = mcqRepository;
        this.fillBlankRepository = fillBlankRepository;
    }

    @Override
    public void run(String... args) {
        // Check if data already exists
        long mcqCount = mcqRepository.count();
        long fillBlankCount = fillBlankRepository.count();

        if (mcqCount > 0 || fillBlankCount > 0) {
            logger.info("Exercise data already exists (MCQ: {}, Fill-Blank: {}), skipping seeding",
                    mcqCount, fillBlankCount);
            return;
        }

        logger.info("Starting exercise data seeding...");

        seedEnglishExercises();
        seedGermanExercises();

        long newMcqCount = mcqRepository.count();
        long newFillBlankCount = fillBlankRepository.count();

        logger.info("Exercise data seeding completed!");
        logger.info("Created {} MCQ exercises", newMcqCount);
        logger.info("Created {} Fill-in-the-Blank exercises", newFillBlankCount);
        logger.info("Total: {} exercises (12 English + 12 German)", newMcqCount + newFillBlankCount);
        logger.info("Total XP Available: 540 (270 per language)");
    }

    private void seedEnglishExercises() {
        logger.info("Seeding English exercises...");

        // A1 Level - Basic Greetings & Introductions
        ExerciseMcq enA1Mcq = new ExerciseMcq();
        enA1Mcq.setTargetLanguage(Language.EN);
        enA1Mcq.setDifficultyLevel(LanguageLevel.A1);
        enA1Mcq.setTopic("greetings");
        enA1Mcq.setQuestionText("Hello! How ___ you?");
        enA1Mcq.setCorrectAnswer("are");
        enA1Mcq.setWrongOption1("is");
        enA1Mcq.setWrongOption2("am");
        enA1Mcq.setWrongOption3("be");
        enA1Mcq.setXpReward(10);
        enA1Mcq.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(enA1Mcq);

        ExerciseFillBlank enA1Fill = new ExerciseFillBlank();
        enA1Fill.setTargetLanguage(Language.EN);
        enA1Fill.setDifficultyLevel(LanguageLevel.A1);
        enA1Fill.setTopic("introductions");
        enA1Fill.setSentenceWithBlank("My name ___ Sarah.");
        enA1Fill.setCorrectAnswer("is");
        enA1Fill.setXpReward(10);
        enA1Fill.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(enA1Fill);

        // A2 Level - Daily Routines & Simple Descriptions
        ExerciseMcq enA2Mcq = new ExerciseMcq();
        enA2Mcq.setTargetLanguage(Language.EN);
        enA2Mcq.setDifficultyLevel(LanguageLevel.A2);
        enA2Mcq.setTopic("daily_routines");
        enA2Mcq.setQuestionText("I ___ breakfast at 7 AM every day.");
        enA2Mcq.setCorrectAnswer("eat");
        enA2Mcq.setWrongOption1("eats");
        enA2Mcq.setWrongOption2("eating");
        enA2Mcq.setWrongOption3("ate");
        enA2Mcq.setXpReward(15);
        enA2Mcq.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(enA2Mcq);

        ExerciseFillBlank enA2Fill = new ExerciseFillBlank();
        enA2Fill.setTargetLanguage(Language.EN);
        enA2Fill.setDifficultyLevel(LanguageLevel.A2);
        enA2Fill.setTopic("descriptions");
        enA2Fill.setSentenceWithBlank("The weather today is very ___.");
        enA2Fill.setCorrectAnswer("nice");
        enA2Fill.setXpReward(15);
        enA2Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(enA2Fill);

        // B1 Level - Travel & Past Experiences
        ExerciseMcq enB1Mcq = new ExerciseMcq();
        enB1Mcq.setTargetLanguage(Language.EN);
        enB1Mcq.setDifficultyLevel(LanguageLevel.B1);
        enB1Mcq.setTopic("travel");
        enB1Mcq.setQuestionText("Last summer, I ___ to Paris for vacation.");
        enB1Mcq.setCorrectAnswer("went");
        enB1Mcq.setWrongOption1("go");
        enB1Mcq.setWrongOption2("going");
        enB1Mcq.setWrongOption3("gone");
        enB1Mcq.setXpReward(20);
        enB1Mcq.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(enB1Mcq);

        ExerciseFillBlank enB1Fill = new ExerciseFillBlank();
        enB1Fill.setTargetLanguage(Language.EN);
        enB1Fill.setDifficultyLevel(LanguageLevel.B1);
        enB1Fill.setTopic("experiences");
        enB1Fill.setSentenceWithBlank("I have ___ visited that museum before.");
        enB1Fill.setCorrectAnswer("never");
        enB1Fill.setXpReward(20);
        enB1Fill.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(enB1Fill);

        // B2 Level - Opinions & Abstract Concepts
        ExerciseMcq enB2Mcq = new ExerciseMcq();
        enB2Mcq.setTargetLanguage(Language.EN);
        enB2Mcq.setDifficultyLevel(LanguageLevel.B2);
        enB2Mcq.setTopic("opinions");
        enB2Mcq.setQuestionText("In my opinion, technology has ___ our lives significantly.");
        enB2Mcq.setCorrectAnswer("improved");
        enB2Mcq.setWrongOption1("improving");
        enB2Mcq.setWrongOption2("improves");
        enB2Mcq.setWrongOption3("improve");
        enB2Mcq.setXpReward(25);
        enB2Mcq.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(enB2Mcq);

        ExerciseFillBlank enB2Fill = new ExerciseFillBlank();
        enB2Fill.setTargetLanguage(Language.EN);
        enB2Fill.setDifficultyLevel(LanguageLevel.B2);
        enB2Fill.setTopic("abstract_concepts");
        enB2Fill.setSentenceWithBlank("Success requires both talent and ___.");
        enB2Fill.setCorrectAnswer("perseverance");
        enB2Fill.setXpReward(25);
        enB2Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(enB2Fill);

        // C1 Level - Complex Ideas & Nuanced Language
        ExerciseMcq enC1Mcq = new ExerciseMcq();
        enC1Mcq.setTargetLanguage(Language.EN);
        enC1Mcq.setDifficultyLevel(LanguageLevel.C1);
        enC1Mcq.setTopic("complex_ideas");
        enC1Mcq.setQuestionText("The research findings ___ that climate change is accelerating.");
        enC1Mcq.setCorrectAnswer("indicate");
        enC1Mcq.setWrongOption1("indicates");
        enC1Mcq.setWrongOption2("indicating");
        enC1Mcq.setWrongOption3("indicated");
        enC1Mcq.setXpReward(30);
        enC1Mcq.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(enC1Mcq);

        ExerciseFillBlank enC1Fill = new ExerciseFillBlank();
        enC1Fill.setTargetLanguage(Language.EN);
        enC1Fill.setDifficultyLevel(LanguageLevel.C1);
        enC1Fill.setTopic("nuanced_language");
        enC1Fill.setSentenceWithBlank("Her argument was ___ despite the opposing evidence.");
        enC1Fill.setCorrectAnswer("compelling");
        enC1Fill.setXpReward(30);
        enC1Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(enC1Fill);

        // C2 Level - Idiomatic Expressions & Sophisticated Language
        ExerciseMcq enC2Mcq = new ExerciseMcq();
        enC2Mcq.setTargetLanguage(Language.EN);
        enC2Mcq.setDifficultyLevel(LanguageLevel.C2);
        enC2Mcq.setTopic("idioms");
        enC2Mcq.setQuestionText("After months of preparation, the project finally came to ___.");
        enC2Mcq.setCorrectAnswer("fruition");
        enC2Mcq.setWrongOption1("completion");
        enC2Mcq.setWrongOption2("ending");
        enC2Mcq.setWrongOption3("conclusion");
        enC2Mcq.setXpReward(35);
        enC2Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(enC2Mcq);

        ExerciseFillBlank enC2Fill = new ExerciseFillBlank();
        enC2Fill.setTargetLanguage(Language.EN);
        enC2Fill.setDifficultyLevel(LanguageLevel.C2);
        enC2Fill.setTopic("sophisticated_language");
        enC2Fill.setSentenceWithBlank("The author's prose was characterized by its ___ eloquence.");
        enC2Fill.setCorrectAnswer("unparalleled");
        enC2Fill.setXpReward(35);
        enC2Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(enC2Fill);

        logger.info("English exercises seeded: 6 MCQ + 6 Fill-Blank = 12 total");
    }

    private void seedGermanExercises() {
        logger.info("Seeding German exercises...");

        // A1 Level - Grüße (Basic Greetings & Introductions)
        ExerciseMcq deA1Mcq = new ExerciseMcq();
        deA1Mcq.setTargetLanguage(Language.DE);
        deA1Mcq.setDifficultyLevel(LanguageLevel.A1);
        deA1Mcq.setTopic("greetings");
        deA1Mcq.setQuestionText("Guten Tag! Wie ___ es Ihnen?");
        deA1Mcq.setCorrectAnswer("geht");
        deA1Mcq.setWrongOption1("gehen");
        deA1Mcq.setWrongOption2("gehst");
        deA1Mcq.setWrongOption3("ging");
        deA1Mcq.setXpReward(10);
        deA1Mcq.setContentType(ExerciseContentType.GRAMMAR);

        mcqRepository.save(deA1Mcq);

        ExerciseFillBlank deA1Fill = new ExerciseFillBlank();
        deA1Fill.setTargetLanguage(Language.DE);
        deA1Fill.setDifficultyLevel(LanguageLevel.A1);
        deA1Fill.setTopic("introductions");
        deA1Fill.setSentenceWithBlank("Mein Name ___ Sarah.");
        deA1Fill.setCorrectAnswer("ist");
        deA1Fill.setXpReward(10);
        deA1Fill.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(deA1Fill);

        // A2 Level - Alltag (Daily Routines & Simple Descriptions)
        ExerciseMcq deA2Mcq = new ExerciseMcq();
        deA2Mcq.setTargetLanguage(Language.DE);
        deA2Mcq.setDifficultyLevel(LanguageLevel.A2);
        deA2Mcq.setTopic("daily_routines");
        deA2Mcq.setQuestionText("Ich ___ jeden Tag um 7 Uhr Frühstück.");
        deA2Mcq.setCorrectAnswer("esse");
        deA2Mcq.setWrongOption1("isst");
        deA2Mcq.setWrongOption2("essen");
        deA2Mcq.setWrongOption3("aß");
        deA2Mcq.setXpReward(15);
        deA2Mcq.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(deA2Mcq);

        ExerciseFillBlank deA2Fill = new ExerciseFillBlank();
        deA2Fill.setTargetLanguage(Language.DE);
        deA2Fill.setDifficultyLevel(LanguageLevel.A2);
        deA2Fill.setTopic("descriptions");
        deA2Fill.setSentenceWithBlank("Das Wetter heute ist sehr ___.");
        deA2Fill.setCorrectAnswer("schön");
        deA2Fill.setXpReward(15);
        deA2Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(deA2Fill);

        // B1 Level - Reisen (Travel & Past Experiences)
        ExerciseMcq deB1Mcq = new ExerciseMcq();
        deB1Mcq.setTargetLanguage(Language.DE);
        deB1Mcq.setDifficultyLevel(LanguageLevel.B1);
        deB1Mcq.setTopic("travel");
        deB1Mcq.setQuestionText("Letzten Sommer ___ ich nach Paris in den Urlaub.");
        deB1Mcq.setCorrectAnswer("fuhr");
        deB1Mcq.setWrongOption1("fahre");
        deB1Mcq.setWrongOption2("fahren");
        deB1Mcq.setWrongOption3("gefahren");
        deB1Mcq.setXpReward(20);
        deB1Mcq.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(deB1Mcq);

        ExerciseFillBlank deB1Fill = new ExerciseFillBlank();
        deB1Fill.setTargetLanguage(Language.DE);
        deB1Fill.setDifficultyLevel(LanguageLevel.B1);
        deB1Fill.setTopic("experiences");
        deB1Fill.setSentenceWithBlank("Ich habe dieses Museum noch ___ besucht.");
        deB1Fill.setCorrectAnswer("nie");
        deB1Fill.setXpReward(20);
        deB1Fill.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(deB1Fill);

        // B2 Level - Meinungen (Opinions & Abstract Concepts)
        ExerciseMcq deB2Mcq = new ExerciseMcq();
        deB2Mcq.setTargetLanguage(Language.DE);
        deB2Mcq.setDifficultyLevel(LanguageLevel.B2);
        deB2Mcq.setTopic("opinions");
        deB2Mcq.setQuestionText("Meiner Meinung nach hat die Technologie unser Leben erheblich ___.");
        deB2Mcq.setCorrectAnswer("verbessert");
        deB2Mcq.setWrongOption1("verbessern");
        deB2Mcq.setWrongOption2("verbessernd");
        deB2Mcq.setWrongOption3("verbesserte");
        deB2Mcq.setXpReward(25);
        deB2Mcq.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(deB2Mcq);

        ExerciseFillBlank deB2Fill = new ExerciseFillBlank();
        deB2Fill.setTargetLanguage(Language.DE);
        deB2Fill.setDifficultyLevel(LanguageLevel.B2);
        deB2Fill.setTopic("abstract_concepts");
        deB2Fill.setSentenceWithBlank("Erfolg erfordert sowohl Talent als auch ___.");
        deB2Fill.setCorrectAnswer("Ausdauer");
        deB2Fill.setXpReward(25);
        deB2Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(deB2Fill);

        // C1 Level - Komplexe Ideen (Complex Ideas & Nuanced Language)
        ExerciseMcq deC1Mcq = new ExerciseMcq();
        deC1Mcq.setTargetLanguage(Language.DE);
        deC1Mcq.setDifficultyLevel(LanguageLevel.C1);
        deC1Mcq.setTopic("complex_ideas");
        deC1Mcq.setQuestionText("Die Forschungsergebnisse ___ darauf hin, dass sich der Klimawandel beschleunigt.");
        deC1Mcq.setCorrectAnswer("deuten");
        deC1Mcq.setWrongOption1("deutet");
        deC1Mcq.setWrongOption2("deutend");
        deC1Mcq.setWrongOption3("gedeutet");
        deC1Mcq.setXpReward(30);
        deC1Mcq.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(deC1Mcq);

        ExerciseFillBlank deC1Fill = new ExerciseFillBlank();
        deC1Fill.setTargetLanguage(Language.DE);
        deC1Fill.setDifficultyLevel(LanguageLevel.C1);
        deC1Fill.setTopic("nuanced_language");
        deC1Fill.setSentenceWithBlank("Ihr Argument war trotz der gegenteiligen Beweise ___.");
        deC1Fill.setCorrectAnswer("überzeugend");
        deC1Fill.setXpReward(30);
        deC1Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(deC1Fill);

        // C2 Level - Gehobene Sprache (Idiomatic Expressions & Sophisticated Language)
        ExerciseMcq deC2Mcq = new ExerciseMcq();
        deC2Mcq.setTargetLanguage(Language.DE);
        deC2Mcq.setDifficultyLevel(LanguageLevel.C2);
        deC2Mcq.setTopic("idioms");
        deC2Mcq.setQuestionText("Nach monatelanger Vorbereitung kam das Projekt endlich zur ___.");
        deC2Mcq.setCorrectAnswer("Vollendung");
        deC2Mcq.setWrongOption1("Beendigung");
        deC2Mcq.setWrongOption2("Abschluss");
        deC2Mcq.setWrongOption3("Fertigstellung");
        deC2Mcq.setXpReward(35);
        deC2Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(deC2Mcq);

        ExerciseFillBlank deC2Fill = new ExerciseFillBlank();
        deC2Fill.setTargetLanguage(Language.DE);
        deC2Fill.setDifficultyLevel(LanguageLevel.C2);
        deC2Fill.setTopic("sophisticated_language");
        deC2Fill.setSentenceWithBlank("Die Prosa des Autors zeichnete sich durch ihre ___ Eloquenz aus.");
        deC2Fill.setCorrectAnswer("unvergleichliche");
        deC2Fill.setXpReward(35);
        deC2Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(deC2Fill);

        logger.info("German exercises seeded: 6 MCQ + 6 Fill-Blank = 12 total");
    }
}