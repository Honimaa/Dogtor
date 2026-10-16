// "Dogtor Encuentra": PERDIDA = el dueño busca a su mascota; ENCONTRADA = alguien halló una mascota y busca al dueño
export interface Publicacion {
  idPublicacion?: number;
  tipo: 'PERDIDA' | 'ENCONTRADA';
  nombreMascota?: string;
  especie?: string;
  descripcion?: string;
  lugar: string;
  ciudad?: string;
  fechaSuceso?: string;
  foto?: string;
  autorUsuario?: string;
  contactoNombre: string;
  contactoTelefono: string;
  fechaPublicacion?: string;
  resuelta?: boolean;
}
