# Sprachlernspiele mit Transloco

## 🎯 Konzept

Dieses Dokument beschreibt, wie Transloco für Sprachlernspiele in unserer App verwendet werden kann.

## 🧩 Zwei-Ebenen-Ansatz

### 1️⃣ **UI-Sprache** (mit Transloco)
Die Sprache der Benutzeroberfläche - Menüs, Buttons, Anweisungen, Feedback

### 2️⃣ **Lerninhalt** (eigene Datenstruktur)
Die zu lernenden Vokabeln, Sätze, Grammatikregeln

## 📁 Empfohlene Dateistruktur

```
frontend/
  src/
    assets/
      i18n/                      # UI-Übersetzungen (Transloco)
        de.json                  # Deutsche UI
        en.json                  # Englische UI
      
      lessons/                   # Lerninhalt (eigene Struktur)
        vocabulary/
          basic-words.json
          animals.json
          colors.json
        grammar/
          present-tense.json
          articles.json
        sentences/
          greetings.json
          daily-conversations.json
```

## 💡 Praktisches Beispiel

### UI-Übersetzungen (Transloco)

**`assets/i18n/de.json`:**
```json
{
  "game": {
    "title": "Vokabeltrainer",
    "instructions": "Wähle die richtige Übersetzung",
    "start": "Spiel starten",
    "next": "Weiter",
    "finish": "Beenden",
    "score": "Punkte",
    "correct": "Richtig! 🎉",
    "incorrect": "Leider falsch. Versuche es nochmal!",
    "timeLeft": "Zeit übrig",
    "questionsAnswered": "Fragen beantwortet"
  },
  "exercises": {
    "vocabulary": "Vokabeln lernen",
    "grammar": "Grammatik üben",
    "listening": "Hörverständnis",
    "speaking": "Sprechen üben"
  }
}
```

**`assets/i18n/en.json`:**
```json
{
  "game": {
    "title": "Vocabulary Trainer",
    "instructions": "Choose the correct translation",
    "start": "Start Game",
    "next": "Next",
    "finish": "Finish",
    "score": "Score",
    "correct": "Correct! 🎉",
    "incorrect": "Sorry, that's wrong. Try again!",
    "timeLeft": "Time left",
    "questionsAnswered": "Questions answered"
  },
  "exercises": {
    "vocabulary": "Learn Vocabulary",
    "grammar": "Practice Grammar",
    "listening": "Listening Comprehension",
    "speaking": "Practice Speaking"
  }
}
```

### Lerninhalt (eigene JSON-Struktur)

**`assets/lessons/vocabulary/animals.json`:**
```json
{
  "lessonId": "animals-basic",
  "title": {
    "de": "Tiere - Grundwortschatz",
    "en": "Animals - Basic Vocabulary"
  },
  "difficulty": "beginner",
  "words": [
    {
      "id": 1,
      "de": "Hund",
      "en": "dog",
      "image": "/assets/images/animals/dog.jpg",
      "audio": {
        "de": "/assets/audio/de/hund.mp3",
        "en": "/assets/audio/en/dog.mp3"
      }
    },
    {
      "id": 2,
      "de": "Katze",
      "en": "cat",
      "image": "/assets/images/animals/cat.jpg",
      "audio": {
        "de": "/assets/audio/de/katze.mp3",
        "en": "/assets/audio/en/cat.mp3"
      }
    },
    {
      "id": 3,
      "de": "Vogel",
      "en": "bird",
      "image": "/assets/images/animals/bird.jpg",
      "audio": {
        "de": "/assets/audio/de/vogel.mp3",
        "en": "/assets/audio/en/bird.mp3"
      }
    }
  ]
}
```

## 🎮 Beispiel-Komponente: Vokabelspiel

### TypeScript

```typescript
import { Component, inject, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { TranslocoDirective, TranslocoService } from '@jsverse/transloco';
import { CommonModule } from '@angular/common';

interface VocabularyWord {
  id: number;
  de: string;
  en: string;
  image?: string;
  audio?: { de: string; en: string };
}

interface VocabularyLesson {
  lessonId: string;
  title: { de: string; en: string };
  words: VocabularyWord[];
}

@Component({
  selector: 'app-vocabulary-game',
  imports: [TranslocoDirective, CommonModule],
  templateUrl: './vocabulary-game.html',
  styleUrl: './vocabulary-game.css'
})
export class VocabularyGame implements OnInit {
  private http = inject(HttpClient);
  private translocoService = inject(TranslocoService);

  lesson: VocabularyLesson | null = null;
  currentWordIndex = 0;
  score = 0;
  isCorrect: boolean | null = null;

  ngOnInit() {
    this.loadLesson('animals');
  }

  loadLesson(lessonName: string) {
    this.http.get<VocabularyLesson>(`/assets/lessons/vocabulary/${lessonName}.json`)
      .subscribe(data => {
        this.lesson = data;
        this.shuffleWords();
      });
  }

  checkAnswer(answer: string) {
    const currentWord = this.lesson!.words[this.currentWordIndex];
    const correctAnswer = currentWord.en; // Oder currentWord.de je nach Richtung
    
    this.isCorrect = answer === correctAnswer;
    if (this.isCorrect) {
      this.score++;
    }
  }

  nextWord() {
    this.isCorrect = null;
    this.currentWordIndex++;
  }

  shuffleWords() {
    // Fisher-Yates shuffle
    if (this.lesson) {
      for (let i = this.lesson.words.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [this.lesson.words[i], this.lesson.words[j]] = 
          [this.lesson.words[j], this.lesson.words[i]];
      }
    }
  }

  get currentWord() {
    return this.lesson?.words[this.currentWordIndex];
  }

  get uiLang() {
    return this.translocoService.getActiveLang();
  }
}
```

