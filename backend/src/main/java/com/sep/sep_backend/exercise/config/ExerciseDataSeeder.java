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
        seedEnglishGrammarExercises();
        seedGermanGrammarExercises();

        long newMcqCount = mcqRepository.count();
        long newFillBlankCount = fillBlankRepository.count();

        logger.info("Exercise data seeding completed!");
        logger.info("Created {} MCQ exercises", newMcqCount);
        logger.info("Created {} Fill-in-the-Blank exercises", newFillBlankCount);
        logger.info("Total: {} exercises (92 English + 92 German)", newMcqCount + newFillBlankCount);
        logger.info("Total XP Available: 4080 (2040 EN + 2040 DE)");
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

    private void seedEnglishGrammarExercises() {
        logger.info("Seeding English GRAMMAR exercises...");

        // EN A1 MCQ GRAMMAR (4)
        ExerciseMcq enA1G1 = new ExerciseMcq();
        enA1G1.setTargetLanguage(Language.EN);
        enA1G1.setDifficultyLevel(LanguageLevel.A1);
        enA1G1.setTopic("articles");
        enA1G1.setQuestionText("I have ___ apple.");
        enA1G1.setCorrectAnswer("an");
        enA1G1.setWrongOption1("a");
        enA1G1.setWrongOption2("the");
        enA1G1.setWrongOption3("some");
        enA1G1.setXpReward(10);
        enA1G1.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(enA1G1);

        ExerciseMcq enA1G2 = new ExerciseMcq();
        enA1G2.setTargetLanguage(Language.EN);
        enA1G2.setDifficultyLevel(LanguageLevel.A1);
        enA1G2.setTopic("to_be");
        enA1G2.setQuestionText("She ___ a teacher.");
        enA1G2.setCorrectAnswer("is");
        enA1G2.setWrongOption1("are");
        enA1G2.setWrongOption2("am");
        enA1G2.setWrongOption3("be");
        enA1G2.setXpReward(10);
        enA1G2.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(enA1G2);

        ExerciseMcq enA1G3 = new ExerciseMcq();
        enA1G3.setTargetLanguage(Language.EN);
        enA1G3.setDifficultyLevel(LanguageLevel.A1);
        enA1G3.setTopic("pronouns");
        enA1G3.setQuestionText("___ am from Germany.");
        enA1G3.setCorrectAnswer("I");
        enA1G3.setWrongOption1("He");
        enA1G3.setWrongOption2("She");
        enA1G3.setWrongOption3("You");
        enA1G3.setXpReward(10);
        enA1G3.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(enA1G3);

        ExerciseMcq enA1G4 = new ExerciseMcq();
        enA1G4.setTargetLanguage(Language.EN);
        enA1G4.setDifficultyLevel(LanguageLevel.A1);
        enA1G4.setTopic("plurals");
        enA1G4.setQuestionText("There are three ___ on the table.");
        enA1G4.setCorrectAnswer("books");
        enA1G4.setWrongOption1("book");
        enA1G4.setWrongOption2("bookes");
        enA1G4.setWrongOption3("bookies");
        enA1G4.setXpReward(10);
        enA1G4.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(enA1G4);

        // EN A2 MCQ GRAMMAR (4)
        ExerciseMcq enA2G1 = new ExerciseMcq();
        enA2G1.setTargetLanguage(Language.EN);
        enA2G1.setDifficultyLevel(LanguageLevel.A2);
        enA2G1.setTopic("present_continuous");
        enA2G1.setQuestionText("Look! The children ___ in the park.");
        enA2G1.setCorrectAnswer("are playing");
        enA2G1.setWrongOption1("is playing");
        enA2G1.setWrongOption2("plays");
        enA2G1.setWrongOption3("play");
        enA2G1.setXpReward(15);
        enA2G1.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(enA2G1);

        ExerciseMcq enA2G2 = new ExerciseMcq();
        enA2G2.setTargetLanguage(Language.EN);
        enA2G2.setDifficultyLevel(LanguageLevel.A2);
        enA2G2.setTopic("past_simple");
        enA2G2.setQuestionText("Yesterday, I ___ to the cinema.");
        enA2G2.setCorrectAnswer("went");
        enA2G2.setWrongOption1("go");
        enA2G2.setWrongOption2("goes");
        enA2G2.setWrongOption3("going");
        enA2G2.setXpReward(15);
        enA2G2.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(enA2G2);

        ExerciseMcq enA2G3 = new ExerciseMcq();
        enA2G3.setTargetLanguage(Language.EN);
        enA2G3.setDifficultyLevel(LanguageLevel.A2);
        enA2G3.setTopic("possessives");
        enA2G3.setQuestionText("This is ___ car. It belongs to John.");
        enA2G3.setCorrectAnswer("his");
        enA2G3.setWrongOption1("her");
        enA2G3.setWrongOption2("its");
        enA2G3.setWrongOption3("their");
        enA2G3.setXpReward(15);
        enA2G3.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(enA2G3);

        ExerciseMcq enA2G4 = new ExerciseMcq();
        enA2G4.setTargetLanguage(Language.EN);
        enA2G4.setDifficultyLevel(LanguageLevel.A2);
        enA2G4.setTopic("comparatives");
        enA2G4.setQuestionText("My house is ___ than yours.");
        enA2G4.setCorrectAnswer("bigger");
        enA2G4.setWrongOption1("more big");
        enA2G4.setWrongOption2("big");
        enA2G4.setWrongOption3("biggest");
        enA2G4.setXpReward(15);
        enA2G4.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(enA2G4);

        // EN B1 MCQ GRAMMAR (4)
        ExerciseMcq enB1G1 = new ExerciseMcq();
        enB1G1.setTargetLanguage(Language.EN);
        enB1G1.setDifficultyLevel(LanguageLevel.B1);
        enB1G1.setTopic("present_perfect");
        enB1G1.setQuestionText("I have never ___ to Japan.");
        enB1G1.setCorrectAnswer("been");
        enB1G1.setWrongOption1("went");
        enB1G1.setWrongOption2("go");
        enB1G1.setWrongOption3("gone");
        enB1G1.setXpReward(20);
        enB1G1.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(enB1G1);

        ExerciseMcq enB1G2 = new ExerciseMcq();
        enB1G2.setTargetLanguage(Language.EN);
        enB1G2.setDifficultyLevel(LanguageLevel.B1);
        enB1G2.setTopic("past_perfect");
        enB1G2.setQuestionText("By the time I arrived, they had already ___.");
        enB1G2.setCorrectAnswer("left");
        enB1G2.setWrongOption1("leave");
        enB1G2.setWrongOption2("leaving");
        enB1G2.setWrongOption3("leaves");
        enB1G2.setXpReward(20);
        enB1G2.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(enB1G2);

        ExerciseMcq enB1G3 = new ExerciseMcq();
        enB1G3.setTargetLanguage(Language.EN);
        enB1G3.setDifficultyLevel(LanguageLevel.B1);
        enB1G3.setTopic("relative_clauses");
        enB1G3.setQuestionText("The man ___ called you is my uncle.");
        enB1G3.setCorrectAnswer("who");
        enB1G3.setWrongOption1("which");
        enB1G3.setWrongOption2("what");
        enB1G3.setWrongOption3("whom");
        enB1G3.setXpReward(20);
        enB1G3.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(enB1G3);

        ExerciseMcq enB1G4 = new ExerciseMcq();
        enB1G4.setTargetLanguage(Language.EN);
        enB1G4.setDifficultyLevel(LanguageLevel.B1);
        enB1G4.setTopic("modals");
        enB1G4.setQuestionText("You ___ see a doctor. You look ill.");
        enB1G4.setCorrectAnswer("should");
        enB1G4.setWrongOption1("would");
        enB1G4.setWrongOption2("could");
        enB1G4.setWrongOption3("might");
        enB1G4.setXpReward(20);
        enB1G4.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(enB1G4);

        // EN B2 MCQ GRAMMAR (4)
        ExerciseMcq enB2G1 = new ExerciseMcq();
        enB2G1.setTargetLanguage(Language.EN);
        enB2G1.setDifficultyLevel(LanguageLevel.B2);
        enB2G1.setTopic("conditionals");
        enB2G1.setQuestionText("If I ___ more money, I would travel the world.");
        enB2G1.setCorrectAnswer("had");
        enB2G1.setWrongOption1("have");
        enB2G1.setWrongOption2("will have");
        enB2G1.setWrongOption3("had had");
        enB2G1.setXpReward(25);
        enB2G1.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(enB2G1);

        ExerciseMcq enB2G2 = new ExerciseMcq();
        enB2G2.setTargetLanguage(Language.EN);
        enB2G2.setDifficultyLevel(LanguageLevel.B2);
        enB2G2.setTopic("passive_voice");
        enB2G2.setQuestionText("The letter ___ by the secretary yesterday.");
        enB2G2.setCorrectAnswer("was written");
        enB2G2.setWrongOption1("is written");
        enB2G2.setWrongOption2("wrote");
        enB2G2.setWrongOption3("has written");
        enB2G2.setXpReward(25);
        enB2G2.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(enB2G2);

        ExerciseMcq enB2G3 = new ExerciseMcq();
        enB2G3.setTargetLanguage(Language.EN);
        enB2G3.setDifficultyLevel(LanguageLevel.B2);
        enB2G3.setTopic("reported_speech");
        enB2G3.setQuestionText("She said that she ___ tired.");
        enB2G3.setCorrectAnswer("was");
        enB2G3.setWrongOption1("is");
        enB2G3.setWrongOption2("be");
        enB2G3.setWrongOption3("were");
        enB2G3.setXpReward(25);
        enB2G3.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(enB2G3);

        ExerciseMcq enB2G4 = new ExerciseMcq();
        enB2G4.setTargetLanguage(Language.EN);
        enB2G4.setDifficultyLevel(LanguageLevel.B2);
        enB2G4.setTopic("wish_clauses");
        enB2G4.setQuestionText("I wish I ___ speak French fluently.");
        enB2G4.setCorrectAnswer("could");
        enB2G4.setWrongOption1("can");
        enB2G4.setWrongOption2("would");
        enB2G4.setWrongOption3("will");
        enB2G4.setXpReward(25);
        enB2G4.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(enB2G4);

        // EN C1 MCQ GRAMMAR (3)
        ExerciseMcq enC1G1 = new ExerciseMcq();
        enC1G1.setTargetLanguage(Language.EN);
        enC1G1.setDifficultyLevel(LanguageLevel.C1);
        enC1G1.setTopic("subjunctive");
        enC1G1.setQuestionText("The manager insisted that he ___ present at the meeting.");
        enC1G1.setCorrectAnswer("be");
        enC1G1.setWrongOption1("is");
        enC1G1.setWrongOption2("was");
        enC1G1.setWrongOption3("were");
        enC1G1.setXpReward(30);
        enC1G1.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(enC1G1);

        ExerciseMcq enC1G2 = new ExerciseMcq();
        enC1G2.setTargetLanguage(Language.EN);
        enC1G2.setDifficultyLevel(LanguageLevel.C1);
        enC1G2.setTopic("inversion");
        enC1G2.setQuestionText("Not only ___ he late, but he also forgot the documents.");
        enC1G2.setCorrectAnswer("was");
        enC1G2.setWrongOption1("is");
        enC1G2.setWrongOption2("did");
        enC1G2.setWrongOption3("had");
        enC1G2.setXpReward(30);
        enC1G2.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(enC1G2);

        ExerciseMcq enC1G3 = new ExerciseMcq();
        enC1G3.setTargetLanguage(Language.EN);
        enC1G3.setDifficultyLevel(LanguageLevel.C1);
        enC1G3.setTopic("cleft_sentences");
        enC1G3.setQuestionText("It ___ John who broke the window.");
        enC1G3.setCorrectAnswer("was");
        enC1G3.setWrongOption1("is");
        enC1G3.setWrongOption2("were");
        enC1G3.setWrongOption3("has");
        enC1G3.setXpReward(30);
        enC1G3.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(enC1G3);

        // EN C2 MCQ GRAMMAR (3)
        ExerciseMcq enC2G1 = new ExerciseMcq();
        enC2G1.setTargetLanguage(Language.EN);
        enC2G1.setDifficultyLevel(LanguageLevel.C2);
        enC2G1.setTopic("mixed_conditionals");
        enC2G1.setQuestionText("If she had studied harder, she ___ a better job now.");
        enC2G1.setCorrectAnswer("would have");
        enC2G1.setWrongOption1("will have");
        enC2G1.setWrongOption2("would has");
        enC2G1.setWrongOption3("had");
        enC2G1.setXpReward(35);
        enC2G1.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(enC2G1);

        ExerciseMcq enC2G2 = new ExerciseMcq();
        enC2G2.setTargetLanguage(Language.EN);
        enC2G2.setDifficultyLevel(LanguageLevel.C2);
        enC2G2.setTopic("ellipsis");
        enC2G2.setQuestionText("She can speak French and so ___ I.");
        enC2G2.setCorrectAnswer("can");
        enC2G2.setWrongOption1("do");
        enC2G2.setWrongOption2("am");
        enC2G2.setWrongOption3("have");
        enC2G2.setXpReward(35);
        enC2G2.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(enC2G2);

        ExerciseMcq enC2G3 = new ExerciseMcq();
        enC2G3.setTargetLanguage(Language.EN);
        enC2G3.setDifficultyLevel(LanguageLevel.C2);
        enC2G3.setTopic("nominalization");
        enC2G3.setQuestionText("The ___ of the project took three months.");
        enC2G3.setCorrectAnswer("completion");
        enC2G3.setWrongOption1("complete");
        enC2G3.setWrongOption2("completing");
        enC2G3.setWrongOption3("completed");
        enC2G3.setXpReward(35);
        enC2G3.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(enC2G3);

        // EN A1 Fill-blank GRAMMAR (4)
        ExerciseFillBlank enA1GF1 = new ExerciseFillBlank();
        enA1GF1.setTargetLanguage(Language.EN);
        enA1GF1.setDifficultyLevel(LanguageLevel.A1);
        enA1GF1.setTopic("to_be");
        enA1GF1.setSentenceWithBlank("I ___ a student.");
        enA1GF1.setCorrectAnswer("am");
        enA1GF1.setXpReward(10);
        enA1GF1.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(enA1GF1);

        ExerciseFillBlank enA1GF2 = new ExerciseFillBlank();
        enA1GF2.setTargetLanguage(Language.EN);
        enA1GF2.setDifficultyLevel(LanguageLevel.A1);
        enA1GF2.setTopic("to_be");
        enA1GF2.setSentenceWithBlank("They ___ my friends.");
        enA1GF2.setCorrectAnswer("are");
        enA1GF2.setXpReward(10);
        enA1GF2.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(enA1GF2);

        ExerciseFillBlank enA1GF3 = new ExerciseFillBlank();
        enA1GF3.setTargetLanguage(Language.EN);
        enA1GF3.setDifficultyLevel(LanguageLevel.A1);
        enA1GF3.setTopic("articles");
        enA1GF3.setSentenceWithBlank("She has ___ cat.");
        enA1GF3.setCorrectAnswer("a");
        enA1GF3.setXpReward(10);
        enA1GF3.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(enA1GF3);

        ExerciseFillBlank enA1GF4 = new ExerciseFillBlank();
        enA1GF4.setTargetLanguage(Language.EN);
        enA1GF4.setDifficultyLevel(LanguageLevel.A1);
        enA1GF4.setTopic("pronouns");
        enA1GF4.setSentenceWithBlank("___ is my brother Tom.");
        enA1GF4.setCorrectAnswer("This");
        enA1GF4.setXpReward(10);
        enA1GF4.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(enA1GF4);

        // EN A2 Fill-blank GRAMMAR (4)
        ExerciseFillBlank enA2GF1 = new ExerciseFillBlank();
        enA2GF1.setTargetLanguage(Language.EN);
        enA2GF1.setDifficultyLevel(LanguageLevel.A2);
        enA2GF1.setTopic("past_simple");
        enA2GF1.setSentenceWithBlank("She ___ to school yesterday.");
        enA2GF1.setCorrectAnswer("went");
        enA2GF1.setXpReward(15);
        enA2GF1.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(enA2GF1);

        ExerciseFillBlank enA2GF2 = new ExerciseFillBlank();
        enA2GF2.setTargetLanguage(Language.EN);
        enA2GF2.setDifficultyLevel(LanguageLevel.A2);
        enA2GF2.setTopic("possessives");
        enA2GF2.setSentenceWithBlank("This book is mine. That book is ___.");
        enA2GF2.setCorrectAnswer("yours");
        enA2GF2.setXpReward(15);
        enA2GF2.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(enA2GF2);

        ExerciseFillBlank enA2GF3 = new ExerciseFillBlank();
        enA2GF3.setTargetLanguage(Language.EN);
        enA2GF3.setDifficultyLevel(LanguageLevel.A2);
        enA2GF3.setTopic("present_continuous");
        enA2GF3.setSentenceWithBlank("She ___ cooking dinner right now.");
        enA2GF3.setCorrectAnswer("is");
        enA2GF3.setXpReward(15);
        enA2GF3.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(enA2GF3);

        ExerciseFillBlank enA2GF4 = new ExerciseFillBlank();
        enA2GF4.setTargetLanguage(Language.EN);
        enA2GF4.setDifficultyLevel(LanguageLevel.A2);
        enA2GF4.setTopic("prepositions");
        enA2GF4.setSentenceWithBlank("The book is ___ the table.");
        enA2GF4.setCorrectAnswer("on");
        enA2GF4.setXpReward(15);
        enA2GF4.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(enA2GF4);

        // EN B1 Fill-blank GRAMMAR (4)
        ExerciseFillBlank enB1GF1 = new ExerciseFillBlank();
        enB1GF1.setTargetLanguage(Language.EN);
        enB1GF1.setDifficultyLevel(LanguageLevel.B1);
        enB1GF1.setTopic("present_perfect");
        enB1GF1.setSentenceWithBlank("I have ___ finished my homework.");
        enB1GF1.setCorrectAnswer("just");
        enB1GF1.setXpReward(20);
        enB1GF1.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(enB1GF1);

        ExerciseFillBlank enB1GF2 = new ExerciseFillBlank();
        enB1GF2.setTargetLanguage(Language.EN);
        enB1GF2.setDifficultyLevel(LanguageLevel.B1);
        enB1GF2.setTopic("relative_clauses");
        enB1GF2.setSentenceWithBlank("The woman ___ lives next door is a doctor.");
        enB1GF2.setCorrectAnswer("who");
        enB1GF2.setXpReward(20);
        enB1GF2.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(enB1GF2);

        ExerciseFillBlank enB1GF3 = new ExerciseFillBlank();
        enB1GF3.setTargetLanguage(Language.EN);
        enB1GF3.setDifficultyLevel(LanguageLevel.B1);
        enB1GF3.setTopic("modals");
        enB1GF3.setSentenceWithBlank("You ___ wear a seatbelt. It is the law.");
        enB1GF3.setCorrectAnswer("must");
        enB1GF3.setXpReward(20);
        enB1GF3.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(enB1GF3);

        ExerciseFillBlank enB1GF4 = new ExerciseFillBlank();
        enB1GF4.setTargetLanguage(Language.EN);
        enB1GF4.setDifficultyLevel(LanguageLevel.B1);
        enB1GF4.setTopic("connectors");
        enB1GF4.setSentenceWithBlank("I stayed home ___ I was feeling sick.");
        enB1GF4.setCorrectAnswer("because");
        enB1GF4.setXpReward(20);
        enB1GF4.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(enB1GF4);

        // EN B2 Fill-blank GRAMMAR (4)
        ExerciseFillBlank enB2GF1 = new ExerciseFillBlank();
        enB2GF1.setTargetLanguage(Language.EN);
        enB2GF1.setDifficultyLevel(LanguageLevel.B2);
        enB2GF1.setTopic("passive_voice");
        enB2GF1.setSentenceWithBlank("The report ___ written by the team last week.");
        enB2GF1.setCorrectAnswer("was");
        enB2GF1.setXpReward(25);
        enB2GF1.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(enB2GF1);

        ExerciseFillBlank enB2GF2 = new ExerciseFillBlank();
        enB2GF2.setTargetLanguage(Language.EN);
        enB2GF2.setDifficultyLevel(LanguageLevel.B2);
        enB2GF2.setTopic("conditionals");
        enB2GF2.setSentenceWithBlank("If I were you, I ___ accept the offer.");
        enB2GF2.setCorrectAnswer("would");
        enB2GF2.setXpReward(25);
        enB2GF2.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(enB2GF2);

        ExerciseFillBlank enB2GF3 = new ExerciseFillBlank();
        enB2GF3.setTargetLanguage(Language.EN);
        enB2GF3.setDifficultyLevel(LanguageLevel.B2);
        enB2GF3.setTopic("reported_speech");
        enB2GF3.setSentenceWithBlank("He told me that he ___ busy the day before.");
        enB2GF3.setCorrectAnswer("had been");
        enB2GF3.setXpReward(25);
        enB2GF3.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(enB2GF3);

        ExerciseFillBlank enB2GF4 = new ExerciseFillBlank();
        enB2GF4.setTargetLanguage(Language.EN);
        enB2GF4.setDifficultyLevel(LanguageLevel.B2);
        enB2GF4.setTopic("gerunds_infinitives");
        enB2GF4.setSentenceWithBlank("She suggested ___ to the beach.");
        enB2GF4.setCorrectAnswer("going");
        enB2GF4.setXpReward(25);
        enB2GF4.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(enB2GF4);

        // EN C1 Fill-blank GRAMMAR (3)
        ExerciseFillBlank enC1GF1 = new ExerciseFillBlank();
        enC1GF1.setTargetLanguage(Language.EN);
        enC1GF1.setDifficultyLevel(LanguageLevel.C1);
        enC1GF1.setTopic("inversion");
        enC1GF1.setSentenceWithBlank("Never ___ I seen such a beautiful sunset.");
        enC1GF1.setCorrectAnswer("have");
        enC1GF1.setXpReward(30);
        enC1GF1.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(enC1GF1);

        ExerciseFillBlank enC1GF2 = new ExerciseFillBlank();
        enC1GF2.setTargetLanguage(Language.EN);
        enC1GF2.setDifficultyLevel(LanguageLevel.C1);
        enC1GF2.setTopic("participle_clauses");
        enC1GF2.setSentenceWithBlank("___ finished the exam, she left the room.");
        enC1GF2.setCorrectAnswer("Having");
        enC1GF2.setXpReward(30);
        enC1GF2.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(enC1GF2);

        ExerciseFillBlank enC1GF3 = new ExerciseFillBlank();
        enC1GF3.setTargetLanguage(Language.EN);
        enC1GF3.setDifficultyLevel(LanguageLevel.C1);
        enC1GF3.setTopic("subjunctive");
        enC1GF3.setSentenceWithBlank("It is essential that she ___ on time.");
        enC1GF3.setCorrectAnswer("be");
        enC1GF3.setXpReward(30);
        enC1GF3.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(enC1GF3);

        // EN C2 Fill-blank GRAMMAR (3)
        ExerciseFillBlank enC2GF1 = new ExerciseFillBlank();
        enC2GF1.setTargetLanguage(Language.EN);
        enC2GF1.setDifficultyLevel(LanguageLevel.C2);
        enC2GF1.setTopic("ellipsis");
        enC2GF1.setSentenceWithBlank("She wanted to help, but she could ___.");
        enC2GF1.setCorrectAnswer("not");
        enC2GF1.setXpReward(35);
        enC2GF1.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(enC2GF1);

        ExerciseFillBlank enC2GF2 = new ExerciseFillBlank();
        enC2GF2.setTargetLanguage(Language.EN);
        enC2GF2.setDifficultyLevel(LanguageLevel.C2);
        enC2GF2.setTopic("fronting");
        enC2GF2.setSentenceWithBlank("Strange ___ it may seem, he was right.");
        enC2GF2.setCorrectAnswer("as");
        enC2GF2.setXpReward(35);
        enC2GF2.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(enC2GF2);

        ExerciseFillBlank enC2GF3 = new ExerciseFillBlank();
        enC2GF3.setTargetLanguage(Language.EN);
        enC2GF3.setDifficultyLevel(LanguageLevel.C2);
        enC2GF3.setTopic("emphasis");
        enC2GF3.setSentenceWithBlank("What I need ___ a good rest.");
        enC2GF3.setCorrectAnswer("is");
        enC2GF3.setXpReward(35);
        enC2GF3.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(enC2GF3);

        logger.info("English GRAMMAR exercises seeded: 22 MCQ + 22 Fill-Blank = 44 total");
    }

    private void seedGermanGrammarExercises() {
        logger.info("Seeding German GRAMMAR exercises...");

        // DE A1 MCQ GRAMMAR (4)
        ExerciseMcq deA1G1 = new ExerciseMcq();
        deA1G1.setTargetLanguage(Language.DE);
        deA1G1.setDifficultyLevel(LanguageLevel.A1);
        deA1G1.setTopic("artikel");
        deA1G1.setQuestionText("Das ist ___ Buch.");
        deA1G1.setCorrectAnswer("ein");
        deA1G1.setWrongOption1("eine");
        deA1G1.setWrongOption2("einen");
        deA1G1.setWrongOption3("einer");
        deA1G1.setXpReward(10);
        deA1G1.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(deA1G1);

        ExerciseMcq deA1G2 = new ExerciseMcq();
        deA1G2.setTargetLanguage(Language.DE);
        deA1G2.setDifficultyLevel(LanguageLevel.A1);
        deA1G2.setTopic("verben");
        deA1G2.setQuestionText("Ich ___ Deutsch.");
        deA1G2.setCorrectAnswer("lerne");
        deA1G2.setWrongOption1("lernst");
        deA1G2.setWrongOption2("lernt");
        deA1G2.setWrongOption3("lernen");
        deA1G2.setXpReward(10);
        deA1G2.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(deA1G2);

        ExerciseMcq deA1G3 = new ExerciseMcq();
        deA1G3.setTargetLanguage(Language.DE);
        deA1G3.setDifficultyLevel(LanguageLevel.A1);
        deA1G3.setTopic("sein");
        deA1G3.setQuestionText("Wir ___ aus Deutschland.");
        deA1G3.setCorrectAnswer("sind");
        deA1G3.setWrongOption1("ist");
        deA1G3.setWrongOption2("bin");
        deA1G3.setWrongOption3("seid");
        deA1G3.setXpReward(10);
        deA1G3.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(deA1G3);

        ExerciseMcq deA1G4 = new ExerciseMcq();
        deA1G4.setTargetLanguage(Language.DE);
        deA1G4.setDifficultyLevel(LanguageLevel.A1);
        deA1G4.setTopic("haben");
        deA1G4.setQuestionText("Er ___ einen Hund.");
        deA1G4.setCorrectAnswer("hat");
        deA1G4.setWrongOption1("habe");
        deA1G4.setWrongOption2("hast");
        deA1G4.setWrongOption3("haben");
        deA1G4.setXpReward(10);
        deA1G4.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(deA1G4);

        // DE A2 MCQ GRAMMAR (4)
        ExerciseMcq deA2G1 = new ExerciseMcq();
        deA2G1.setTargetLanguage(Language.DE);
        deA2G1.setDifficultyLevel(LanguageLevel.A2);
        deA2G1.setTopic("akkusativ");
        deA2G1.setQuestionText("Ich sehe ___ Mann.");
        deA2G1.setCorrectAnswer("den");
        deA2G1.setWrongOption1("der");
        deA2G1.setWrongOption2("dem");
        deA2G1.setWrongOption3("des");
        deA2G1.setXpReward(15);
        deA2G1.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(deA2G1);

        ExerciseMcq deA2G2 = new ExerciseMcq();
        deA2G2.setTargetLanguage(Language.DE);
        deA2G2.setDifficultyLevel(LanguageLevel.A2);
        deA2G2.setTopic("modalverben");
        deA2G2.setQuestionText("Ich ___ Deutsch sprechen.");
        deA2G2.setCorrectAnswer("kann");
        deA2G2.setWrongOption1("können");
        deA2G2.setWrongOption2("kannst");
        deA2G2.setWrongOption3("konnte");
        deA2G2.setXpReward(15);
        deA2G2.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(deA2G2);

        ExerciseMcq deA2G3 = new ExerciseMcq();
        deA2G3.setTargetLanguage(Language.DE);
        deA2G3.setDifficultyLevel(LanguageLevel.A2);
        deA2G3.setTopic("perfekt");
        deA2G3.setQuestionText("Ich habe gestern einen Film ___.");
        deA2G3.setCorrectAnswer("gesehen");
        deA2G3.setWrongOption1("sehen");
        deA2G3.setWrongOption2("sah");
        deA2G3.setWrongOption3("geseht");
        deA2G3.setXpReward(15);
        deA2G3.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(deA2G3);

        ExerciseMcq deA2G4 = new ExerciseMcq();
        deA2G4.setTargetLanguage(Language.DE);
        deA2G4.setDifficultyLevel(LanguageLevel.A2);
        deA2G4.setTopic("trennbare_verben");
        deA2G4.setQuestionText("Ich stehe jeden Tag um 7 Uhr ___.");
        deA2G4.setCorrectAnswer("auf");
        deA2G4.setWrongOption1("an");
        deA2G4.setWrongOption2("ab");
        deA2G4.setWrongOption3("ein");
        deA2G4.setXpReward(15);
        deA2G4.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(deA2G4);

        // DE B1 MCQ GRAMMAR (4)
        ExerciseMcq deB1G1 = new ExerciseMcq();
        deB1G1.setTargetLanguage(Language.DE);
        deB1G1.setDifficultyLevel(LanguageLevel.B1);
        deB1G1.setTopic("dativ");
        deB1G1.setQuestionText("Ich gebe ___ Frau das Geschenk.");
        deB1G1.setCorrectAnswer("der");
        deB1G1.setWrongOption1("die");
        deB1G1.setWrongOption2("den");
        deB1G1.setWrongOption3("das");
        deB1G1.setXpReward(20);
        deB1G1.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(deB1G1);

        ExerciseMcq deB1G2 = new ExerciseMcq();
        deB1G2.setTargetLanguage(Language.DE);
        deB1G2.setDifficultyLevel(LanguageLevel.B1);
        deB1G2.setTopic("nebensaetze");
        deB1G2.setQuestionText("Ich bleibe zu Hause, ___ es regnet.");
        deB1G2.setCorrectAnswer("weil");
        deB1G2.setWrongOption1("denn");
        deB1G2.setWrongOption2("aber");
        deB1G2.setWrongOption3("und");
        deB1G2.setXpReward(20);
        deB1G2.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(deB1G2);

        ExerciseMcq deB1G3 = new ExerciseMcq();
        deB1G3.setTargetLanguage(Language.DE);
        deB1G3.setDifficultyLevel(LanguageLevel.B1);
        deB1G3.setTopic("relativsaetze");
        deB1G3.setQuestionText("Der Mann, ___ dort steht, ist mein Vater.");
        deB1G3.setCorrectAnswer("der");
        deB1G3.setWrongOption1("den");
        deB1G3.setWrongOption2("dem");
        deB1G3.setWrongOption3("dessen");
        deB1G3.setXpReward(20);
        deB1G3.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(deB1G3);

        ExerciseMcq deB1G4 = new ExerciseMcq();
        deB1G4.setTargetLanguage(Language.DE);
        deB1G4.setDifficultyLevel(LanguageLevel.B1);
        deB1G4.setTopic("praeteritum");
        deB1G4.setQuestionText("Als ich jung ___, spielte ich oft Fußball.");
        deB1G4.setCorrectAnswer("war");
        deB1G4.setWrongOption1("bin");
        deB1G4.setWrongOption2("sei");
        deB1G4.setWrongOption3("wäre");
        deB1G4.setXpReward(20);
        deB1G4.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(deB1G4);

        // DE B2 MCQ GRAMMAR (4)
        ExerciseMcq deB2G1 = new ExerciseMcq();
        deB2G1.setTargetLanguage(Language.DE);
        deB2G1.setDifficultyLevel(LanguageLevel.B2);
        deB2G1.setTopic("konjunktiv_ii");
        deB2G1.setQuestionText("Wenn ich reich ___, würde ich reisen.");
        deB2G1.setCorrectAnswer("wäre");
        deB2G1.setWrongOption1("bin");
        deB2G1.setWrongOption2("war");
        deB2G1.setWrongOption3("sei");
        deB2G1.setXpReward(25);
        deB2G1.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(deB2G1);

        ExerciseMcq deB2G2 = new ExerciseMcq();
        deB2G2.setTargetLanguage(Language.DE);
        deB2G2.setDifficultyLevel(LanguageLevel.B2);
        deB2G2.setTopic("passiv");
        deB2G2.setQuestionText("Das Haus ___ letztes Jahr gebaut.");
        deB2G2.setCorrectAnswer("wurde");
        deB2G2.setWrongOption1("wird");
        deB2G2.setWrongOption2("war");
        deB2G2.setWrongOption3("worden");
        deB2G2.setXpReward(25);
        deB2G2.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(deB2G2);

        ExerciseMcq deB2G3 = new ExerciseMcq();
        deB2G3.setTargetLanguage(Language.DE);
        deB2G3.setDifficultyLevel(LanguageLevel.B2);
        deB2G3.setTopic("indirekte_rede");
        deB2G3.setQuestionText("Er sagte, er ___ keine Zeit.");
        deB2G3.setCorrectAnswer("habe");
        deB2G3.setWrongOption1("hat");
        deB2G3.setWrongOption2("hätte");
        deB2G3.setWrongOption3("haben");
        deB2G3.setXpReward(25);
        deB2G3.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(deB2G3);

        ExerciseMcq deB2G4 = new ExerciseMcq();
        deB2G4.setTargetLanguage(Language.DE);
        deB2G4.setDifficultyLevel(LanguageLevel.B2);
        deB2G4.setTopic("infinitiv_mit_zu");
        deB2G4.setQuestionText("Es ist wichtig, pünktlich ___ sein.");
        deB2G4.setCorrectAnswer("zu");
        deB2G4.setWrongOption1("um");
        deB2G4.setWrongOption2("für");
        deB2G4.setWrongOption3("-");
        deB2G4.setXpReward(25);
        deB2G4.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(deB2G4);

        // DE C1 MCQ GRAMMAR (3)
        ExerciseMcq deC1G1 = new ExerciseMcq();
        deC1G1.setTargetLanguage(Language.DE);
        deC1G1.setDifficultyLevel(LanguageLevel.C1);
        deC1G1.setTopic("genitiv");
        deC1G1.setQuestionText("Das ist das Auto ___ Mannes.");
        deC1G1.setCorrectAnswer("des");
        deC1G1.setWrongOption1("dem");
        deC1G1.setWrongOption2("den");
        deC1G1.setWrongOption3("der");
        deC1G1.setXpReward(30);
        deC1G1.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(deC1G1);

        ExerciseMcq deC1G2 = new ExerciseMcq();
        deC1G2.setTargetLanguage(Language.DE);
        deC1G2.setDifficultyLevel(LanguageLevel.C1);
        deC1G2.setTopic("partizipial");
        deC1G2.setQuestionText("Die ___ Arbeit wurde anerkannt.");
        deC1G2.setCorrectAnswer("geleistete");
        deC1G2.setWrongOption1("leistende");
        deC1G2.setWrongOption2("geleistet");
        deC1G2.setWrongOption3("leisten");
        deC1G2.setXpReward(30);
        deC1G2.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(deC1G2);

        ExerciseMcq deC1G3 = new ExerciseMcq();
        deC1G3.setTargetLanguage(Language.DE);
        deC1G3.setDifficultyLevel(LanguageLevel.C1);
        deC1G3.setTopic("nominalisierung");
        deC1G3.setQuestionText("Das ___ der Aufgabe dauerte lange.");
        deC1G3.setCorrectAnswer("Lösen");
        deC1G3.setWrongOption1("Lösung");
        deC1G3.setWrongOption2("Gelöst");
        deC1G3.setWrongOption3("Lösend");
        deC1G3.setXpReward(30);
        deC1G3.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(deC1G3);

        // DE C2 MCQ GRAMMAR (3)
        ExerciseMcq deC2G1 = new ExerciseMcq();
        deC2G1.setTargetLanguage(Language.DE);
        deC2G1.setDifficultyLevel(LanguageLevel.C2);
        deC2G1.setTopic("konjunktiv_i");
        deC2G1.setQuestionText("Er behauptete, er ___ unschuldig.");
        deC2G1.setCorrectAnswer("sei");
        deC2G1.setWrongOption1("ist");
        deC2G1.setWrongOption2("wäre");
        deC2G1.setWrongOption3("war");
        deC2G1.setXpReward(35);
        deC2G1.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(deC2G1);

        ExerciseMcq deC2G2 = new ExerciseMcq();
        deC2G2.setTargetLanguage(Language.DE);
        deC2G2.setDifficultyLevel(LanguageLevel.C2);
        deC2G2.setTopic("erweitertes_partizip");
        deC2G2.setQuestionText("Der ___ Mann ist mein Kollege.");
        deC2G2.setCorrectAnswer("dort stehende");
        deC2G2.setWrongOption1("dort steht");
        deC2G2.setWrongOption2("dort gestanden");
        deC2G2.setWrongOption3("dort stehen");
        deC2G2.setXpReward(35);
        deC2G2.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(deC2G2);

        ExerciseMcq deC2G3 = new ExerciseMcq();
        deC2G3.setTargetLanguage(Language.DE);
        deC2G3.setDifficultyLevel(LanguageLevel.C2);
        deC2G3.setTopic("nomen_verb_verbindungen");
        deC2G3.setQuestionText("Er hat eine Entscheidung ___.");
        deC2G3.setCorrectAnswer("getroffen");
        deC2G3.setWrongOption1("gemacht");
        deC2G3.setWrongOption2("genommen");
        deC2G3.setWrongOption3("gegeben");
        deC2G3.setXpReward(35);
        deC2G3.setContentType(ExerciseContentType.GRAMMAR);
        mcqRepository.save(deC2G3);

        // DE A1 Fill-blank GRAMMAR (4)
        ExerciseFillBlank deA1GF1 = new ExerciseFillBlank();
        deA1GF1.setTargetLanguage(Language.DE);
        deA1GF1.setDifficultyLevel(LanguageLevel.A1);
        deA1GF1.setTopic("sein");
        deA1GF1.setSentenceWithBlank("Ich ___ Student.");
        deA1GF1.setCorrectAnswer("bin");
        deA1GF1.setXpReward(10);
        deA1GF1.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(deA1GF1);

        ExerciseFillBlank deA1GF2 = new ExerciseFillBlank();
        deA1GF2.setTargetLanguage(Language.DE);
        deA1GF2.setDifficultyLevel(LanguageLevel.A1);
        deA1GF2.setTopic("haben");
        deA1GF2.setSentenceWithBlank("Er ___ zwei Kinder.");
        deA1GF2.setCorrectAnswer("hat");
        deA1GF2.setXpReward(10);
        deA1GF2.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(deA1GF2);

        ExerciseFillBlank deA1GF3 = new ExerciseFillBlank();
        deA1GF3.setTargetLanguage(Language.DE);
        deA1GF3.setDifficultyLevel(LanguageLevel.A1);
        deA1GF3.setTopic("artikel");
        deA1GF3.setSentenceWithBlank("___ Apfel ist rot.");
        deA1GF3.setCorrectAnswer("Der");
        deA1GF3.setXpReward(10);
        deA1GF3.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(deA1GF3);

        ExerciseFillBlank deA1GF4 = new ExerciseFillBlank();
        deA1GF4.setTargetLanguage(Language.DE);
        deA1GF4.setDifficultyLevel(LanguageLevel.A1);
        deA1GF4.setTopic("verben");
        deA1GF4.setSentenceWithBlank("Du ___ sehr gut Deutsch.");
        deA1GF4.setCorrectAnswer("sprichst");
        deA1GF4.setXpReward(10);
        deA1GF4.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(deA1GF4);

        // DE A2 Fill-blank GRAMMAR (4)
        ExerciseFillBlank deA2GF1 = new ExerciseFillBlank();
        deA2GF1.setTargetLanguage(Language.DE);
        deA2GF1.setDifficultyLevel(LanguageLevel.A2);
        deA2GF1.setTopic("akkusativ");
        deA2GF1.setSentenceWithBlank("Ich kaufe ___ Buch.");
        deA2GF1.setCorrectAnswer("ein");
        deA2GF1.setXpReward(15);
        deA2GF1.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(deA2GF1);

        ExerciseFillBlank deA2GF2 = new ExerciseFillBlank();
        deA2GF2.setTargetLanguage(Language.DE);
        deA2GF2.setDifficultyLevel(LanguageLevel.A2);
        deA2GF2.setTopic("trennbare_verben");
        deA2GF2.setSentenceWithBlank("Ich stehe jeden Morgen um 7 Uhr ___.");
        deA2GF2.setCorrectAnswer("auf");
        deA2GF2.setXpReward(15);
        deA2GF2.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(deA2GF2);

        ExerciseFillBlank deA2GF3 = new ExerciseFillBlank();
        deA2GF3.setTargetLanguage(Language.DE);
        deA2GF3.setDifficultyLevel(LanguageLevel.A2);
        deA2GF3.setTopic("perfekt");
        deA2GF3.setSentenceWithBlank("Ich habe einen Kuchen ___.");
        deA2GF3.setCorrectAnswer("gebacken");
        deA2GF3.setXpReward(15);
        deA2GF3.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(deA2GF3);

        ExerciseFillBlank deA2GF4 = new ExerciseFillBlank();
        deA2GF4.setTargetLanguage(Language.DE);
        deA2GF4.setDifficultyLevel(LanguageLevel.A2);
        deA2GF4.setTopic("praepositionen");
        deA2GF4.setSentenceWithBlank("Das Buch liegt ___ dem Tisch.");
        deA2GF4.setCorrectAnswer("auf");
        deA2GF4.setXpReward(15);
        deA2GF4.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(deA2GF4);

        // DE B1 Fill-blank GRAMMAR (4)
        ExerciseFillBlank deB1GF1 = new ExerciseFillBlank();
        deB1GF1.setTargetLanguage(Language.DE);
        deB1GF1.setDifficultyLevel(LanguageLevel.B1);
        deB1GF1.setTopic("dativ");
        deB1GF1.setSentenceWithBlank("Ich helfe ___ Frau.");
        deB1GF1.setCorrectAnswer("der");
        deB1GF1.setXpReward(20);
        deB1GF1.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(deB1GF1);

        ExerciseFillBlank deB1GF2 = new ExerciseFillBlank();
        deB1GF2.setTargetLanguage(Language.DE);
        deB1GF2.setDifficultyLevel(LanguageLevel.B1);
        deB1GF2.setTopic("nebensaetze");
        deB1GF2.setSentenceWithBlank("Ich weiß nicht, ___ er kommt.");
        deB1GF2.setCorrectAnswer("ob");
        deB1GF2.setXpReward(20);
        deB1GF2.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(deB1GF2);

        ExerciseFillBlank deB1GF3 = new ExerciseFillBlank();
        deB1GF3.setTargetLanguage(Language.DE);
        deB1GF3.setDifficultyLevel(LanguageLevel.B1);
        deB1GF3.setTopic("reflexive_verben");
        deB1GF3.setSentenceWithBlank("Ich freue ___ auf den Urlaub.");
        deB1GF3.setCorrectAnswer("mich");
        deB1GF3.setXpReward(20);
        deB1GF3.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(deB1GF3);

        ExerciseFillBlank deB1GF4 = new ExerciseFillBlank();
        deB1GF4.setTargetLanguage(Language.DE);
        deB1GF4.setDifficultyLevel(LanguageLevel.B1);
        deB1GF4.setTopic("komparativ");
        deB1GF4.setSentenceWithBlank("Berlin ist ___ als München.");
        deB1GF4.setCorrectAnswer("größer");
        deB1GF4.setXpReward(20);
        deB1GF4.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(deB1GF4);

        // DE B2 Fill-blank GRAMMAR (4)
        ExerciseFillBlank deB2GF1 = new ExerciseFillBlank();
        deB2GF1.setTargetLanguage(Language.DE);
        deB2GF1.setDifficultyLevel(LanguageLevel.B2);
        deB2GF1.setTopic("konjunktiv_ii");
        deB2GF1.setSentenceWithBlank("Wenn ich Zeit ___, würde ich kommen.");
        deB2GF1.setCorrectAnswer("hätte");
        deB2GF1.setXpReward(25);
        deB2GF1.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(deB2GF1);

        ExerciseFillBlank deB2GF2 = new ExerciseFillBlank();
        deB2GF2.setTargetLanguage(Language.DE);
        deB2GF2.setDifficultyLevel(LanguageLevel.B2);
        deB2GF2.setTopic("passiv");
        deB2GF2.setSentenceWithBlank("Das Fenster wurde von mir ___.");
        deB2GF2.setCorrectAnswer("geöffnet");
        deB2GF2.setXpReward(25);
        deB2GF2.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(deB2GF2);

        ExerciseFillBlank deB2GF3 = new ExerciseFillBlank();
        deB2GF3.setTargetLanguage(Language.DE);
        deB2GF3.setDifficultyLevel(LanguageLevel.B2);
        deB2GF3.setTopic("plusquamperfekt");
        deB2GF3.setSentenceWithBlank("Nachdem ich gegessen ___, ging ich spazieren.");
        deB2GF3.setCorrectAnswer("hatte");
        deB2GF3.setXpReward(25);
        deB2GF3.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(deB2GF3);

        ExerciseFillBlank deB2GF4 = new ExerciseFillBlank();
        deB2GF4.setTargetLanguage(Language.DE);
        deB2GF4.setDifficultyLevel(LanguageLevel.B2);
        deB2GF4.setTopic("konnektoren");
        deB2GF4.setSentenceWithBlank("Er kam nicht, ___ er krank war.");
        deB2GF4.setCorrectAnswer("obwohl");
        deB2GF4.setXpReward(25);
        deB2GF4.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(deB2GF4);

        // DE C1 Fill-blank GRAMMAR (3)
        ExerciseFillBlank deC1GF1 = new ExerciseFillBlank();
        deC1GF1.setTargetLanguage(Language.DE);
        deC1GF1.setDifficultyLevel(LanguageLevel.C1);
        deC1GF1.setTopic("genitiv");
        deC1GF1.setSentenceWithBlank("Trotz ___ Regens gingen wir spazieren.");
        deC1GF1.setCorrectAnswer("des");
        deC1GF1.setXpReward(30);
        deC1GF1.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(deC1GF1);

        ExerciseFillBlank deC1GF2 = new ExerciseFillBlank();
        deC1GF2.setTargetLanguage(Language.DE);
        deC1GF2.setDifficultyLevel(LanguageLevel.C1);
        deC1GF2.setTopic("partizipial");
        deC1GF2.setSentenceWithBlank("Die ___ Aufgabe war sehr schwierig.");
        deC1GF2.setCorrectAnswer("gestellte");
        deC1GF2.setXpReward(30);
        deC1GF2.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(deC1GF2);

        ExerciseFillBlank deC1GF3 = new ExerciseFillBlank();
        deC1GF3.setTargetLanguage(Language.DE);
        deC1GF3.setDifficultyLevel(LanguageLevel.C1);
        deC1GF3.setTopic("futur_ii");
        deC1GF3.setSentenceWithBlank("Bis morgen werde ich die Arbeit ___ haben.");
        deC1GF3.setCorrectAnswer("beendet");
        deC1GF3.setXpReward(30);
        deC1GF3.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(deC1GF3);

        // DE C2 Fill-blank GRAMMAR (3)
        ExerciseFillBlank deC2GF1 = new ExerciseFillBlank();
        deC2GF1.setTargetLanguage(Language.DE);
        deC2GF1.setDifficultyLevel(LanguageLevel.C2);
        deC2GF1.setTopic("konjunktiv_i");
        deC2GF1.setSentenceWithBlank("Er sagt, er ___ krank.");
        deC2GF1.setCorrectAnswer("sei");
        deC2GF1.setXpReward(35);
        deC2GF1.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(deC2GF1);

        ExerciseFillBlank deC2GF2 = new ExerciseFillBlank();
        deC2GF2.setTargetLanguage(Language.DE);
        deC2GF2.setDifficultyLevel(LanguageLevel.C2);
        deC2GF2.setTopic("nomen_verb");
        deC2GF2.setSentenceWithBlank("Sie hat großen Einfluss auf ihn ___.");
        deC2GF2.setCorrectAnswer("ausgeübt");
        deC2GF2.setXpReward(35);
        deC2GF2.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(deC2GF2);

        ExerciseFillBlank deC2GF3 = new ExerciseFillBlank();
        deC2GF3.setTargetLanguage(Language.DE);
        deC2GF3.setDifficultyLevel(LanguageLevel.C2);
        deC2GF3.setTopic("modalpartikeln");
        deC2GF3.setSentenceWithBlank("Das ist ___ interessant!");
        deC2GF3.setCorrectAnswer("ja");
        deC2GF3.setXpReward(35);
        deC2GF3.setContentType(ExerciseContentType.GRAMMAR);
        fillBlankRepository.save(deC2GF3);

        logger.info("German GRAMMAR exercises seeded: 22 MCQ + 22 Fill-Blank = 44 total");
    }
}