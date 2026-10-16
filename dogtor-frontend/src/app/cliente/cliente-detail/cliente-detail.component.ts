import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ClienteCL } from 'src/app/model/cliente-cl';
import { MascotaCL } from 'src/app/model/mascota-cl';
import { ClienteService } from 'src/app/service/cliente.service';
import { MascotaService } from 'src/app/service/mascota.service';
import { AuthService } from 'src/app/service/auth.service';

@Component({
  selector: 'app-cliente-detail',
  templateUrl: './cliente-detail.component.html',
  styleUrls: ['./cliente-detail.component.css']
})
export class ClienteDetailComponent implements OnInit {
  cliente!: ClienteCL;
  mascotas: MascotaCL[] = [];
  esPropietario = false;

  constructor(
    private route: ActivatedRoute,
    private clienteService: ClienteService,
    private mascotaService: MascotaService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.esPropietario = this.authService.getUserType() === 'Cliente' && this.authService.getUserId() === id;

    this.clienteService.getCliente(id).subscribe(cliente => {
      this.cliente = cliente;
    });
    this.mascotaService.getMascotasPorCliente(id).subscribe({
      next: (mascotas) => this.mascotas = mascotas,
      error: () => this.mascotas = []
    });
  }
}
