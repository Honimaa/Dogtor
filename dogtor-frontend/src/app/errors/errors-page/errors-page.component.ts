import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core';

@Component({
  selector: 'app-errors-page',
  templateUrl: './errors-page.component.html',
  styleUrls: ['./errors-page.component.css']
})
export class ErrorsPageComponent implements AfterViewInit {
  @ViewChild('video') video?: ElementRef<HTMLVideoElement>;

  // Angular no aplica el atributo "muted" antes del autoplay, así que el navegador lo bloquea.
  // Se silencia por código y se inicia la reproducción manualmente.
  ngAfterViewInit(): void {
    const video = this.video?.nativeElement;
    if (!video) return;
    video.muted = true;
    video.play().catch(() => {});
  }
}
