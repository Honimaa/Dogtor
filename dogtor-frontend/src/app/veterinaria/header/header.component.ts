import { Component, HostListener, OnInit } from '@angular/core';
import { AuthService } from 'src/app/service/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
  userType: string | null = null;
  userName: string = '';
  userPhoto: string = '';
  userId: number | null = null;
  isMenuOpen: boolean = false;
  isNavOpen: boolean = false;
  isScrolled: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.userType = this.authService.getUserType();
    this.userId = this.authService.getUserId();
    this.userName = this.authService.getUserName();
    this.userPhoto = this.authService.getUserPhoto();
  }

  @HostListener('window:scroll')
  onScroll(): void {
    this.isScrolled = window.scrollY > 10;
  }

  toggleMenu(): void {
    this.isMenuOpen = !this.isMenuOpen;
  }

  toggleNav(): void {
    this.isNavOpen = !this.isNavOpen;
  }

  closeNav(): void {
    this.isNavOpen = false;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/home']).then(() => {
      window.location.reload();
    });
  }

  getMascotasCliente(): any[] {
    return ['/mascotas-cliente', this.userId];
  }
}
