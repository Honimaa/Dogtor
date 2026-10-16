import { Component, OnInit } from '@angular/core';
import { Publicacion } from 'src/app/model/publicacion';
import { PublicacionService } from 'src/app/service/publicacion.service';
import { mensajeError } from 'src/app/model/dogtor';
import { AuthService } from 'src/app/service/auth.service';
import { NotificacionService } from 'src/app/service/notificacion.service';

type Tipo = 'PERDIDA' | 'ENCONTRADA';

@Component({
  selector: 'app-publicacion',
  templateUrl: './publicacion.component.html',
  styleUrls: ['./publicacion.component.css']
})
export class PublicacionComponent implements OnInit {
  publicaciones: Publicacion[] = [];
  cargando = true;

  // Formulario
  modoPublicacion: Tipo = 'PERDIDA';
  nueva: Publicacion = this.vacia();
  enviando = false;
  error = '';
  exito = '';

  // Listado: qué publicaciones se están viendo
  verTipo: Tipo = 'PERDIDA';
  ocultarResueltas = true;

  // Sesión: publicar y resolver avisos requiere iniciar sesión
  sesionIniciada = false;
  usuarioActual: string | null = null;
  esAdmin = false;
  // Avisos que están mostrando la animación de "¡Reencuentro!"
  celebrando = new Set<number>();

  constructor(
    private publicacionService: PublicacionService,
    private authService: AuthService,
    private notificacion: NotificacionService
  ) {}

  ngOnInit(): void {
    this.sesionIniciada = this.authService.isLoggedIn() && !!localStorage.getItem('token');
    this.usuarioActual = this.authService.getUsername();
    this.esAdmin = this.authService.getUserType() === 'Admin';
    this.cargarPublicaciones();
  }

  cargarPublicaciones(): void {
    this.cargando = true;
    this.publicacionService.listar().subscribe({
      next: (p) => {
        this.publicaciones = p;
        this.cargando = false;
      },
      error: () => this.cargando = false
    });
  }

  get publicacionesFiltradas(): Publicacion[] {
    return this.publicaciones.filter(p => p.tipo === this.verTipo && (!this.ocultarResueltas || !p.resuelta));
  }

  contar(tipo: Tipo): number {
    return this.publicaciones.filter(p => p.tipo === tipo && !p.resuelta).length;
  }

  cambiarModo(modo: Tipo): void {
    this.modoPublicacion = modo;
    this.error = '';
    this.exito = '';
  }

  guardarPublicacion(): void {
    const n = this.nueva;
    if (!n.lugar.trim() || !n.contactoNombre.trim() || !n.contactoTelefono.trim()) {
      this.error = 'Indica el lugar y tus datos de contacto para que puedan comunicarse contigo.';
      return;
    }
    this.enviando = true;
    this.error = '';
    this.publicacionService.publicar({ ...n, tipo: this.modoPublicacion }).subscribe({
      next: () => {
        this.enviando = false;
        // Se muestra el listado del mismo tipo para que la persona vea su publicación
        this.verTipo = this.modoPublicacion;
        this.exito = 'Tu publicación ya es visible para todos en la lista de abajo.';
        this.nueva = this.vacia();
        this.cargarPublicaciones();
        setTimeout(() => document.getElementById('listado')?.scrollIntoView({ behavior: 'smooth' }), 300);
      },
      error: (err) => {
        this.enviando = false;
        this.error = mensajeError(err, 'No se pudo publicar. Inténtalo de nuevo.');
      }
    });
  }

  puedeResolver(pub: Publicacion): boolean {
    return this.sesionIniciada && (this.esAdmin || (!!pub.autorUsuario && pub.autorUsuario === this.usuarioActual));
  }

  // Sin ventana de confirmación: la tarjeta celebra el reencuentro y luego pasa a "resuelto"
  marcarResuelta(pub: Publicacion): void {
    const id = pub.idPublicacion;
    if (!id || this.celebrando.has(id)) {
      return;
    }
    this.publicacionService.marcarResuelta(id).subscribe({
      next: () => {
        this.celebrando.add(id);
        setTimeout(() => {
          pub.resuelta = true;
          this.celebrando.delete(id);
        }, 2200);
      },
      error: (err) => this.notificacion.error(mensajeError(err, 'No se pudo actualizar el aviso.'))
    });
  }

  // Comprime la foto a máximo 700 px para que la publicación cargue rápido
  convertirImagen(event: Event): void {
    const archivo = (event.target as HTMLInputElement).files?.[0];
    if (!archivo || !archivo.type.startsWith('image/')) {
      return;
    }
    const lector = new FileReader();
    lector.onload = () => {
      const img = new Image();
      img.onload = () => {
        const escala = Math.min(1, 700 / Math.max(img.width, img.height));
        const canvas = document.createElement('canvas');
        canvas.width = Math.round(img.width * escala);
        canvas.height = Math.round(img.height * escala);
        canvas.getContext('2d')!.drawImage(img, 0, 0, canvas.width, canvas.height);
        this.nueva.foto = canvas.toDataURL('image/jpeg', 0.82);
      };
      img.src = lector.result as string;
    };
    lector.readAsDataURL(archivo);
  }

  enlaceWhatsapp(pub: Publicacion): string {
    const numero = pub.contactoTelefono.replace(/\D/g, '');
    const mensaje = pub.tipo === 'PERDIDA'
      ? `Hola, vi tu publicación en Dogtor Encuentra sobre ${pub.nombreMascota || 'tu mascota'}. Creo que tengo información.`
      : 'Hola, vi en Dogtor Encuentra la mascota que encontraste. Creo que es mía.';
    return `https://wa.me/57${numero}?text=${encodeURIComponent(mensaje)}`;
  }

  private vacia(): Publicacion {
    return {
      tipo: 'PERDIDA', nombreMascota: '', especie: 'Perro', descripcion: '', lugar: '', ciudad: '',
      fechaSuceso: new Date().toISOString().slice(0, 10), foto: '', contactoNombre: '', contactoTelefono: ''
    };
  }
}
