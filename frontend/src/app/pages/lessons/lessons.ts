import { Component } from '@angular/core';
import { TranslocoDirective } from '@jsverse/transloco';

@Component({
  selector: 'app-lessons',
  imports: [TranslocoDirective],
  templateUrl: './lessons.html',
  styleUrl: './lessons.css'
})
export class Lessons {
}
