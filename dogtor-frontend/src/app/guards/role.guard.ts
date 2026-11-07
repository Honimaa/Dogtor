import { inject } from '@angular/core';
import { CanActivateFn, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthService } from '../service/auth.service';

export const roleGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const expectedRoles: string[] = route.data['roles']; // Los roles esperados
  const userRoles = authService.getUserType();

  // Si no hay sesión iniciada o userRoles es null/undefined
  if (!userRoles) {
    // Dogtor: se envía al login y, al entrar, se vuelve a la página que pidió (p. ej. agendar cita)
    return router.createUrlTree(['/inicio-sesion'], { queryParams: { returnUrl: state.url } });
  }

  const hasRole = expectedRoles.some(role => userRoles.includes(role));

  if (!hasRole) {
    router.navigate(['/inicio-sesion']); // o a una página de acceso denegado
    return false;
  }

  return true;
};
