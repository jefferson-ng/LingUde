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
        logger.info("Total: {} exercises (48 English + 48 German)", newMcqCount + newFillBlankCount);
        logger.info("Total XP Available: 2160 (1080 EN + 1080 DE)");
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

        ExerciseMcq enA1_2Mcq = new ExerciseMcq();
        enA1_2Mcq.setTargetLanguage(Language.EN);
        enA1_2Mcq.setDifficultyLevel(LanguageLevel.A1);
        enA1_2Mcq.setTopic("greetings");
        enA1_2Mcq.setQuestionText("Which word is a greeting?");
        enA1_2Mcq.setCorrectAnswer("Hello");
        enA1_2Mcq.setWrongOption1("Goodbye");
        enA1_2Mcq.setWrongOption2("Thank you");
        enA1_2Mcq.setWrongOption3("Please");
        enA1_2Mcq.setXpReward(10);
        enA1_2Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(enA1_2Mcq);

        ExerciseMcq enA1_3Mcq = new ExerciseMcq();
        enA1_3Mcq.setTargetLanguage(Language.EN);
        enA1_3Mcq.setDifficultyLevel(LanguageLevel.A1);
        enA1_3Mcq.setTopic("introductions");
        enA1_3Mcq.setQuestionText("Which phrase is used to ask someone's name?");
        enA1_3Mcq.setCorrectAnswer("What is your name?");
        enA1_3Mcq.setWrongOption1("Where do you live?");
        enA1_3Mcq.setWrongOption2("How old are you?");
        enA1_3Mcq.setWrongOption3("Do you like coffee?");
        enA1_3Mcq.setXpReward(10);
        enA1_3Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(enA1_3Mcq);

        ExerciseMcq enA1_4Mcq = new ExerciseMcq();
        enA1_4Mcq.setTargetLanguage(Language.EN);
        enA1_4Mcq.setDifficultyLevel(LanguageLevel.A1);
        enA1_4Mcq.setTopic("greetings");
        enA1_4Mcq.setQuestionText("Which word is used to politely get someone's attention?");
        enA1_4Mcq.setCorrectAnswer("Excuse me");
        enA1_4Mcq.setWrongOption1("See you later");
        enA1_4Mcq.setWrongOption2("Good night");
        enA1_4Mcq.setWrongOption3("Welcome");
        enA1_4Mcq.setXpReward(10);
        enA1_4Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(enA1_4Mcq);

        ExerciseFillBlank enA1Fill = new ExerciseFillBlank();
        enA1Fill.setTargetLanguage(Language.EN);
        enA1Fill.setDifficultyLevel(LanguageLevel.A1);
        enA1Fill.setTopic("introductions");
        enA1Fill.setSentenceWithBlank("My name ___ Sarah.");
        enA1Fill.setCorrectAnswer("is");
        enA1Fill.setXpReward(10);
        enA1Fill.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(enA1Fill);

        ExerciseFillBlank enA1_2Fill = new ExerciseFillBlank();
        enA1_2Fill.setTargetLanguage(Language.EN);
        enA1_2Fill.setDifficultyLevel(LanguageLevel.A1);
        enA1_2Fill.setTopic("introductions");
        enA1_2Fill.setSentenceWithBlank("Nice to ___ you!");
        enA1_2Fill.setCorrectAnswer("meet");
        enA1_2Fill.setXpReward(10);
        enA1_2Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(enA1_2Fill);

        ExerciseFillBlank enA1_3Fill = new ExerciseFillBlank();
        enA1_3Fill.setTargetLanguage(Language.EN);
        enA1_3Fill.setDifficultyLevel(LanguageLevel.A1);
        enA1_3Fill.setTopic("greetings");
        enA1_3Fill.setSentenceWithBlank("Good ___!");
        enA1_3Fill.setCorrectAnswer("morning");
        enA1_3Fill.setXpReward(10);
        enA1_3Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(enA1_3Fill);

        ExerciseFillBlank enA1_4Fill = new ExerciseFillBlank();
        enA1_4Fill.setTargetLanguage(Language.EN);
        enA1_4Fill.setDifficultyLevel(LanguageLevel.A1);
        enA1_4Fill.setTopic("introductions");
        enA1_4Fill.setSentenceWithBlank("I am from ___.");
        enA1_4Fill.setCorrectAnswer("Germany");
        enA1_4Fill.setXpReward(10);
        enA1_4Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(enA1_4Fill);


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

        ExerciseMcq enA2_2Mcq = new ExerciseMcq();
        enA2_2Mcq.setTargetLanguage(Language.EN);
        enA2_2Mcq.setDifficultyLevel(LanguageLevel.A2);
        enA2_2Mcq.setTopic("daily_routines");
        enA2_2Mcq.setQuestionText("I usually ___ to work at 8 AM.");
        enA2_2Mcq.setCorrectAnswer("go");
        enA2_2Mcq.setWrongOption1("goes");
        enA2_2Mcq.setWrongOption2("went");
        enA2_2Mcq.setWrongOption3("going");
        enA2_2Mcq.setXpReward(15);
        enA2_2Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(enA2_2Mcq);

        ExerciseMcq enA2_3Mcq = new ExerciseMcq();
        enA2_3Mcq.setTargetLanguage(Language.EN);
        enA2_3Mcq.setDifficultyLevel(LanguageLevel.A2);
        enA2_3Mcq.setTopic("daily_routines");
        enA2_3Mcq.setQuestionText("She usually ___ TV in the evening.");
        enA2_3Mcq.setCorrectAnswer("watches");
        enA2_3Mcq.setWrongOption1("watch");
        enA2_3Mcq.setWrongOption2("watched");
        enA2_3Mcq.setWrongOption3("watching");
        enA2_3Mcq.setXpReward(15);
        enA2_3Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(enA2_3Mcq);

        ExerciseMcq enA2_4Mcq = new ExerciseMcq();
        enA2_4Mcq.setTargetLanguage(Language.EN);
        enA2_4Mcq.setDifficultyLevel(LanguageLevel.A2);
        enA2_4Mcq.setTopic("descriptions");
        enA2_4Mcq.setQuestionText("Her bag is very ___.");
        enA2_4Mcq.setCorrectAnswer("heavy");
        enA2_4Mcq.setWrongOption1("heavily");
        enA2_4Mcq.setWrongOption2("heat");
        enA2_4Mcq.setWrongOption3("huge");
        enA2_4Mcq.setXpReward(15);
        enA2_4Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(enA2_4Mcq);

        ExerciseFillBlank enA2Fill = new ExerciseFillBlank();
        enA2Fill.setTargetLanguage(Language.EN);
        enA2Fill.setDifficultyLevel(LanguageLevel.A2);
        enA2Fill.setTopic("descriptions");
        enA2Fill.setSentenceWithBlank("The weather today is very ___.");
        enA2Fill.setCorrectAnswer("nice");
        enA2Fill.setXpReward(15);
        enA2Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(enA2Fill);

        ExerciseFillBlank enA2_2Fill = new ExerciseFillBlank();
        enA2_2Fill.setTargetLanguage(Language.EN);
        enA2_2Fill.setDifficultyLevel(LanguageLevel.A2);
        enA2_2Fill.setTopic("daily_routines");
        enA2_2Fill.setSentenceWithBlank("I usually have lunch at ___.");
        enA2_2Fill.setCorrectAnswer("noon");
        enA2_2Fill.setXpReward(15);
        enA2_2Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(enA2_2Fill);

        ExerciseFillBlank enA2_3Fill = new ExerciseFillBlank();
        enA2_3Fill.setTargetLanguage(Language.EN);
        enA2_3Fill.setDifficultyLevel(LanguageLevel.A2);
        enA2_3Fill.setTopic("daily_routines");
        enA2_3Fill.setSentenceWithBlank("She goes to bed at ___.");
        enA2_3Fill.setCorrectAnswer("midnight");
        enA2_3Fill.setXpReward(15);
        enA2_3Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(enA2_3Fill);

        ExerciseFillBlank enA2_4Fill = new ExerciseFillBlank();
        enA2_4Fill.setTargetLanguage(Language.EN);
        enA2_4Fill.setDifficultyLevel(LanguageLevel.A2);
        enA2_4Fill.setTopic("descriptions");
        enA2_4Fill.setSentenceWithBlank("The movie yesterday was really ___.");
        enA2_4Fill.setCorrectAnswer("interesting");
        enA2_4Fill.setXpReward(15);
        enA2_4Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(enA2_4Fill);


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

        ExerciseMcq enB1_2Mcq = new ExerciseMcq();
        enB1_2Mcq.setTargetLanguage(Language.EN);
        enB1_2Mcq.setDifficultyLevel(LanguageLevel.B1);
        enB1_2Mcq.setTopic("travel");
        enB1_2Mcq.setQuestionText("We ___ our flight because we arrived late at the airport.");
        enB1_2Mcq.setCorrectAnswer("missed");
        enB1_2Mcq.setWrongOption1("miss");
        enB1_2Mcq.setWrongOption2("missing");
        enB1_2Mcq.setWrongOption3("misses");
        enB1_2Mcq.setXpReward(20);
        enB1_2Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(enB1_2Mcq);

        ExerciseMcq enB1_3Mcq = new ExerciseMcq();
        enB1_3Mcq.setTargetLanguage(Language.EN);
        enB1_3Mcq.setDifficultyLevel(LanguageLevel.B1);
        enB1_3Mcq.setTopic("travel");
        enB1_3Mcq.setQuestionText("The hotel room was very ___.");
        enB1_3Mcq.setCorrectAnswer("comfortable");
        enB1_3Mcq.setWrongOption1("comfort");
        enB1_3Mcq.setWrongOption2("comfortably");
        enB1_3Mcq.setWrongOption3("comforting");
        enB1_3Mcq.setXpReward(20);
        enB1_3Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(enB1_3Mcq);

        ExerciseMcq enB1_4Mcq = new ExerciseMcq();
        enB1_4Mcq.setTargetLanguage(Language.EN);
        enB1_4Mcq.setDifficultyLevel(LanguageLevel.B1);
        enB1_4Mcq.setTopic("experiences");
        enB1_4Mcq.setQuestionText("I have never ___ such a beautiful place before.");
        enB1_4Mcq.setCorrectAnswer("seen");
        enB1_4Mcq.setWrongOption1("see");
        enB1_4Mcq.setWrongOption2("seeing");
        enB1_4Mcq.setWrongOption3("saw");
        enB1_4Mcq.setXpReward(20);
        enB1_4Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(enB1_4Mcq);

        ExerciseFillBlank enB1Fill = new ExerciseFillBlank();
        enB1Fill.setTargetLanguage(Language.EN);
        enB1Fill.setDifficultyLevel(LanguageLevel.B1);
        enB1Fill.setTopic("experiences");
        enB1Fill.setSentenceWithBlank("I have ___ visited that museum before.");
        enB1Fill.setCorrectAnswer("never");
        enB1Fill.setXpReward(20);
        enB1Fill.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(enB1Fill);

        ExerciseFillBlank enB1_2Fill = new ExerciseFillBlank();
        enB1_2Fill.setTargetLanguage(Language.EN);
        enB1_2Fill.setDifficultyLevel(LanguageLevel.B1);
        enB1_2Fill.setTopic("travel");
        enB1_2Fill.setSentenceWithBlank("We stayed in a small hotel near the ___.");
        enB1_2Fill.setCorrectAnswer("beach");
        enB1_2Fill.setXpReward(20);
        enB1_2Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(enB1_2Fill);

        ExerciseFillBlank enB1_3Fill = new ExerciseFillBlank();
        enB1_3Fill.setTargetLanguage(Language.EN);
        enB1_3Fill.setDifficultyLevel(LanguageLevel.B1);
        enB1_3Fill.setTopic("travel");
        enB1_3Fill.setSentenceWithBlank("The train was delayed due to heavy ___.");
        enB1_3Fill.setCorrectAnswer("traffic");
        enB1_3Fill.setXpReward(20);
        enB1_3Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(enB1_3Fill);

        ExerciseFillBlank enB1_4Fill = new ExerciseFillBlank();
        enB1_4Fill.setTargetLanguage(Language.EN);
        enB1_4Fill.setDifficultyLevel(LanguageLevel.B1);
        enB1_4Fill.setTopic("experiences");
        enB1_4Fill.setSentenceWithBlank("It was one of the most ___ trips of my life.");
        enB1_4Fill.setCorrectAnswer("exciting");
        enB1_4Fill.setXpReward(20);
        enB1_4Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(enB1_4Fill);


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

        ExerciseMcq enB2_2Mcq = new ExerciseMcq();
        enB2_2Mcq.setTargetLanguage(Language.EN);
        enB2_2Mcq.setDifficultyLevel(LanguageLevel.B2);
        enB2_2Mcq.setTopic("opinions");
        enB2_2Mcq.setQuestionText("Many people believe that education is the key to ___.");
        enB2_2Mcq.setCorrectAnswer("success");
        enB2_2Mcq.setWrongOption1("successful");
        enB2_2Mcq.setWrongOption2("succeeding");
        enB2_2Mcq.setWrongOption3("succeeds");
        enB2_2Mcq.setXpReward(25);
        enB2_2Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(enB2_2Mcq);

        ExerciseMcq enB2_3Mcq = new ExerciseMcq();
        enB2_3Mcq.setTargetLanguage(Language.EN);
        enB2_3Mcq.setDifficultyLevel(LanguageLevel.B2);
        enB2_3Mcq.setTopic("opinions");
        enB2_3Mcq.setQuestionText("It is widely argued that social media has a major impact on young people's ___.");
        enB2_3Mcq.setCorrectAnswer("behavior");
        enB2_3Mcq.setWrongOption1("behave");
        enB2_3Mcq.setWrongOption2("behaving");
        enB2_3Mcq.setWrongOption3("behaviors");
        enB2_3Mcq.setXpReward(25);
        enB2_3Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(enB2_3Mcq);

        ExerciseMcq enB2_4Mcq = new ExerciseMcq();
        enB2_4Mcq.setTargetLanguage(Language.EN);
        enB2_4Mcq.setDifficultyLevel(LanguageLevel.B2);
        enB2_4Mcq.setTopic("abstract_concepts");
        enB2_4Mcq.setQuestionText("A strong economy depends on stable political ___.");
        enB2_4Mcq.setCorrectAnswer("conditions");
        enB2_4Mcq.setWrongOption1("conditional");
        enB2_4Mcq.setWrongOption2("conditioning");
        enB2_4Mcq.setWrongOption3("conditioned");
        enB2_4Mcq.setXpReward(25);
        enB2_4Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(enB2_4Mcq);


        ExerciseFillBlank enB2Fill = new ExerciseFillBlank();
        enB2Fill.setTargetLanguage(Language.EN);
        enB2Fill.setDifficultyLevel(LanguageLevel.B2);
        enB2Fill.setTopic("abstract_concepts");
        enB2Fill.setSentenceWithBlank("Success requires both talent and ___.");
        enB2Fill.setCorrectAnswer("perseverance");
        enB2Fill.setXpReward(25);
        enB2Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(enB2Fill);

        ExerciseFillBlank enB2_2Fill = new ExerciseFillBlank();
        enB2_2Fill.setTargetLanguage(Language.EN);
        enB2_2Fill.setDifficultyLevel(LanguageLevel.B2);
        enB2_2Fill.setTopic("opinions");
        enB2_2Fill.setSentenceWithBlank("In my view, climate change is one of the most urgent ___ of our time.");
        enB2_2Fill.setCorrectAnswer("issues");
        enB2_2Fill.setXpReward(25);
        enB2_2Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(enB2_2Fill);

        ExerciseFillBlank enB2_3Fill = new ExerciseFillBlank();
        enB2_3Fill.setTargetLanguage(Language.EN);
        enB2_3Fill.setDifficultyLevel(LanguageLevel.B2);
        enB2_3Fill.setTopic("abstract_concepts");
        enB2_3Fill.setSentenceWithBlank("Critical thinking is essential for making ___ decisions.");
        enB2_3Fill.setCorrectAnswer("informed");
        enB2_3Fill.setXpReward(25);
        enB2_3Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(enB2_3Fill);

        ExerciseFillBlank enB2_4Fill = new ExerciseFillBlank();
        enB2_4Fill.setTargetLanguage(Language.EN);
        enB2_4Fill.setDifficultyLevel(LanguageLevel.B2);
        enB2_4Fill.setTopic("abstract_concepts");
        enB2_4Fill.setSentenceWithBlank("Economic growth often depends on technological ___.");
        enB2_4Fill.setCorrectAnswer("innovation");
        enB2_4Fill.setXpReward(25);
        enB2_4Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(enB2_4Fill);


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

        ExerciseMcq enC1_2Mcq = new ExerciseMcq();
        enC1_2Mcq.setTargetLanguage(Language.EN);
        enC1_2Mcq.setDifficultyLevel(LanguageLevel.C1);
        enC1_2Mcq.setTopic("complex_ideas");
        enC1_2Mcq.setQuestionText("The study aims to ___ the relationship between stress and productivity.");
        enC1_2Mcq.setCorrectAnswer("examine");
        enC1_2Mcq.setWrongOption1("exam");
        enC1_2Mcq.setWrongOption2("examination");
        enC1_2Mcq.setWrongOption3("examining");
        enC1_2Mcq.setXpReward(30);
        enC1_2Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(enC1_2Mcq);

        ExerciseMcq enC1_3Mcq = new ExerciseMcq();
        enC1_3Mcq.setTargetLanguage(Language.EN);
        enC1_3Mcq.setDifficultyLevel(LanguageLevel.C1);
        enC1_3Mcq.setTopic("complex_ideas");
        enC1_3Mcq.setQuestionText("Researchers often encounter challenges when trying to ___ accurate data.");
        enC1_3Mcq.setCorrectAnswer("collect");
        enC1_3Mcq.setWrongOption1("collection");
        enC1_3Mcq.setWrongOption2("collective");
        enC1_3Mcq.setWrongOption3("collecting");
        enC1_3Mcq.setXpReward(30);
        enC1_3Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(enC1_3Mcq);

        ExerciseMcq enC1_4Mcq = new ExerciseMcq();
        enC1_4Mcq.setTargetLanguage(Language.EN);
        enC1_4Mcq.setDifficultyLevel(LanguageLevel.C1);
        enC1_4Mcq.setTopic("nuanced_language");
        enC1_4Mcq.setQuestionText("Her statement was intentionally ___ to avoid causing conflict.");
        enC1_4Mcq.setCorrectAnswer("vague");
        enC1_4Mcq.setWrongOption1("vaguely");
        enC1_4Mcq.setWrongOption2("vagueness");
        enC1_4Mcq.setWrongOption3("vaguer");
        enC1_4Mcq.setXpReward(30);
        enC1_4Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(enC1_4Mcq);

        ExerciseFillBlank enC1Fill = new ExerciseFillBlank();
        enC1Fill.setTargetLanguage(Language.EN);
        enC1Fill.setDifficultyLevel(LanguageLevel.C1);
        enC1Fill.setTopic("nuanced_language");
        enC1Fill.setSentenceWithBlank("Her argument was ___ despite the opposing evidence.");
        enC1Fill.setCorrectAnswer("compelling");
        enC1Fill.setXpReward(30);
        enC1Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(enC1Fill);

        ExerciseFillBlank enC1_2Fill = new ExerciseFillBlank();
        enC1_2Fill.setTargetLanguage(Language.EN);
        enC1_2Fill.setDifficultyLevel(LanguageLevel.C1);
        enC1_2Fill.setTopic("nuanced_language");
        enC1_2Fill.setSentenceWithBlank("The speaker's message was intentionally ___ to allow multiple interpretations.");
        enC1_2Fill.setCorrectAnswer("ambiguous");
        enC1_2Fill.setXpReward(30);
        enC1_2Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(enC1_2Fill);

        ExerciseFillBlank enC1_3Fill = new ExerciseFillBlank();
        enC1_3Fill.setTargetLanguage(Language.EN);
        enC1_3Fill.setDifficultyLevel(LanguageLevel.C1);
        enC1_3Fill.setTopic("complex_ideas");
        enC1_3Fill.setSentenceWithBlank("The results were surprising and required careful ___ to understand fully.");
        enC1_3Fill.setCorrectAnswer("analysis");
        enC1_3Fill.setXpReward(30);
        enC1_3Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(enC1_3Fill);

        ExerciseFillBlank enC1_4Fill = new ExerciseFillBlank();
        enC1_4Fill.setTargetLanguage(Language.EN);
        enC1_4Fill.setDifficultyLevel(LanguageLevel.C1);
        enC1_4Fill.setTopic("complex_ideas");
        enC1_4Fill.setSentenceWithBlank("A comprehensive understanding of the issue requires a ___ approach.");
        enC1_4Fill.setCorrectAnswer("multidisciplinary");
        enC1_4Fill.setXpReward(30);
        enC1_4Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(enC1_4Fill);


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

        ExerciseMcq enC2_2Mcq = new ExerciseMcq();
        enC2_2Mcq.setTargetLanguage(Language.EN);
        enC2_2Mcq.setDifficultyLevel(LanguageLevel.C2);
        enC2_2Mcq.setTopic("idioms");
        enC2_2Mcq.setQuestionText("Which expression means 'immediately, without preparation'?");
        enC2_2Mcq.setCorrectAnswer("on the spot");
        enC2_2Mcq.setWrongOption1("on the way");
        enC2_2Mcq.setWrongOption2("on the line");
        enC2_2Mcq.setWrongOption3("on the side");
        enC2_2Mcq.setXpReward(35);
        enC2_2Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(enC2_2Mcq);

        ExerciseMcq enC2_3Mcq = new ExerciseMcq();
        enC2_3Mcq.setTargetLanguage(Language.EN);
        enC2_3Mcq.setDifficultyLevel(LanguageLevel.C2);
        enC2_3Mcq.setTopic("idioms");
        enC2_3Mcq.setQuestionText("Which phrase is used to introduce a short summary?");
        enC2_3Mcq.setCorrectAnswer("in a nutshell");
        enC2_3Mcq.setWrongOption1("in the shell");
        enC2_3Mcq.setWrongOption2("in the middle");
        enC2_3Mcq.setWrongOption3("in the moment");
        enC2_3Mcq.setXpReward(35);
        enC2_3Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(enC2_3Mcq);

        ExerciseMcq enC2_4Mcq = new ExerciseMcq();
        enC2_4Mcq.setTargetLanguage(Language.EN);
        enC2_4Mcq.setDifficultyLevel(LanguageLevel.C2);
        enC2_4Mcq.setTopic("sophisticated_language");
        enC2_4Mcq.setQuestionText("The professor's argument was highly ___ and difficult to refute.");
        enC2_4Mcq.setCorrectAnswer("persuasive");
        enC2_4Mcq.setWrongOption1("persuasion");
        enC2_4Mcq.setWrongOption2("persuaded");
        enC2_4Mcq.setWrongOption3("persuading");
        enC2_4Mcq.setXpReward(35);
        enC2_4Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(enC2_4Mcq);


        ExerciseFillBlank enC2Fill = new ExerciseFillBlank();
        enC2Fill.setTargetLanguage(Language.EN);
        enC2Fill.setDifficultyLevel(LanguageLevel.C2);
        enC2Fill.setTopic("sophisticated_language");
        enC2Fill.setSentenceWithBlank("The author's prose was characterized by its ___ eloquence.");
        enC2Fill.setCorrectAnswer("unparalleled");
        enC2Fill.setXpReward(35);
        enC2Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(enC2Fill);

        ExerciseFillBlank enC2_2Fill = new ExerciseFillBlank();
        enC2_2Fill.setTargetLanguage(Language.EN);
        enC2_2Fill.setDifficultyLevel(LanguageLevel.C2);
        enC2_2Fill.setTopic("sophisticated_language");
        enC2_2Fill.setSentenceWithBlank("His speech was so ___ that the audience remained silent for a moment.");
        enC2_2Fill.setCorrectAnswer("profound");
        enC2_2Fill.setXpReward(35);
        enC2_2Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(enC2_2Fill);

        ExerciseFillBlank enC2_3Fill = new ExerciseFillBlank();
        enC2_3Fill.setTargetLanguage(Language.EN);
        enC2_3Fill.setDifficultyLevel(LanguageLevel.C2);
        enC2_3Fill.setTopic("sophisticated_language");
        enC2_3Fill.setSentenceWithBlank("The report offers a highly ___ assessment of the current situation.");
        enC2_3Fill.setCorrectAnswer("nuanced");
        enC2_3Fill.setXpReward(35);
        enC2_3Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(enC2_3Fill);

        ExerciseFillBlank enC2_4Fill = new ExerciseFillBlank();
        enC2_4Fill.setTargetLanguage(Language.EN);
        enC2_4Fill.setDifficultyLevel(LanguageLevel.C2);
        enC2_4Fill.setTopic("sophisticated_language");
        enC2_4Fill.setSentenceWithBlank("Even under pressure, she answered with calm ___ and confidence.");
        enC2_4Fill.setCorrectAnswer("composure");
        enC2_4Fill.setXpReward(35);
        enC2_4Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(enC2_4Fill);

        logger.info("English exercises seeded: 24 MCQ + 24 Fill-Blank = 48 total");
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

        ExerciseMcq deA1_2Mcq = new ExerciseMcq();
        deA1_2Mcq.setTargetLanguage(Language.DE);
        deA1_2Mcq.setDifficultyLevel(LanguageLevel.A1);
        deA1_2Mcq.setTopic("greetings");
        deA1_2Mcq.setQuestionText("Welche Form ist eine passende Begrüßung?");
        deA1_2Mcq.setCorrectAnswer("Guten Morgen");
        deA1_2Mcq.setWrongOption1("Gute Nacht");
        deA1_2Mcq.setWrongOption2("Tschüss");
        deA1_2Mcq.setWrongOption3("Bitte");
        deA1_2Mcq.setXpReward(10);
        deA1_2Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(deA1_2Mcq);

        ExerciseMcq deA1_3Mcq = new ExerciseMcq();
        deA1_3Mcq.setTargetLanguage(Language.DE);
        deA1_3Mcq.setDifficultyLevel(LanguageLevel.A1);
        deA1_3Mcq.setTopic("greetings");
        deA1_3Mcq.setQuestionText("Wie sagt man auf Deutsch „Hello“?");
        deA1_3Mcq.setCorrectAnswer("Hallo");
        deA1_3Mcq.setWrongOption1("Tschüss");
        deA1_3Mcq.setWrongOption2("Danke");
        deA1_3Mcq.setWrongOption3("Bitte");
        deA1_3Mcq.setXpReward(10);
        deA1_3Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(deA1_3Mcq);

        ExerciseMcq deA1_4Mcq = new ExerciseMcq();
        deA1_4Mcq.setTargetLanguage(Language.DE);
        deA1_4Mcq.setDifficultyLevel(LanguageLevel.A1);
        deA1_4Mcq.setTopic("introductions");
        deA1_4Mcq.setQuestionText("Welche Frage benutzt du, um nach dem Namen zu fragen?");
        deA1_4Mcq.setCorrectAnswer("Wie heißt du?");
        deA1_4Mcq.setWrongOption1("Wo wohnst du?");
        deA1_4Mcq.setWrongOption2("Wie alt bist du?");
        deA1_4Mcq.setWrongOption3("Magst du Kaffee?");
        deA1_4Mcq.setXpReward(10);
        deA1_4Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(deA1_4Mcq);

        ExerciseFillBlank deA1Fill = new ExerciseFillBlank();
        deA1Fill.setTargetLanguage(Language.DE);
        deA1Fill.setDifficultyLevel(LanguageLevel.A1);
        deA1Fill.setTopic("introductions");
        deA1Fill.setSentenceWithBlank("Mein Name ___ Sarah.");
        deA1Fill.setCorrectAnswer("ist");
        deA1Fill.setXpReward(10);
        deA1Fill.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(deA1Fill);

        ExerciseFillBlank deA1_2Fill = new ExerciseFillBlank();
        deA1_2Fill.setTargetLanguage(Language.DE);
        deA1_2Fill.setDifficultyLevel(LanguageLevel.A1);
        deA1_2Fill.setTopic("greetings");
        deA1_2Fill.setSentenceWithBlank("___ Tag, ich heiße Anna.");
        deA1_2Fill.setCorrectAnswer("Guten");
        deA1_2Fill.setXpReward(10);
        deA1_2Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(deA1_2Fill);

        ExerciseFillBlank deA1_3Fill = new ExerciseFillBlank();
        deA1_3Fill.setTargetLanguage(Language.DE);
        deA1_3Fill.setDifficultyLevel(LanguageLevel.A1);
        deA1_3Fill.setTopic("introductions");
        deA1_3Fill.setSentenceWithBlank("Hallo, ich ___ Sara.");
        deA1_3Fill.setCorrectAnswer("bin");
        deA1_3Fill.setXpReward(10);
        deA1_3Fill.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(deA1_3Fill);

        ExerciseFillBlank deA1_4Fill = new ExerciseFillBlank();
        deA1_4Fill.setTargetLanguage(Language.DE);
        deA1_4Fill.setDifficultyLevel(LanguageLevel.A1);
        deA1_4Fill.setTopic("introductions");
        deA1_4Fill.setSentenceWithBlank("Ich komme aus ___.");
        deA1_4Fill.setCorrectAnswer("Deutschland");
        deA1_4Fill.setXpReward(10);
        deA1_4Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(deA1_4Fill);


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

        ExerciseMcq deA2_2Mcq = new ExerciseMcq();
        deA2_2Mcq.setTargetLanguage(Language.DE);
        deA2_2Mcq.setDifficultyLevel(LanguageLevel.A2);
        deA2_2Mcq.setTopic("daily_routines");
        deA2_2Mcq.setQuestionText("Ich ___ um 8 Uhr zur Arbeit.");
        deA2_2Mcq.setCorrectAnswer("gehe");
        deA2_2Mcq.setWrongOption1("geht");
        deA2_2Mcq.setWrongOption2("ging");
        deA2_2Mcq.setWrongOption3("gegangen");
        deA2_2Mcq.setXpReward(15);
        deA2_2Mcq.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(deA2_2Mcq);

        ExerciseMcq deA2_3Mcq = new ExerciseMcq();
        deA2_3Mcq.setTargetLanguage(Language.DE);
        deA2_3Mcq.setDifficultyLevel(LanguageLevel.A2);
        deA2_3Mcq.setTopic("daily_routines");
        deA2_3Mcq.setQuestionText("Abends ___ ich oft fern.");
        deA2_3Mcq.setCorrectAnswer("sehe");
        deA2_3Mcq.setWrongOption1("sehen");
        deA2_3Mcq.setWrongOption2("sieht");
        deA2_3Mcq.setWrongOption3("sah");
        deA2_3Mcq.setXpReward(15);
        deA2_3Mcq.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(deA2_3Mcq);

        ExerciseMcq deA2_4Mcq = new ExerciseMcq();
        deA2_4Mcq.setTargetLanguage(Language.DE);
        deA2_4Mcq.setDifficultyLevel(LanguageLevel.A2);
        deA2_4Mcq.setTopic("descriptions");
        deA2_4Mcq.setQuestionText("Meine Wohnung ist sehr ___.");
        deA2_4Mcq.setCorrectAnswer("gemütlich");
        deA2_4Mcq.setWrongOption1("gemütlichst");
        deA2_4Mcq.setWrongOption2("gemütlicher");
        deA2_4Mcq.setWrongOption3("gemütlichere");
        deA2_4Mcq.setXpReward(15);
        deA2_4Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(deA2_4Mcq);

        ExerciseFillBlank deA2Fill = new ExerciseFillBlank();
        deA2Fill.setTargetLanguage(Language.DE);
        deA2Fill.setDifficultyLevel(LanguageLevel.A2);
        deA2Fill.setTopic("descriptions");
        deA2Fill.setSentenceWithBlank("Das Wetter heute ist sehr ___.");
        deA2Fill.setCorrectAnswer("schön");
        deA2Fill.setXpReward(15);
        deA2Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(deA2Fill);

        ExerciseFillBlank deA2_2Fill = new ExerciseFillBlank();
        deA2_2Fill.setTargetLanguage(Language.DE);
        deA2_2Fill.setDifficultyLevel(LanguageLevel.A2);
        deA2_2Fill.setTopic("daily_routines");
        deA2_2Fill.setSentenceWithBlank("Ich stehe jeden Morgen um ___ Uhr auf.");
        deA2_2Fill.setCorrectAnswer("7");
        deA2_2Fill.setXpReward(15);
        deA2_2Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(deA2_2Fill);

        ExerciseFillBlank deA2_3Fill = new ExerciseFillBlank();
        deA2_3Fill.setTargetLanguage(Language.DE);
        deA2_3Fill.setDifficultyLevel(LanguageLevel.A2);
        deA2_3Fill.setTopic("daily_routines");
        deA2_3Fill.setSentenceWithBlank("Nach der Arbeit ___ ich oft einkaufen.");
        deA2_3Fill.setCorrectAnswer("gehe");
        deA2_3Fill.setXpReward(15);
        deA2_3Fill.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(deA2_3Fill);

        ExerciseFillBlank deA2_4Fill = new ExerciseFillBlank();
        deA2_4Fill.setTargetLanguage(Language.DE);
        deA2_4Fill.setDifficultyLevel(LanguageLevel.A2);
        deA2_4Fill.setTopic("descriptions");
        deA2_4Fill.setSentenceWithBlank("Am Wochenende besuche ich oft meine ___.");
        deA2_4Fill.setCorrectAnswer("Freunde");
        deA2_4Fill.setXpReward(15);
        deA2_4Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(deA2_4Fill);


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

        ExerciseMcq deB1_2Mcq = new ExerciseMcq();
        deB1_2Mcq.setTargetLanguage(Language.DE);
        deB1_2Mcq.setDifficultyLevel(LanguageLevel.B1);
        deB1_2Mcq.setTopic("travel");
        deB1_2Mcq.setQuestionText("Wir ___ den Zug, weil wir zu spät am Bahnhof ankamen.");
        deB1_2Mcq.setCorrectAnswer("verpassten");
        deB1_2Mcq.setWrongOption1("verpassen");
        deB1_2Mcq.setWrongOption2("verpasste");
        deB1_2Mcq.setWrongOption3("verpasst");
        deB1_2Mcq.setXpReward(20);
        deB1_2Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(deB1_2Mcq);

        ExerciseMcq deB1_3Mcq = new ExerciseMcq();
        deB1_3Mcq.setTargetLanguage(Language.DE);
        deB1_3Mcq.setDifficultyLevel(LanguageLevel.B1);
        deB1_3Mcq.setTopic("travel");
        deB1_3Mcq.setQuestionText("Das Hotelzimmer war sehr ___.");
        deB1_3Mcq.setCorrectAnswer("komfortabel");
        deB1_3Mcq.setWrongOption1("Komfort");
        deB1_3Mcq.setWrongOption2("komfortable");
        deB1_3Mcq.setWrongOption3("komfortabler");
        deB1_3Mcq.setXpReward(20);
        deB1_3Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(deB1_3Mcq);

        ExerciseMcq deB1_4Mcq = new ExerciseMcq();
        deB1_4Mcq.setTargetLanguage(Language.DE);
        deB1_4Mcq.setDifficultyLevel(LanguageLevel.B1);
        deB1_4Mcq.setTopic("experiences");
        deB1_4Mcq.setQuestionText("Es war eine meiner ___ Reisen.");
        deB1_4Mcq.setCorrectAnswer("schönsten");
        deB1_4Mcq.setWrongOption1("schöne");
        deB1_4Mcq.setWrongOption2("schöner");
        deB1_4Mcq.setWrongOption3("schönere");
        deB1_4Mcq.setXpReward(20);
        deB1_4Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(deB1_4Mcq);

        ExerciseFillBlank deB1Fill = new ExerciseFillBlank();
        deB1Fill.setTargetLanguage(Language.DE);
        deB1Fill.setDifficultyLevel(LanguageLevel.B1);
        deB1Fill.setTopic("experiences");
        deB1Fill.setSentenceWithBlank("Ich habe dieses Museum noch ___ besucht.");
        deB1Fill.setCorrectAnswer("nie");
        deB1Fill.setXpReward(20);
        deB1Fill.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(deB1Fill);

        ExerciseFillBlank deB1_2Fill = new ExerciseFillBlank();
        deB1_2Fill.setTargetLanguage(Language.DE);
        deB1_2Fill.setDifficultyLevel(LanguageLevel.B1);
        deB1_2Fill.setTopic("travel");
        deB1_2Fill.setSentenceWithBlank("Wir blieben in einem kleinen Hotel in der Nähe des ___.");
        deB1_2Fill.setCorrectAnswer("Strandes");
        deB1_2Fill.setXpReward(20);
        deB1_2Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(deB1_2Fill);

        ExerciseFillBlank deB1_3Fill = new ExerciseFillBlank();
        deB1_3Fill.setTargetLanguage(Language.DE);
        deB1_3Fill.setDifficultyLevel(LanguageLevel.B1);
        deB1_3Fill.setTopic("travel");
        deB1_3Fill.setSentenceWithBlank("Der Zug hatte wegen starken ___ Verspätung.");
        deB1_3Fill.setCorrectAnswer("Schnees");
        deB1_3Fill.setXpReward(20);
        deB1_3Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(deB1_3Fill);

        ExerciseFillBlank deB1_4Fill = new ExerciseFillBlank();
        deB1_4Fill.setTargetLanguage(Language.DE);
        deB1_4Fill.setDifficultyLevel(LanguageLevel.B1);
        deB1_4Fill.setTopic("experiences");
        deB1_4Fill.setSentenceWithBlank("Es war eine der ___ Reisen meines Lebens.");
        deB1_4Fill.setCorrectAnswer("aufregendsten");
        deB1_4Fill.setXpReward(20);
        deB1_4Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(deB1_4Fill);


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

        ExerciseMcq deB2_2Mcq = new ExerciseMcq();
        deB2_2Mcq.setTargetLanguage(Language.DE);
        deB2_2Mcq.setDifficultyLevel(LanguageLevel.B2);
        deB2_2Mcq.setTopic("opinions");
        deB2_2Mcq.setQuestionText("Viele Menschen glauben, dass Bildung der Schlüssel zum ___ ist.");
        deB2_2Mcq.setCorrectAnswer("Erfolg");
        deB2_2Mcq.setWrongOption1("erfolgreich");
        deB2_2Mcq.setWrongOption2("Erfolge");
        deB2_2Mcq.setWrongOption3("erfolgreiche");
        deB2_2Mcq.setXpReward(25);
        deB2_2Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(deB2_2Mcq);

        ExerciseMcq deB2_3Mcq = new ExerciseMcq();
        deB2_3Mcq.setTargetLanguage(Language.DE);
        deB2_3Mcq.setDifficultyLevel(LanguageLevel.B2);
        deB2_3Mcq.setTopic("opinions");
        deB2_3Mcq.setQuestionText("Oft wird argumentiert, dass soziale Medien einen großen Einfluss auf das Verhalten von Jugendlichen ___.");
        deB2_3Mcq.setCorrectAnswer("haben");
        deB2_3Mcq.setWrongOption1("hat");
        deB2_3Mcq.setWrongOption2("hätten");
        deB2_3Mcq.setWrongOption3("hatten");
        deB2_3Mcq.setXpReward(25);
        deB2_3Mcq.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(deB2_3Mcq);

        ExerciseMcq deB2_4Mcq = new ExerciseMcq();
        deB2_4Mcq.setTargetLanguage(Language.DE);
        deB2_4Mcq.setDifficultyLevel(LanguageLevel.B2);
        deB2_4Mcq.setTopic("abstract_concepts");
        deB2_4Mcq.setQuestionText("Eine starke Wirtschaft hängt von stabilen politischen ___ ab.");
        deB2_4Mcq.setCorrectAnswer("Verhältnissen");
        deB2_4Mcq.setWrongOption1("Verhalten");
        deB2_4Mcq.setWrongOption2("Verantwortungen");
        deB2_4Mcq.setWrongOption3("Verhandlungen");
        deB2_4Mcq.setXpReward(25);
        deB2_4Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(deB2_4Mcq);

        ExerciseFillBlank deB2Fill = new ExerciseFillBlank();
        deB2Fill.setTargetLanguage(Language.DE);
        deB2Fill.setDifficultyLevel(LanguageLevel.B2);
        deB2Fill.setTopic("abstract_concepts");
        deB2Fill.setSentenceWithBlank("Erfolg erfordert sowohl Talent als auch ___.");
        deB2Fill.setCorrectAnswer("Ausdauer");
        deB2Fill.setXpReward(25);
        deB2Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(deB2Fill);

        ExerciseFillBlank deB2_2Fill = new ExerciseFillBlank();
        deB2_2Fill.setTargetLanguage(Language.DE);
        deB2_2Fill.setDifficultyLevel(LanguageLevel.B2);
        deB2_2Fill.setTopic("opinions");
        deB2_2Fill.setSentenceWithBlank("Meiner Ansicht nach ist der Klimawandel eines der dringendsten ___ unserer Zeit.");
        deB2_2Fill.setCorrectAnswer("Probleme");
        deB2_2Fill.setXpReward(25);
        deB2_2Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(deB2_2Fill);

        ExerciseFillBlank deB2_3Fill = new ExerciseFillBlank();
        deB2_3Fill.setTargetLanguage(Language.DE);
        deB2_3Fill.setDifficultyLevel(LanguageLevel.B2);
        deB2_3Fill.setTopic("abstract_concepts");
        deB2_3Fill.setSentenceWithBlank("Kritisches Denken ist entscheidend, um ___ Entscheidungen zu treffen.");
        deB2_3Fill.setCorrectAnswer("informierte");
        deB2_3Fill.setXpReward(25);
        deB2_3Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(deB2_3Fill);

        ExerciseFillBlank deB2_4Fill = new ExerciseFillBlank();
        deB2_4Fill.setTargetLanguage(Language.DE);
        deB2_4Fill.setDifficultyLevel(LanguageLevel.B2);
        deB2_4Fill.setTopic("abstract_concepts");
        deB2_4Fill.setSentenceWithBlank("Wirtschaftliches Wachstum hängt oft von technischer ___ ab.");
        deB2_4Fill.setCorrectAnswer("Innovation");
        deB2_4Fill.setXpReward(25);
        deB2_4Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(deB2_4Fill);

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

        ExerciseMcq deC1_2Mcq = new ExerciseMcq();
        deC1_2Mcq.setTargetLanguage(Language.DE);
        deC1_2Mcq.setDifficultyLevel(LanguageLevel.C1);
        deC1_2Mcq.setTopic("complex_ideas");
        deC1_2Mcq.setQuestionText("Die Studie zielt darauf ab, die Beziehung zwischen Stress und Produktivität zu ___.");
        deC1_2Mcq.setCorrectAnswer("untersuchen");
        deC1_2Mcq.setWrongOption1("Untersuchung");
        deC1_2Mcq.setWrongOption2("untersucht");
        deC1_2Mcq.setWrongOption3("untersuchend");
        deC1_2Mcq.setXpReward(30);
        deC1_2Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(deC1_2Mcq);

        ExerciseMcq deC1_3Mcq = new ExerciseMcq();
        deC1_3Mcq.setTargetLanguage(Language.DE);
        deC1_3Mcq.setDifficultyLevel(LanguageLevel.C1);
        deC1_3Mcq.setTopic("complex_ideas");
        deC1_3Mcq.setQuestionText("Forscherinnen und Forscher stoßen oft auf Schwierigkeiten, wenn sie versuchen, genaue Daten zu ___.");
        deC1_3Mcq.setCorrectAnswer("erheben");
        deC1_3Mcq.setWrongOption1("Erhebung");
        deC1_3Mcq.setWrongOption2("erhob");
        deC1_3Mcq.setWrongOption3("erhoben");
        deC1_3Mcq.setXpReward(30);
        deC1_3Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(deC1_3Mcq);

        ExerciseMcq deC1_4Mcq = new ExerciseMcq();
        deC1_4Mcq.setTargetLanguage(Language.DE);
        deC1_4Mcq.setDifficultyLevel(LanguageLevel.C1);
        deC1_4Mcq.setTopic("nuanced_language");
        deC1_4Mcq.setQuestionText("Ihre Aussage war bewusst ___ formuliert, um Konflikte zu vermeiden.");
        deC1_4Mcq.setCorrectAnswer("vage");
        deC1_4Mcq.setWrongOption1("vager");
        deC1_4Mcq.setWrongOption2("Vagheit");
        deC1_4Mcq.setWrongOption3("vagee");
        deC1_4Mcq.setXpReward(30);
        deC1_4Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(deC1_4Mcq);

        ExerciseFillBlank deC1Fill = new ExerciseFillBlank();
        deC1Fill.setTargetLanguage(Language.DE);
        deC1Fill.setDifficultyLevel(LanguageLevel.C1);
        deC1Fill.setTopic("nuanced_language");
        deC1Fill.setSentenceWithBlank("Ihr Argument war trotz der gegenteiligen Beweise ___.");
        deC1Fill.setCorrectAnswer("überzeugend");
        deC1Fill.setXpReward(30);
        deC1Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(deC1Fill);

        ExerciseFillBlank deC1_2Fill = new ExerciseFillBlank();
        deC1_2Fill.setTargetLanguage(Language.DE);
        deC1_2Fill.setDifficultyLevel(LanguageLevel.C1);
        deC1_2Fill.setTopic("nuanced_language");
        deC1_2Fill.setSentenceWithBlank("Die Botschaft des Redners war absichtlich ___, um mehrere Deutungen zu ermöglichen.");
        deC1_2Fill.setCorrectAnswer("mehrdeutig");
        deC1_2Fill.setXpReward(30);
        deC1_2Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(deC1_2Fill);

        ExerciseFillBlank deC1_3Fill = new ExerciseFillBlank();
        deC1_3Fill.setTargetLanguage(Language.DE);
        deC1_3Fill.setDifficultyLevel(LanguageLevel.C1);
        deC1_3Fill.setTopic("complex_ideas");
        deC1_3Fill.setSentenceWithBlank("Die Ergebnisse waren überraschend und erforderten eine sorgfältige ___.");
        deC1_3Fill.setCorrectAnswer("Analyse");
        deC1_3Fill.setXpReward(30);
        deC1_3Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(deC1_3Fill);

        ExerciseFillBlank deC1_4Fill = new ExerciseFillBlank();
        deC1_4Fill.setTargetLanguage(Language.DE);
        deC1_4Fill.setDifficultyLevel(LanguageLevel.C1);
        deC1_4Fill.setTopic("complex_ideas");
        deC1_4Fill.setSentenceWithBlank("Ein umfassendes Verständnis des Problems erfordert einen ___ Ansatz.");
        deC1_4Fill.setCorrectAnswer("interdisziplinären");
        deC1_4Fill.setXpReward(30);
        deC1_4Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(deC1_4Fill);


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

        ExerciseMcq deC2_2Mcq = new ExerciseMcq();
        deC2_2Mcq.setTargetLanguage(Language.DE);
        deC2_2Mcq.setDifficultyLevel(LanguageLevel.C2);
        deC2_2Mcq.setTopic("idioms");
        deC2_2Mcq.setQuestionText("Welche Redewendung bedeutet „sofort, ohne Vorbereitung“?");
        deC2_2Mcq.setCorrectAnswer("aus dem Stegreif");
        deC2_2Mcq.setWrongOption1("aus dem Haus");
        deC2_2Mcq.setWrongOption2("aus der Ruhe");
        deC2_2Mcq.setWrongOption3("aus der Form");
        deC2_2Mcq.setXpReward(35);
        deC2_2Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(deC2_2Mcq);

        ExerciseMcq deC2_3Mcq = new ExerciseMcq();
        deC2_3Mcq.setTargetLanguage(Language.DE);
        deC2_3Mcq.setDifficultyLevel(LanguageLevel.C2);
        deC2_3Mcq.setTopic("idioms");
        deC2_3Mcq.setQuestionText("Welche Wendung benutzt man, um eine kurze Zusammenfassung anzukündigen?");
        deC2_3Mcq.setCorrectAnswer("in aller Kürze");
        deC2_3Mcq.setWrongOption1("in der Kürze");
        deC2_3Mcq.setWrongOption2("in der Länge");
        deC2_3Mcq.setWrongOption3("in aller Ruhe");
        deC2_3Mcq.setXpReward(35);
        deC2_3Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(deC2_3Mcq);

        ExerciseMcq deC2_4Mcq = new ExerciseMcq();
        deC2_4Mcq.setTargetLanguage(Language.DE);
        deC2_4Mcq.setDifficultyLevel(LanguageLevel.C2);
        deC2_4Mcq.setTopic("sophisticated_language");
        deC2_4Mcq.setQuestionText("Das Argument der Professorin war so ___, dass es kaum zu widerlegen war.");
        deC2_4Mcq.setCorrectAnswer("überzeugend");
        deC2_4Mcq.setWrongOption1("Überzeugung");
        deC2_4Mcq.setWrongOption2("überzeugt");
        deC2_4Mcq.setWrongOption3("überzeugender");
        deC2_4Mcq.setXpReward(35);
        deC2_4Mcq.setContentType(ExerciseContentType.VOCABULARY);
        mcqRepository.save(deC2_4Mcq);

        ExerciseFillBlank deC2Fill = new ExerciseFillBlank();
        deC2Fill.setTargetLanguage(Language.DE);
        deC2Fill.setDifficultyLevel(LanguageLevel.C2);
        deC2Fill.setTopic("sophisticated_language");
        deC2Fill.setSentenceWithBlank("Die Prosa des Autors zeichnete sich durch ihre ___ Eloquenz aus.");
        deC2Fill.setCorrectAnswer("unvergleichliche");
        deC2Fill.setXpReward(35);
        deC2Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(deC2Fill);

        ExerciseFillBlank deC2_2Fill = new ExerciseFillBlank();
        deC2_2Fill.setTargetLanguage(Language.DE);
        deC2_2Fill.setDifficultyLevel(LanguageLevel.C2);
        deC2_2Fill.setTopic("sophisticated_language");
        deC2_2Fill.setSentenceWithBlank("Seine Rede war so ___, dass das Publikum einen Moment lang schwieg.");
        deC2_2Fill.setCorrectAnswer("tiefgründig");
        deC2_2Fill.setXpReward(35);
        deC2_2Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(deC2_2Fill);

        ExerciseFillBlank deC2_3Fill = new ExerciseFillBlank();
        deC2_3Fill.setTargetLanguage(Language.DE);
        deC2_3Fill.setDifficultyLevel(LanguageLevel.C2);
        deC2_3Fill.setTopic("sophisticated_language");
        deC2_3Fill.setSentenceWithBlank("Der Bericht bietet eine sehr ___ Einschätzung der aktuellen Lage.");
        deC2_3Fill.setCorrectAnswer("nuancierte");
        deC2_3Fill.setXpReward(35);
        deC2_3Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(deC2_3Fill);

        ExerciseFillBlank deC2_4Fill = new ExerciseFillBlank();
        deC2_4Fill.setTargetLanguage(Language.DE);
        deC2_4Fill.setDifficultyLevel(LanguageLevel.C2);
        deC2_4Fill.setTopic("sophisticated_language");
        deC2_4Fill.setSentenceWithBlank("Selbst unter Druck antwortete sie mit ruhiger ___ und großer Sicherheit.");
        deC2_4Fill.setCorrectAnswer("Gelassenheit");
        deC2_4Fill.setXpReward(35);
        deC2_4Fill.setContentType(ExerciseContentType.VOCABULARY);
        fillBlankRepository.save(deC2_4Fill);

        logger.info("German exercises seeded: 24 MCQ + 24 Fill-Blank = 48 total");
    }
}