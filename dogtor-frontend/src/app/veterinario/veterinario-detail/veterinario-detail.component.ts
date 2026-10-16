import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { VeterinarioCL } from 'src/app/model/veterinario-cl';
import { VeterinarioService } from 'src/app/service/veterinario.service';
import { AuthService } from 'src/app/service/auth.service';

@Component({
  selector: 'app-veterinario-detail',
  templateUrl: './veterinario-detail.component.html',
  styleUrls: ['./veterinario-detail.component.css']
})
export class VeterinarioDetailComponent implements OnInit {
  veterinario!: VeterinarioCL;
  esVeterinario = false;

  constructor(
    private route: ActivatedRoute, 
    private veterinarioService: VeterinarioService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.esVeterinario = this.authService.getUserType() === 'Veterinario';
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.veterinarioService.obtenerVeterinarioPorId(id).subscribe(veterinario => {
      this.veterinario = veterinario;
    });
  }
}