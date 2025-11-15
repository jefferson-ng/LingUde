# 🎯 Eigene Aufgaben in Code & UI integrieren

## Übersicht

Diese Anleitung zeigt **Schritt für Schritt**, wie du deine fertigen Aufgaben (MCQ oder Lückentext) in die Learning Page integrierst, sodass sie im UI angezeigt werden.

---

## 📋 Voraussetzungen

Du hast bereits:
- ✅ Aufgaben erstellt (MCQ oder Fill Blank)
- ✅ Eine Lesson geplant (Titel, Beschreibung, Schwierigkeit)
- ✅ Ein Topic/Thema definiert (z.B. "restaurant", "greetings", "numbers")

---

## 🚀 Integration in 4 Schritten

### **Schritt 1: Übersetzungen hinzufügen**

Füge deine Lesson-Texte in beide Sprachdateien ein.

#### 📁 `frontend/src/assets/i18n/de.json`

```json
{
  "learning": {
    "lessons": {
      "restaurant": {
        "title": "Im Restaurant bestellen",
        "description": "Lerne, wie du Essen und Getränke auf Deutsch bestellst",
        "section": "Alltag",
        "exercises": {
          "ex1": "Begrüßung und Tisch reservieren",
          "ex2": "Speisekarte lesen",
          "ex3": "Getränke bestellen",
          "ex4": "Hauptgericht bestellen",
          "ex5": "Nach der Rechnung fragen"
        }
      }
    }
  }
}
```

#### 📁 `frontend/src/assets/i18n/en.json`

```json
{
  "learning": {
    "lessons": {
      "restaurant": {
        "title": "Ordering at a Restaurant",
        "description": "Learn how to order food and drinks in German",
        "section": "Daily Life",
        "exercises": {
          "ex1": "Greeting and table reservation",
          "ex2": "Reading the menu",
          "ex3": "Ordering drinks",
          "ex4": "Ordering main course",
          "ex5": "Asking for the bill"
        }
      }
    }
  }
}
```

---

### **Schritt 2: Mock-Aufgaben hinzufügen**

Füge deine Aufgaben in die Mock-Daten ein (für Testing ohne Backend).

#### 📁 `frontend/src/app/services/exercise.mock.ts`

```typescript
// Multiple Choice Aufgabe hinzufügen
export const mockMCQExercises: ExerciseMCQ[] = [
  // ... existing exercises ...
  
  {
    exerciseId: 101,
    type: 'MCQ',
    questionText: 'Wie sagt man "I would like to order" auf Deutsch?',
    correctAnswer: 'Ich möchte bestellen',
    wrongAnswers: [
      'Ich will kaufen',
      'Ich kann essen',
      'Ich muss bezahlen'
    ],
    difficultyLevel: 'A2',
    topic: 'restaurant',  // ← Wichtig: Muss mit Lesson-Topic übereinstimmen!
    xpReward: 15,
    languageToLearn: 'DE'
  },
  {
    exerciseId: 102,
    type: 'MCQ',
    questionText: 'Was bedeutet "die Speisekarte"?',
    correctAnswer: 'The menu',
    wrongAnswers: [
      'The bill',
      'The table',
      'The waiter'
    ],
    difficultyLevel: 'A2',
    topic: 'restaurant',
    xpReward: 10,
    languageToLearn: 'DE'
  }
];

// Lückentext Aufgabe hinzufügen
export const mockFillBlankExercises: ExerciseFillBlank[] = [
  // ... existing exercises ...
  
  {
    exerciseId: 201,
    type: 'FILL_BLANK',
    sentence: 'Ich hätte gerne ___ Glas Wasser, bitte.',
    correctAnswer: 'ein',
    difficultyLevel: 'A2',
    topic: 'restaurant',
    xpReward: 12,
    languageToLearn: 'DE'
  },
  {
    exerciseId: 202,
    type: 'FILL_BLANK',
    sentence: 'Die Rechnung, ___!',
    correctAnswer: 'bitte',
    difficultyLevel: 'A1',
    topic: 'restaurant',
    xpReward: 8,
    languageToLearn: 'DE'
  }
];
```

