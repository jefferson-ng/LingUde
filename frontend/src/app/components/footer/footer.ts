import { Component } from '@angular/core';

@Component({
  selector: 'app-footer',
  standalone: true,
  template: `<footer style="height:60px;width:100%;background:var(--color-primary,#3b82f6);color:white;display:flex;align-items:center;justify-content:center;font-size:1.1rem;">LingUDE Footer</footer>`
})
export class Footer {}