### HTML Template

```html
<ng-container *transloco="let t">
  <div class="game-container">
    <header class="game-header">
      <h1>{{ t('game.title') }}</h1>
      <div class="game-stats">
        <span>{{ t('game.score') }}: {{ score }}</span>
        <span>{{ currentWordIndex + 1 }} / {{ lesson?.words.length }}</span>
      </div>
    </header>

    @if (lesson && currentWord) {
      <div class="game-content">
        <p class="instructions">{{ t('game.instructions') }}</p>
        
        <!-- Wort in Deutsch anzeigen -->
        <div class="word-display">
          <h2>{{ currentWord.de }}</h2>
          @if (currentWord.image) {
            <img [src]="currentWord.image" [alt]="currentWord.de">
          }
        </div>

        <!-- Antwortmöglichkeiten -->
        <div class="answer-options">
          @for (word of lesson.words.slice(0, 4); track word.id) {
            <button 
              class="answer-btn"
              [disabled]="isCorrect !== null"
              (click)="checkAnswer(word.en)"
            >
              {{ word.en }}
            </button>
          }
        </div>

        <!-- Feedback -->
        @if (isCorrect === true) {
          <div class="feedback correct">
            {{ t('game.correct') }}
            <button (click)="nextWord()">{{ t('game.next') }}</button>
          </div>
        }
        @if (isCorrect === false) {
          <div class="feedback incorrect">
            {{ t('game.incorrect') }}
          </div>
        }
      </div>
    }
  </div>
</ng-container>
```

## 🔄 Workflow für neue Spiele

1. **UI-Texte zu Transloco hinzufügen**
   - Anweisungen, Buttons, Feedback in `i18n/de.json` und `i18n/en.json`

2. **Lerninhalt erstellen**
   - JSON-Dateien in `assets/lessons/` mit strukturierten Daten

3. **Service erstellen (optional)**
   ```typescript
   @Injectable({ providedIn: 'root' })
   export class LessonService {
     private http = inject(HttpClient);
     
     loadLesson(category: string, lessonName: string) {
       return this.http.get(`/assets/lessons/${category}/${lessonName}.json`);
     }
   }
   ```

4. **Komponente implementieren**
   - Transloco für UI
   - HttpClient für Lerninhalt

## 🎯 Vorteile dieses Ansatzes

✅ **Trennung von UI und Content**: Leichter zu warten und zu erweitern  
✅ **Flexibilität**: Lerninhalt kann dynamisch geladen werden  
✅ **Wiederverwendbarkeit**: Gleiche UI für verschiedene Lernspiele  
✅ **Performance**: Nur benötigte Lektionen werden geladen  
✅ **Erweiterbarkeit**: Neue Sprachen einfach hinzufügen  

## 🚀 Mögliche Spieltypen

- **Vokabeltrainer**: Wort → Übersetzung
- **Lückentext**: Fehlende Wörter einsetzen
- **Memory**: Paare finden (Wort + Übersetzung)
- **Multiple Choice**: Richtige Übersetzung wählen
- **Hörverständnis**: Audio → Wort zuordnen
- **Satzbau**: Wörter in richtige Reihenfolge bringen

## 📝 Nächste Schritte

1. Lerninhalt-Struktur im Team definieren
2. Erste Lektion als JSON erstellen
3. Basis-Spielkomponente entwickeln
4. Weitere Spieltypen hinzufügen

## 💡 Tipps

- **Gamification**: Punkte, Levels, Achievements über Transloco übersetzen
- **Progressives Lernen**: Schwierigkeit basierend auf Performance anpassen
- **Audio**: Native Speaker für Aussprache verwenden
- **Bilder**: Visuelle Unterstützung hilft beim Lernen
- **Feedback**: Sofortiges, motivierendes Feedback geben

---

**Weitere Informationen zu Transloco**: Siehe [TRANSLOCO_GUIDE.md](./TRANSLOCO_GUIDE.md)