**⚠️ WICHTIG:**
- `topic: 'restaurant'` muss **exakt** mit dem Topic in der Lesson übereinstimmen!
- `difficultyLevel` sollte zur Lesson passen
- `languageToLearn: 'DE'` für Deutsch (oder 'EN' für Englisch)

---

### **Schritt 3: Lesson in Learning Page hinzufügen**

Füge die Lesson zum Lernpfad hinzu.

#### 📁 `frontend/src/app/pages/learning/learning.ts`

```typescript
protected readonly lessons = signal<Lesson[]>([
  // ... existing lessons ...
  
  { 
    id: 4,  // ← Eindeutige ID
    titleKey: 'learning.lessons.restaurant.title',
    sectionKey: 'learning.lessons.restaurant.section',
    descriptionKey: 'learning.lessons.restaurant.description',
    exerciseKeys: [
      'learning.lessons.restaurant.exercises.ex1',
      'learning.lessons.restaurant.exercises.ex2',
      'learning.lessons.restaurant.exercises.ex3',
      'learning.lessons.restaurant.exercises.ex4',
      'learning.lessons.restaurant.exercises.ex5'
    ],
    status: 'locked',           // Standardmäßig gesperrt
    progress: 0, 
    stars: 0,
    difficultyLevel: 'A2',      // A1, A2, B1, B2, C1, C2
    topic: 'restaurant'         // ← Muss mit Mock-Daten übereinstimmen!
  }
]);
```

**Status-Optionen:**
- `'locked'` - Gesperrt (Standard für neue Lessons)
- `'available'` - Verfügbar zum Starten
- `'current'` - Aktuell in Bearbeitung
- `'completed'` - Abgeschlossen

---

### **Schritt 4: Testen im Browser**

1. **App starten** (falls noch nicht läuft):
   ```powershell
   cd frontend
   npm start
   ```

2. **Learning Page öffnen** → Zur "/learning" Route navigieren

3. **Lesson Node anklicken** → Side Panel öffnet sich

4. **Auf "Starten" klicken** → Exercise Mode startet

5. **Aufgaben durchgehen** → Testen, ob alles funktioniert

---

## 🔄 Workflow-Diagramm

```
1. Aufgaben erstellen
   ↓
2. Übersetzungen in de.json & en.json hinzufügen
   ↓
3. Mock-Aufgaben in exercise.mock.ts einfügen
   ↓
4. Lesson in learning.ts hinzufügen
   ↓
5. Im Browser testen
   ↓
6. ✅ Fertig!
```

---

## 📝 Vollständiges Beispiel: "Zahlen lernen"

### 1️⃣ Übersetzungen

**de.json:**
```json
"numbers": {
  "title": "Zahlen 1-20",
  "description": "Lerne die deutschen Zahlen von 1 bis 20",
  "section": "Grundlagen",
  "exercises": {
    "ex1": "Zahlen 1-10",
    "ex2": "Zahlen 11-20",
    "ex3": "Zahlen in Sätzen"
  }
}
```

**en.json:**
```json
"numbers": {
  "title": "Numbers 1-20",
  "description": "Learn German numbers from 1 to 20",
  "section": "Basics",
  "exercises": {
    "ex1": "Numbers 1-10",
    "ex2": "Numbers 11-20",
    "ex3": "Numbers in sentences"
  }
}
```

### 2️⃣ Mock-Aufgaben

**exercise.mock.ts:**
```typescript
{
  exerciseId: 301,
  type: 'MCQ',
  questionText: 'Was bedeutet "fünf"?',
  correctAnswer: 'five',
  wrongAnswers: ['four', 'six', 'seven'],
  difficultyLevel: 'A1',
  topic: 'numbers',
  xpReward: 10,
  languageToLearn: 'DE'
},
{
  exerciseId: 302,
  type: 'FILL_BLANK',
  sentence: 'Ich habe ___ Äpfel.',
  correctAnswer: 'drei',
  difficultyLevel: 'A1',
  topic: 'numbers',
  xpReward: 8,
  languageToLearn: 'DE'
}
```

