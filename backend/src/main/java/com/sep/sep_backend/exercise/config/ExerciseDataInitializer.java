package com.sep.sep_backend.exercise.config;

import com.sep.sep_backend.exercise.entity.ExerciseFillBlank;
import com.sep.sep_backend.exercise.entity.ExerciseMcq;
import com.sep.sep_backend.exercise.repository.ExerciseFillBlankRepository;
import com.sep.sep_backend.exercise.repository.ExerciseMcqRepository;
import com.sep.sep_backend.user.entity.Language;
import com.sep.sep_backend.user.entity.LanguageLevel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class that initializes the database with sample exercise data.
 * Creates Multiple Choice and Fill-in-the-Blank exercises for German language learning
 * across all CEFR levels (A1 to C2).
 */
@Configuration
public class ExerciseDataInitializer {

    /**
     * CommandLineRunner bean that populates the database with sample exercises.
     * Only runs if no exercises exist to avoid duplicates.
     *
     * @param mcqRepository repository for MCQ exercises
     * @param fillBlankRepository repository for Fill-Blank exercises
     * @return CommandLineRunner that executes on application startup
     */
    @Bean
    CommandLineRunner initializeExerciseData(ExerciseMcqRepository mcqRepository,
                                              ExerciseFillBlankRepository fillBlankRepository) {
        return args -> {
            // Check if exercises already exist
            long mcqCount = mcqRepository.count();
            long fillBlankCount = fillBlankRepository.count();
            
            if (mcqCount > 0 || fillBlankCount > 0) {
                System.out.println("ℹ️  Exercise data already exists (MCQ: " + mcqCount + ", Fill-Blank: " + fillBlankCount + "), skipping initialization");
                return;
            }
            
            System.out.println("🎓 Initializing German language exercise data...");
            
            // ============= A1 Level Exercises (Basics) =============
            
            // A1 MCQ Exercises
            ExerciseMcq a1Mcq1 = new ExerciseMcq(
                Language.DE, LanguageLevel.A1,
                "How do you say 'Hello' in German?",
                "Hallo",
                "Tschüss",
                "Danke",
                "Bitte"
            );
            a1Mcq1.setTopic("basics");
            a1Mcq1.setXpReward(10);
            
            ExerciseMcq a1Mcq2 = new ExerciseMcq(
                Language.DE, LanguageLevel.A1,
                "What is the German word for 'water'?",
                "Wasser",
                "Brot",
                "Milch",
                "Kaffee"
            );
            a1Mcq2.setTopic("basics");
            a1Mcq2.setXpReward(10);
            
            ExerciseMcq a1Mcq3 = new ExerciseMcq(
                Language.DE, LanguageLevel.A1,
                "Which number is 'drei'?",
                "3",
                "2",
                "4",
                "5"
            );
            a1Mcq3.setTopic("basics");
            a1Mcq3.setXpReward(10);
            
            // A1 Fill-Blank Exercises
            ExerciseFillBlank a1Fill1 = new ExerciseFillBlank(
                Language.DE, LanguageLevel.A1,
                "Guten ___! (Good morning!)",
                "Morgen"
            );
            a1Fill1.setTopic("basics");
            a1Fill1.setXpReward(10);
            
            ExerciseFillBlank a1Fill2 = new ExerciseFillBlank(
                Language.DE, LanguageLevel.A1,
                "Ich ___ Anna. (I am Anna.)",
                "bin"
            );
            a1Fill2.setTopic("basics");
            a1Fill2.setXpReward(10);
            
            ExerciseFillBlank a1Fill3 = new ExerciseFillBlank(
                Language.DE, LanguageLevel.A1,
                "Das ist ___ Buch. (This is a book.)",
                "ein"
            );
            a1Fill3.setTopic("basics");
            a1Fill3.setXpReward(10);
            
            // ============= A2 Level Exercises (Basics) =============
            
            // A2 MCQ Exercises
            ExerciseMcq a2Mcq1 = new ExerciseMcq(
                Language.DE, LanguageLevel.A2,
                "How do you say 'I would like' in German?",
                "Ich möchte",
                "Ich habe",
                "Ich bin",
                "Ich kann"
            );
            a2Mcq1.setTopic("basics");
            a2Mcq1.setXpReward(12);
            
            ExerciseMcq a2Mcq2 = new ExerciseMcq(
                Language.DE, LanguageLevel.A2,
                "What is the past tense of 'ich gehe' (I go)?",
                "ich ging",
                "ich gehen",
                "ich geht",
                "ich gegangen"
            );
            a2Mcq2.setTopic("basics");
            a2Mcq2.setXpReward(12);
            
            ExerciseMcq a2Mcq3 = new ExerciseMcq(
                Language.DE, LanguageLevel.A2,
                "Which word means 'family'?",
                "Familie",
                "Freund",
                "Haus",
                "Stadt"
            );
            a2Mcq3.setTopic("basics");
            a2Mcq3.setXpReward(12);
            
            // A2 Fill-Blank Exercises
            ExerciseFillBlank a2Fill1 = new ExerciseFillBlank(
                Language.DE, LanguageLevel.A2,
                "Meine Mutter ___ sehr nett. (My mother is very nice.)",
                "ist"
            );
            a2Fill1.setTopic("basics");
            a2Fill1.setXpReward(12);
            
            ExerciseFillBlank a2Fill2 = new ExerciseFillBlank(
                Language.DE, LanguageLevel.A2,
                "Wir ___ gestern im Kino. (We were at the cinema yesterday.)",
                "waren"
            );
            a2Fill2.setTopic("basics");
            a2Fill2.setXpReward(12);
            
            ExerciseFillBlank a2Fill3 = new ExerciseFillBlank(
                Language.DE, LanguageLevel.A2,
                "Ich ___ gerne Fußball. (I like playing football.)",
                "spiele"
            );
            a2Fill3.setTopic("basics");
            a2Fill3.setXpReward(12);
            
            // ============= B1 Level Exercises (Intermediate) =============
            
            // B1 MCQ Exercises
            ExerciseMcq b1Mcq1 = new ExerciseMcq(
                Language.DE, LanguageLevel.B1,
                "Which sentence is grammatically correct?",
                "Ich habe gestern einen Film gesehen.",
                "Ich habe gestern ein Film gesehen.",
                "Ich bin gestern einen Film gesehen.",
                "Ich hatte gestern einen Film sehen."
            );
            b1Mcq1.setTopic("intermediate");
            b1Mcq1.setXpReward(15);
            
            ExerciseMcq b1Mcq2 = new ExerciseMcq(
                Language.DE, LanguageLevel.B1,
                "What does 'obwohl' mean?",
                "although",
                "because",
                "therefore",
                "however"
            );
            b1Mcq2.setTopic("intermediate");
            b1Mcq2.setXpReward(15);
            
            ExerciseMcq b1Mcq3 = new ExerciseMcq(
                Language.DE, LanguageLevel.B1,
                "Which verb fits: 'Er ___ sich über das Geschenk gefreut'?",
                "hat",
                "ist",
                "wird",
                "hatte"
            );
            b1Mcq3.setTopic("intermediate");
            b1Mcq3.setXpReward(15);
            
            // B1 Fill-Blank Exercises
            ExerciseFillBlank b1Fill1 = new ExerciseFillBlank(
                Language.DE, LanguageLevel.B1,
                "Ich würde gerne mehr Zeit mit meiner Familie ___, wenn ich könnte. (I would like to spend more time with my family if I could.)",
                "verbringen"
            );
            b1Fill1.setTopic("intermediate");
            b1Fill1.setXpReward(15);
            
            ExerciseFillBlank b1Fill2 = new ExerciseFillBlank(
                Language.DE, LanguageLevel.B1,
                "Der Film, ___ wir gestern gesehen haben, war sehr spannend. (The film that we saw yesterday was very exciting.)",
                "den"
            );
            b1Fill2.setTopic("intermediate");
            b1Fill2.setXpReward(15);
            
            ExerciseFillBlank b1Fill3 = new ExerciseFillBlank(
                Language.DE, LanguageLevel.B1,
                "Sie arbeitet hart, ___ sie erfolgreich sein möchte. (She works hard because she wants to be successful.)",
                "weil"
            );
            b1Fill3.setTopic("intermediate");
            b1Fill3.setXpReward(15);
            
            // ============= B2 Level Exercises (Intermediate) =============
            
            // B2 MCQ Exercises
            ExerciseMcq b2Mcq1 = new ExerciseMcq(
                Language.DE, LanguageLevel.B2,
                "What is the meaning of the idiom 'Tomaten auf den Augen haben'?",
                "to be oblivious to something obvious",
                "to be very tired",
                "to love tomatoes",
                "to have good vision"
            );
            b2Mcq1.setTopic("intermediate");
            b2Mcq1.setXpReward(18);
            
            ExerciseMcq b2Mcq2 = new ExerciseMcq(
                Language.DE, LanguageLevel.B2,
                "Which is the correct Konjunktiv II form of 'können'?",
                "könnte",
                "kann",
                "konnte",
                "gekonnt"
            );
            b2Mcq2.setTopic("intermediate");
            b2Mcq2.setXpReward(18);
            
            ExerciseMcq b2Mcq3 = new ExerciseMcq(
                Language.DE, LanguageLevel.B2,
                "Complete: 'Je mehr ich lerne, ___ besser verstehe ich die Sprache.'",
                "desto",
                "als",
                "wie",
                "so"
            );
            b2Mcq3.setTopic("intermediate");
            b2Mcq3.setXpReward(18);
            
            // B2 Fill-Blank Exercises
            ExerciseFillBlank b2Fill1 = new ExerciseFillBlank(
                Language.DE, LanguageLevel.B2,
                "Die Verhandlungen wurden ___, nachdem beide Seiten einen Kompromiss gefunden hatten. (The negotiations were concluded after both sides found a compromise.)",
                "abgeschlossen"
            );
            b2Fill1.setTopic("intermediate");
            b2Fill1.setXpReward(18);
            
            ExerciseFillBlank b2Fill2 = new ExerciseFillBlank(
                Language.DE, LanguageLevel.B2,
                "___ des schlechten Wetters fand das Festival statt. (Despite the bad weather, the festival took place.)",
                "Trotz"
            );
            b2Fill2.setTopic("intermediate");
            b2Fill2.setXpReward(18);
            
            ExerciseFillBlank b2Fill3 = new ExerciseFillBlank(
                Language.DE, LanguageLevel.B2,
                "Es ist wichtig, dass du dich auf die Prüfung ___. (It's important that you prepare for the exam.)",
                "vorbereitest"
            );
            b2Fill3.setTopic("intermediate");
            b2Fill3.setXpReward(18);
            
            // ============= C1 Level Exercises (Advanced) =============
            
            // C1 MCQ Exercises
            ExerciseMcq c1Mcq1 = new ExerciseMcq(
                Language.DE, LanguageLevel.C1,
                "Which word best fits: 'Die Politik hat ___ Auswirkungen auf die Wirtschaft'?",
                "weitreichende",
                "weite",
                "reichende",
                "ausreichende"
            );
            c1Mcq1.setTopic("advanced");
            c1Mcq1.setXpReward(22);
            
            ExerciseMcq c1Mcq2 = new ExerciseMcq(
                Language.DE, LanguageLevel.C1,
                "What does 'etw. in Kauf nehmen' mean?",
                "to accept something unpleasant",
                "to buy something",
                "to take something shopping",
                "to negotiate a price"
            );
            c1Mcq2.setTopic("advanced");
            c1Mcq2.setXpReward(22);
            
            ExerciseMcq c1Mcq3 = new ExerciseMcq(
                Language.DE, LanguageLevel.C1,
                "Choose the correct form: 'Der Antrag ___ bis Ende des Monats eingereicht werden.'",
                "muss",
                "müsste",
                "musste",
                "müssen"
            );
            c1Mcq3.setTopic("advanced");
            c1Mcq3.setXpReward(22);
            
            // C1 Fill-Blank Exercises
            ExerciseFillBlank c1Fill1 = new ExerciseFillBlank(
                Language.DE, LanguageLevel.C1,
                "Die neue Gesetzgebung ___ heftige Debatten im Parlament aus. (The new legislation triggered heated debates in parliament.)",
                "löste"
            );
            c1Fill1.setTopic("advanced");
            c1Fill1.setXpReward(22);
            
            ExerciseFillBlank c1Fill2 = new ExerciseFillBlank(
                Language.DE, LanguageLevel.C1,
                "___ seiner langjährigen Erfahrung wurde er zum Abteilungsleiter ernannt. (Due to his many years of experience, he was appointed department head.)",
                "Aufgrund"
            );
            c1Fill2.setTopic("advanced");
            c1Fill2.setXpReward(22);
            
            ExerciseFillBlank c1Fill3 = new ExerciseFillBlank(
                Language.DE, LanguageLevel.C1,
                "Es ___ sich um ein äußerst komplexes Problem, das sorgfältige Analyse erfordert. (It is an extremely complex problem that requires careful analysis.)",
                "handelt"
            );
            c1Fill3.setTopic("advanced");
            c1Fill3.setXpReward(22);
            
            // ============= C2 Level Exercises (Advanced) =============
            
            // C2 MCQ Exercises
            ExerciseMcq c2Mcq1 = new ExerciseMcq(
                Language.DE, LanguageLevel.C2,
                "Which synonym best replaces 'unumgänglich' in 'Diese Maßnahme ist unumgänglich'?",
                "unvermeidlich",
                "unmöglich",
                "unverständlich",
                "unbrauchbar"
            );
            c2Mcq1.setTopic("advanced");
            c2Mcq1.setXpReward(25);
            
            ExerciseMcq c2Mcq2 = new ExerciseMcq(
                Language.DE, LanguageLevel.C2,
                "What does 'jmdm. einen Bärendienst erweisen' mean?",
                "to do someone a disservice despite good intentions",
                "to help someone generously",
                "to work with bears",
                "to provide excellent service"
            );
            c2Mcq2.setTopic("advanced");
            c2Mcq2.setXpReward(25);
            
            ExerciseMcq c2Mcq3 = new ExerciseMcq(
                Language.DE, LanguageLevel.C2,
                "Complete the sophisticated expression: 'Die Forschungsergebnisse ___ neue Perspektiven für die Wissenschaft.'",
                "eröffnen",
                "öffnen",
                "machen",
                "geben"
            );
            c2Mcq3.setTopic("advanced");
            c2Mcq3.setXpReward(25);
            
            // C2 Fill-Blank Exercises
            ExerciseFillBlank c2Fill1 = new ExerciseFillBlank(
                Language.DE, LanguageLevel.C2,
                "Die Dissertation ___ sich mit den philosophischen Implikationen der Quantenmechanik. (The dissertation deals with the philosophical implications of quantum mechanics.)",
                "befasst"
            );
            c2Fill1.setTopic("advanced");
            c2Fill1.setXpReward(25);
            
            ExerciseFillBlank c2Fill2 = new ExerciseFillBlank(
                Language.DE, LanguageLevel.C2,
                "___ der vorherrschenden Meinung vertrat er eine konträre Position. (Contrary to the prevailing opinion, he held a contrarian position.)",
                "Entgegen"
            );
            c2Fill2.setTopic("advanced");
            c2Fill2.setXpReward(25);
            
            // Save all MCQ exercises
            mcqRepository.save(a1Mcq1);
            mcqRepository.save(a1Mcq2);
            mcqRepository.save(a1Mcq3);
            mcqRepository.save(a2Mcq1);
            mcqRepository.save(a2Mcq2);
            mcqRepository.save(a2Mcq3);
            mcqRepository.save(b1Mcq1);
            mcqRepository.save(b1Mcq2);
            mcqRepository.save(b1Mcq3);
            mcqRepository.save(b2Mcq1);
            mcqRepository.save(b2Mcq2);
            mcqRepository.save(b2Mcq3);
            mcqRepository.save(c1Mcq1);
            mcqRepository.save(c1Mcq2);
            mcqRepository.save(c1Mcq3);
            mcqRepository.save(c2Mcq1);
            mcqRepository.save(c2Mcq2);
            mcqRepository.save(c2Mcq3);
            
            // Save all Fill-Blank exercises
            fillBlankRepository.save(a1Fill1);
            fillBlankRepository.save(a1Fill2);
            fillBlankRepository.save(a1Fill3);
            fillBlankRepository.save(a2Fill1);
            fillBlankRepository.save(a2Fill2);
            fillBlankRepository.save(a2Fill3);
            fillBlankRepository.save(b1Fill1);
            fillBlankRepository.save(b1Fill2);
            fillBlankRepository.save(b1Fill3);
            fillBlankRepository.save(b2Fill1);
            fillBlankRepository.save(b2Fill2);
            fillBlankRepository.save(b2Fill3);
            fillBlankRepository.save(c1Fill1);
            fillBlankRepository.save(c1Fill2);
            fillBlankRepository.save(c1Fill3);
            fillBlankRepository.save(c2Fill1);
            fillBlankRepository.save(c2Fill2);
            
            System.out.println("✅ Created 18 MCQ exercises");
            System.out.println("✅ Created 17 Fill-in-the-Blank exercises");
            System.out.println("📚 Total: 35 German language exercises across all CEFR levels (A1-C2)");
            System.out.println("🎯 Topics: basics (A1-A2), intermediate (B1-B2), advanced (C1-C2)");
        };
    }
}
