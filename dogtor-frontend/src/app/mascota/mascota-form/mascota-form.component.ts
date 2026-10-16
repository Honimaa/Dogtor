import {
  Component,
  EventEmitter,
  Output,
  ViewChild,
  OnInit,
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgForm } from '@angular/forms';
import { MascotaCL } from 'src/app/model/mascota-cl';
import { MascotaService } from 'src/app/service/mascota.service';
import { ClienteCL } from 'src/app/model/cliente-cl';
import { ClienteService } from 'src/app/service/cliente.service';
import { AuthService } from 'src/app/service/auth.service';

@Component({
  selector: 'app-mascota-form',
  templateUrl: './mascota-form.component.html',
  styleUrls: ['./mascota-form.component.css'],
})
export class MascotaFormComponent implements OnInit {
  @ViewChild('mascotaForm') mascotaForm!: NgForm;
  @Output() addMascotaEvent = new EventEmitter<MascotaCL>();

  formMascota: MascotaCL = new MascotaCL();

  editMode = false;

  clientes: ClienteCL[] = [];

  // Dogtor: el propietario registra sus propias mascotas desde "Mis mascotas"
  esCliente = false;
  idClienteSesion: number | null = null;
  errorFoto = '';
  errorGuardado = '';

  constructor(
    private mascotaService: MascotaService,
    private clienteService: ClienteService,
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.esCliente = this.authService.getUserType() === 'Cliente';
    this.idClienteSesion = this.authService.getUserId();
    if (this.esCliente) {
      this.formMascota.clienteId = this.idClienteSesion!;
      this.formMascota.estado = 1;
      return;
    }
    this.formMascota.estado = 1;
    this.clienteService.findAll().subscribe((clientes) => {
      this.clientes = clientes;

      const id = this.route.snapshot.paramMap.get('id');
      if (id) {
        this.editMode = true;
        this.mascotaService.getMascota(+id).subscribe((mascota) => {
          if (mascota) {
            this.formMascota = new MascotaCL(
              mascota.idMascota,
              mascota.nombre,
              mascota.raza,
              mascota.edad,
              mascota.peso,
              mascota.enfermedad,
              mascota.foto,
              mascota.fechaNacimiento,
              mascota.fechaIngreso,
              mascota.fechaSalida,
              mascota.estado,
              mascota.cliente
            );
            console.log('Mascota:', mascota);
            console.log('Cliente ID asignado:', this.formMascota.clienteId);
          }
        });
      }
    });
  }

  guardarMascota() {
    this.markAllAsTouched();

    if (this.mascotaForm.valid) {
      if (this.editMode) {
        this.mascotaService
          .editarMascota(this.formMascota.idMascota, this.formMascota)
          .subscribe(() => {
            this.router.navigate(['/mascotas']);
          });
      } else {
        const idCliente = this.esCliente ? this.idClienteSesion : this.formMascota.clienteId;

        if (idCliente) {
          const datos = this.formMascota.toBackendFormat();
          if (this.esCliente) {
            datos.fechaIngreso = new Date().toISOString().split('T')[0];
            datos.estado = 1;
          }
          if (!datos.foto) {
            datos.foto = 'assets/mascota-placeholder.svg';
          }
          this.errorGuardado = '';
          this.mascotaService
            .agregarMascota(datos, idCliente)
            .subscribe({
              next: () => {
                this.router.navigate(this.esCliente ? ['/mascotas-cliente', idCliente] : ['/mascotas']);
              },
              error: () => this.errorGuardado = 'No se pudo registrar la mascota. Inténtalo de nuevo.'
            });
        } else {
          console.error('ID del cliente no definido.');
        }
      }
    }
  }

  private markAllAsTouched() {
    Object.keys(this.mascotaForm.controls).forEach((field) => {
      const control = this.mascotaForm.controls[field];
      control.markAsTouched();
    });
  }

  resetForm() {
    this.mascotaForm.resetForm({
      idMascota: 0,
      estado: 1,
      clienteId: 0,
      edad: null,
      peso: null,
    });

    this.formMascota = new MascotaCL();
  }

  // Reduce la foto a máximo 600 px y la guarda como JPEG para no sobrecargar la base de datos
  subirFoto(event: Event): void {
    const archivo = (event.target as HTMLInputElement).files?.[0];
    this.errorFoto = '';
    if (!archivo) {
      return;
    }
    if (!archivo.type.startsWith('image/')) {
      this.errorFoto = 'El archivo debe ser una imagen.';
      return;
    }
    const lector = new FileReader();
    lector.onload = () => {
      const img = new Image();
      img.onload = () => {
        const escala = Math.min(1, 600 / Math.max(img.width, img.height));
        const canvas = document.createElement('canvas');
        canvas.width = Math.round(img.width * escala);
        canvas.height = Math.round(img.height * escala);
        canvas.getContext('2d')!.drawImage(img, 0, 0, canvas.width, canvas.height);
        this.formMascota.foto = canvas.toDataURL('image/jpeg', 0.82);
      };
      img.onerror = () => this.errorFoto = 'No se pudo leer la imagen.';
      img.src = lector.result as string;
    };
    lector.readAsDataURL(archivo);
  }

  volver(): void {
    window.history.back();
  }
}
