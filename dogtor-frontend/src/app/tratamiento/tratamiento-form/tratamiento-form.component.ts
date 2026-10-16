import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, FormArray, ReactiveFormsModule } from '@angular/forms';
import { MascotaCL } from 'src/app/model/mascota-cl';
import { MedicamentoCL } from 'src/app/model/medicamento-cl';
import { ServicioCL } from 'src/app/model/servicio-cl';
import { TratamientoCL } from 'src/app/model/tratamiento-cl';
import { MascotaService } from 'src/app/service/mascota.service';
import { MedicamentoService } from 'src/app/service/medicamento.service';
import { ServicioService } from 'src/app/service/servicio.service';
import { TratamientoService } from 'src/app/service/tratamiento.service';
import { ActivatedRoute,Router } from '@angular/router';
import { AuthService } from 'src/app/service/auth.service';
import { VeterinarioService } from 'src/app/service/veterinario.service';
import { VeterinarioCL } from 'src/app/model/veterinario-cl';
import { NotificacionService } from 'src/app/service/notificacion.service';
@Component({
  selector: 'app-tratamiento-form',
  templateUrl: './tratamiento-form.component.html',
  styleUrls: ['./tratamiento-form.component.css']
})
export class TratamientoFormComponent implements OnInit{
  tratamientoForm!: FormGroup;
  loading = false;
  successMessage: string | null = null;
  errorMessage: string | null = null;

  servicios: ServicioCL[] = []; 
  mascotas: MascotaCL[] = [];  
  medicamentosDisponibles: MedicamentoCL[] = []; 

  idVeterinario: number=0;
  // Dogtor: el admin elige al doctor; el doctor queda asignado con su sesión
  esAdmin = false;
  veterinarios: VeterinarioCL[] = [];
  idMascotaPreseleccionada: number | null = null;
  constructor(
    private authService: AuthService,
    private veterinarioService: VeterinarioService,
    private notificacion: NotificacionService,
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private tratamientoService: TratamientoService,
    private servicioService: ServicioService,
    private mascotaService: MascotaService,
    private medicamentoService: MedicamentoService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.esAdmin = this.authService.getUserType() === 'Admin';
    const idSesion = this.authService.getUserType() === 'Veterinario' ? this.authService.getUserId() : null;
    this.idVeterinario = Number(history.state?.id || idSesion || 0);
    const idMascota = history.state?.idMascota || this.route.snapshot.queryParamMap.get('mascota');
    this.idMascotaPreseleccionada = idMascota ? Number(idMascota) : null;

    this.tratamientoForm = this.fb.group({
      codigo: [this.codigoAutomatico(), Validators.required],
      fecha: [new Date().toISOString().substring(0, 10), Validators.required],
      idServicio: ['', Validators.required],
      idMascota: [this.idMascotaPreseleccionada ?? '', Validators.required],
      idVeterinario: [this.idVeterinario || '', this.esAdmin ? Validators.required : []],
      detalles: ['', Validators.required],
      medicamentos: this.fb.array([this.createMedicamentoGroup()])
    });

    if (this.esAdmin) {
      this.veterinarioService.obtenerTodosVeterinarios().subscribe({
        next: (vets) => this.veterinarios = vets.filter(v => v.estado === 1)
      });
    }

      this.servicioService.findAll().subscribe({
        next: (serviciodata) => {
          this.servicios=serviciodata;
        },
        error: (err) =>{
          console.error('Error al obtener los servicios',err);
        }
      });

      this.mascotaService.findAll().subscribe({
        next: (mascotadata) =>{
          this.mascotas=mascotadata;
        },
        error: (err) =>{
          console.error('Error al obtener las mascotas',err)
        }
      });

      this.medicamentoService.findAll().subscribe({
        next: (medicamentodata) =>{
          this.medicamentosDisponibles = medicamentodata
        },
        error: (err) =>{
          console.error('Error al obtener los medicamentos',err);
        }
      });
  }

  createMedicamentoGroup(): FormGroup {
    return this.fb.group({
      idMedicamento: ['', Validators.required],
      dosis: ['', Validators.required]
    });
  }

  get medicamentosArray(): FormArray {
    return this.tratamientoForm.get('medicamentos') as FormArray;
  }

  addMedicamento(): void {
    this.medicamentosArray.push(this.createMedicamentoGroup());
  }

  removeMedicamento(index: number): void {
    if (this.medicamentosArray.length > 1) {
      this.medicamentosArray.removeAt(index);
    } else {
      this.showError('Debe haber al menos un medicamento');
    }
  }

  onSubmit(): void {
    if (this.tratamientoForm.invalid) {
      this.showError('Por favor complete todos los campos requeridos');
      return;
    }

    this.loading = true;
    this.successMessage = null;
    this.errorMessage = null;

    const formValue = this.tratamientoForm.value;
    const tratamiento = new TratamientoCL(
      0,
      formValue.codigo,
      new Date(formValue.fecha),
      formValue.detalles
    );

    const idsMedicamentos = formValue.medicamentos.map((m: any) => m.idMedicamento);

    this.tratamientoService.crearTratamiento(
      tratamiento,
      formValue.idMascota,
      formValue.idServicio,
      formValue.idVeterinario,
      idsMedicamentos
    ).subscribe({
      next: () => {
        this.notificacion.exito('Tratamiento registrado y ligado al paciente.');
        const idMascota = Number(formValue.idMascota);
        this.resetForm();
        if (this.idMascotaPreseleccionada) {
          this.router.navigate(['/historia-clinica', idMascota]);
        } else if (this.esAdmin) {
          this.router.navigate(['/tratamiento']);
        } else {
          this.router.navigate(['/historial-tratamientos', this.idVeterinario]);
        }
      },
      error: (err) => {
        this.showError('Error al crear el tratamiento: ' + err.message);
        this.loading = false;
      },
      complete: () => this.loading = false
    });
  }

  resetForm(): void {
    this.tratamientoForm.reset({
      codigo: this.codigoAutomatico(),
      fecha: new Date().toISOString().substring(0, 10),
      idServicio: '',
      idMascota: this.idMascotaPreseleccionada ?? '',
      idVeterinario: this.idVeterinario || '',
      detalles: '',
    });
  }

  private showSuccess(message: string): void {
    this.successMessage = message;
    setTimeout(() => this.successMessage = null, 5000);
  }

  private showError(message: string): void {
    this.errorMessage = message;
    this.loading = false;
  }

  // Código legible y único para el tratamiento, p. ej. TRAT-261016-4821
  private codigoAutomatico(): string {
    const hoy = new Date();
    const fecha = `${String(hoy.getFullYear()).slice(2)}${String(hoy.getMonth() + 1).padStart(2, '0')}${String(hoy.getDate()).padStart(2, '0')}`;
    return `TRAT-${fecha}-${Math.floor(1000 + Math.random() * 9000)}`;
  }

  volver(): void {
    window.history.back();
  }
}
