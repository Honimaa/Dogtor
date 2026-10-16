// Modelos de los módulos Dogtor: historia clínica, adopción, urgencias, recordatorios e inventario.

export type TipoEventoClinico = 'VACUNA' | 'DIAGNOSTICO' | 'TRATAMIENTO' | 'CIRUGIA' | 'ALERGIA';

export interface EventoClinico {
  idEvento?: number;
  tipo: TipoEventoClinico;
  titulo: string;
  descripcion?: string;
  fecha: string;
  fechaProxima?: string | null;
  fechaRegistro?: string;
  veterinario?: { idVeterinario: number; nombre: string; especialidad?: string } | null;
}

export const TIPOS_EVENTO: { valor: TipoEventoClinico; etiqueta: string; icono: string }[] = [
  { valor: 'VACUNA', etiqueta: 'Vacuna', icono: 'shield-checkmark-outline' },
  { valor: 'DIAGNOSTICO', etiqueta: 'Diagnóstico', icono: 'search-outline' },
  { valor: 'TRATAMIENTO', etiqueta: 'Tratamiento', icono: 'bandage-outline' },
  { valor: 'CIRUGIA', etiqueta: 'Cirugía', icono: 'cut-outline' },
  { valor: 'ALERGIA', etiqueta: 'Alergia', icono: 'alert-circle-outline' }
];

export interface Refugio {
  idRefugio?: number;
  nombre: string;
  ciudad: string;
  telefono?: string;
  correo?: string;
  descripcion?: string;
  logo?: string;
  validado?: boolean;
}

export type EstadoAdopcion = 'DISPONIBLE' | 'EN_PROCESO' | 'ADOPTADA';

export interface MascotaAdopcion {
  idMascotaAdopcion?: number;
  nombre: string;
  especie: 'PERRO' | 'GATO' | 'OTRO';
  raza?: string;
  edadMeses?: number;
  tamano?: 'PEQUENO' | 'MEDIANO' | 'GRANDE';
  sexo?: string;
  ciudad: string;
  descripcion?: string;
  estadoSalud?: string;
  foto: string;
  estado?: EstadoAdopcion;
  fechaPublicacion?: string;
  refugio?: Refugio;
}

export type EstadoSolicitud = 'PENDIENTE' | 'EN_REVISION' | 'APROBADA' | 'RECHAZADA';

export interface SolicitudAdopcion {
  idSolicitud?: number;
  nombreSolicitante: string;
  correo: string;
  telefono: string;
  ciudad?: string;
  mensaje?: string;
  fecha?: string;
  estado?: EstadoSolicitud;
  mascotaAdopcion?: MascotaAdopcion;
}

export interface DoctorUrgencias {
  idVeterinario: number;
  nombre: string;
  especialidad: string;
  foto: string;
  horario?: string;
}

export interface SedeUrgencias {
  sede: string;
  disponibleAhora: boolean;
  doctores: DoctorUrgencias[];
}

export interface Recordatorio {
  tipo: 'CITA' | 'VACUNA' | 'CONTROL';
  titulo: string;
  mensaje: string;
  fecha: string;
  idMascota: number;
  nombreMascota: string;
  nombrePropietario?: string;
  correoPropietario?: string;
  urgente: boolean;
}

export interface MedicamentoInventario {
  idMedicamento: number;
  nombre: string;
  precioCompra: number;
  precioVenta: number;
  unidadesDisponibles: number;
  unidadesVendidas: number;
  stockMinimo?: number;
  fechaVencimiento?: string | null;
  lote?: string;
  categoria?: string;
}

export interface AlertasInventario {
  bajoStock: MedicamentoInventario[];
  porVencer: MedicamentoInventario[];
  vencidos: MedicamentoInventario[];
}

/** Texto legible de la edad a partir de meses. */
export function edadLegible(meses?: number | null): string {
  if (meses == null) {
    return 'Edad desconocida';
  }
  if (meses < 12) {
    return `${meses} ${meses === 1 ? 'mes' : 'meses'}`;
  }
  const anios = Math.floor(meses / 12);
  return `${anios} ${anios === 1 ? 'año' : 'años'}`;
}

/** Extrae el mensaje de error que envía el backend (server.error.include-message=always). */
export function mensajeError(err: any, porDefecto = 'Ocurrió un error. Inténtalo de nuevo.'): string {
  if (typeof err?.error === 'string' && err.error.trim()) {
    return err.error;
  }
  return err?.error?.message || porDefecto;
}