### 3️⃣ Lesson

**learning.ts:**
```typescript
{ 
  id: 5,
  titleKey: 'learning.lessons.numbers.title',
  sectionKey: 'learning.lessons.numbers.section',
  descriptionKey: 'learning.lessons.numbers.description',
  exerciseKeys: [
    'learning.lessons.numbers.exercises.ex1',
    'learning.lessons.numbers.exercises.ex2',
    'learning.lessons.numbers.exercises.ex3'
  ],
  status: 'available',  // Sofort verfügbar
  progress: 0,
  stars: 0,
  difficultyLevel: 'A1',
  topic: 'numbers'
}
```

---

## 🎯 Wichtige Regeln

### ✅ DO's:
- **Eindeutige IDs** für Lessons verwenden
- **Topic exakt** zwischen Lesson und Exercises matchen
- **Beide Sprachen** (de.json UND en.json) pflegen
- **Konsistente Schwierigkeit** zwischen Lesson und Exercises
- **Übungsnamen** in exerciseKeys in der **richtigen Reihenfolge** auflisten

### ❌ DON'Ts:
- Topic-Namen **nicht** mit Tippfehlern schreiben
- **Keine hardcodierten** Texte in learning.ts (nur Translation Keys!)
- **Nicht** den Mock-Modus vergessen zu aktivieren (useMockData = true)
- **Keine** doppelten Exercise IDs verwenden

---

## 🔧 Backend-Integration (später)

Wenn das Backend fertig ist:

1. **Mock-Modus deaktivieren:**
   ```typescript
   // In exercise.service.ts
   private useMockData = false;  // ← false setzen
   ```

2. **API-Endpunkt konfigurieren:**
   ```typescript
   private apiUrl = 'http://localhost:8080/api/exercises';
   ```

3. **Aufgaben aus DB laden:**
   - Der Service holt automatisch Aufgaben per API
   - Filter nach: `languageToLearn`, `difficultyLevel`, `topic`

---

## 🆘 Troubleshooting

### Problem: Aufgaben werden nicht geladen
**Lösung:**
- Überprüfe, ob `useMockData = true` in `exercise.service.ts`
- Stelle sicher, dass `topic` in Lesson und Exercises identisch ist

### Problem: Lesson bleibt gesperrt
**Lösung:**
- Setze `status: 'available'` in der Lesson-Definition
- Oder setze die vorherige Lesson auf `status: 'completed'`

### Problem: Texte werden nicht übersetzt
**Lösung:**
- Überprüfe die Translation Keys auf Tippfehler
- Stelle sicher, dass Keys in **beiden** JSON-Dateien existieren

### Problem: Exercise Mode startet nicht
**Lösung:**
- Öffne Browser Console (F12)
- Schaue nach Fehlermeldungen
- Überprüfe, ob mindestens 1 Exercise mit passendem Topic existiert

---

## 📚 Weitere Ressourcen

- **AUFGABEN_QUICK_EXAMPLE.md** - Schnelles Beispiel zum Kopieren
- **EIGENE_AUFGABEN_HINZUFUEGEN.md** - Aufgaben erstellen
- **LESSONS_HINZUFUEGEN.md** - Lessons hinzufügen
- **TRANSLOCO_LESSONS.md** - Mehrsprachigkeit
- **EXERCISE_ARCHITECTURE.md** - Technische Details

---

## ✅ Checkliste

Vor dem Testen:
- [ ] Übersetzungen in de.json hinzugefügt
- [ ] Übersetzungen in en.json hinzugefügt
- [ ] Mock-Aufgaben in exercise.mock.ts erstellt
- [ ] Topic zwischen Lesson und Exercises stimmt überein
- [ ] Lesson in learning.ts hinzugefügt
- [ ] Browser-Console auf Fehler überprüft
- [ ] Lesson im UI sichtbar
- [ ] Exercise Mode startet beim Klick

**Viel Erfolg! 🚀**
