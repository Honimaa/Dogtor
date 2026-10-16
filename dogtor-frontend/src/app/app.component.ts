import { Component, OnDestroy, OnInit } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'Dogtor';
  userType = 'Administrador';

  // Respaldo global: si una foto externa no carga, se muestra una imagen local en lugar del texto alternativo
  private readonly onImageError = (event: Event): void => {
    const img = event.target;
    if (!(img instanceof HTMLImageElement) || img.dataset['fallback']) {
      return;
    }
    img.dataset['fallback'] = 'true';
    const texto = `${img.alt} ${img.className}`.toLowerCase();
    const esPersona = /(cliente|usuario|veterinari|administrador|propietario|doctor|user|vet-img)/.test(texto);
    img.src = esPersona ? 'assets/avatar-placeholder.svg' : 'assets/mascota-placeholder.svg';
  };

  ngOnInit(): void {
    // Los errores de carga de imágenes no burbujean: se escuchan en fase de captura
    document.addEventListener('error', this.onImageError, true);
  }

  ngOnDestroy(): void {
    document.removeEventListener('error', this.onImageError, true);
  }
}
