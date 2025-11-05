import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

interface Lesson {
  id: number;
  title: string;
  section: string;
  status: 'locked' | 'available' | 'completed' | 'current';
  progress?: number;
  stars?: number;
  exercises?: string[];
  description?: string;
}

@Component({
  selector: 'app-learning',
  imports: [CommonModule, RouterLink],
  templateUrl: './learning.html',
  styleUrl: './learning.css'
})
export class Learning {
  protected readonly currentUnit = signal('Stufe 1, Abschnitt 1');
  protected readonly unitTitle = signal('Bestelle im Cafe');
  
  protected readonly lessons = signal<Lesson[]>([
    { 
      id: 1, 
      title: 'Begrüßungen', 
      section: 'Grundlagen', 
      status: 'completed', 
      progress: 100, 
      stars: 3,
      description: 'Lerne grundlegende Begrüßungen und Vorstellungen',
      exercises: ['Hallo & Tschüss', 'Wie geht es dir?', 'Sich vorstellen']
    },
    { 
      id: 2, 
      title: 'Zahlen', 
      section: 'Grundlagen', 
      status: 'completed', 
      progress: 100, 
      stars: 2,
      description: 'Zahlen von 1-100 lernen und verwenden',
      exercises: ['Zahlen 1-10', 'Zahlen 11-100', 'Preise nennen']
    },
    { 
      id: 3, 
      title: 'Im Café', 
      section: 'Grundlagen', 
      status: 'current', 
      progress: 60, 
      stars: 0,
      description: 'Bestelle Getränke und Essen im Café',
      exercises: ['Getränke bestellen', 'Essen bestellen', 'Bezahlen']
    },
    { 
      id: 4, 
      title: 'Familie', 
      section: 'Grundlagen', 
      status: 'available', 
      progress: 0, 
      stars: 0,
      description: 'Sprich über deine Familie und Verwandte',
      exercises: ['Familienmitglieder', 'Beziehungen', 'Über Familie sprechen']
    },
    { 
      id: 5, 
      title: 'Wiederholung 1', 
      section: 'Grundlagen', 
      status: 'available', 
      progress: 0, 
      stars: 0,
      description: 'Wiederhole alles aus den vorherigen Lektionen',
      exercises: ['Gemischte Übungen', 'Quiz', 'Sprechübung']
    },
    { 
      id: 6, 
      title: 'Einkaufen', 
      section: 'Fortgeschritten', 
      status: 'locked', 
      progress: 0, 
      stars: 0,
      description: 'Lerne im Geschäft einzukaufen',
      exercises: ['Kleidung', 'Größen & Farben', 'Im Supermarkt']
    },
    { 
      id: 7, 
      title: 'Wegbeschreibung', 
      section: 'Fortgeschritten', 
      status: 'locked', 
      progress: 0, 
      stars: 0,
      description: 'Nach dem Weg fragen und Richtungen verstehen',
      exercises: ['Richtungen', 'Orte in der Stadt', 'Nach dem Weg fragen']
    },
    { 
      id: 8, 
      title: 'Abschlusstest', 
      section: 'Fortgeschritten', 
      status: 'locked', 
      progress: 0, 
      stars: 0,
      description: 'Teste dein Wissen in allen Bereichen',
      exercises: ['Hörverständnis', 'Leseverständnis', 'Sprechen']
    }
  ]);

  protected readonly dailyGoal = signal(10);
  protected readonly dailyProgress = signal(0);
  protected readonly streak = signal(0);
  protected readonly hearts = signal(4);
  protected readonly gems = signal(500);

  startLesson(lesson: Lesson) {
    if (lesson.status === 'locked') {
      console.log('Diese Lektion ist noch gesperrt');
      return;
    }
    console.log('Starte Lektion:', lesson.title);
    // Hier würde die Lektion gestartet werden
  }

  getLessonIcon(lesson: Lesson): string {
    return '';
  }

  getLessonStatusText(lesson: Lesson): string {
    if (lesson.status === 'completed') return 'Abgeschlossen';
    if (lesson.status === 'current') return 'In Bearbeitung';
    if (lesson.status === 'available') return 'Verfügbar';
    return 'Gesperrt';
  }
}
