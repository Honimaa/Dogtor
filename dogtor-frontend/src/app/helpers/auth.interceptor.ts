import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Router } from '@angular/router';
import { EMPTY, Observable, catchError, throwError } from 'rxjs';
import { AuthService } from '../service/auth.service';
import { NotificacionService } from '../service/notificacion.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(
    private authService: AuthService,
    private router: Router,
    private notificacion: NotificacionService
  ) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const token = localStorage.getItem('token');
    if (token) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }
    return next.handle(request).pipe(
      catchError((err: HttpErrorResponse) => {
        // Si el servidor ya no reconoce la sesión guardada (expiró o el backend cambió de clave),
        // se cierra la sesión local y se pide iniciar sesión de nuevo en vez de mostrar "Unauthorized".
        const esLogin = request.url.includes('/auth/inicio_sesion');
        if (err.status === 401 && token && !esLogin) {
          // Varias peticiones pueden fallar a la vez: solo la primera avisa y redirige
          if (localStorage.getItem('token')) {
            this.authService.logout();
            this.notificacion.advertencia('Tu sesión expiró. Vuelve a iniciar sesión.');
            this.router.navigate(['/inicio-sesion']);
          }
          // La pantalla anterior ya no importa: no se propaga el error para no mostrar "Unauthorized"
          return EMPTY;
        }
        return throwError(() => err);
      })
    );
  }
}
