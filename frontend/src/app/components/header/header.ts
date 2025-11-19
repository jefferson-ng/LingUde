import { Component } from '@angular/core';

@Component({
  selector: 'app-header',
  standalone: true,
  template: `<header style="height:80px;width:100%;background:var(--color-primary,#3b82f6);color:white;display:flex;align-items:center;justify-content:center;font-size:1.5rem;">LingUDE Header</header>`
})
export class Header {}
