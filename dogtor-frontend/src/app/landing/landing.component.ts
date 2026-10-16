import { Component, OnInit, HostListener, AfterViewInit, ElementRef, ViewChildren, QueryList, OnDestroy } from '@angular/core';
import { ServicioService } from 'src/app/service/servicio.service';
import { TestimonioService } from 'src/app/service/testimonio.service';
import { UrgenciasService } from 'src/app/service/urgencias.service';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { ViewportScroller } from '@angular/common';

@Component({
  selector: 'app-landing',
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.css'],
  providers: [DatePipe]
})
export class LandingComponent implements OnInit, AfterViewInit, OnDestroy {
  servicios: any[] = [];
  isLoading = true;
  errorMessage = '';
  testimonios: any[] = [];
  isLoadingTestimonios = true;
  errorTestimonios = '';
  sedesUrgencias = 0;

  // Variables para el carrusel
  currentSlide = 0;
  carouselItems = [
    {
      title: 'La salud de tu mascota, organizada en un solo lugar',
      description: 'Historias clínicas digitales, citas en línea con disponibilidad real y recordatorios de vacunas y controles.',
      background: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?w=1600'
    },
    {
      title: 'Cuidado profesional las 24 horas',
      description: 'Consulta en tiempo real qué sedes tienen un doctor de turno para atender urgencias.',
      background: 'https://images.unsplash.com/photo-1628009368231-7bb7cfcb0def?w=1600'
    },
    {
      title: 'Adopta y cambia una vida',
      description: 'Refugios validados por Dogtor publican a sus rescatados. Envía tu solicitud sin registrarte.',
      background: 'https://images.unsplash.com/photo-1450778869180-41d0601e046e?w=1600'
    },
    {
      title: 'Perros, gatos y toda la familia',
      description: 'Una plataforma que conecta clínicas, propietarios y fundaciones de protección animal.',
      background: 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?w=1600'
    }
  ];
  private carouselInterval: any;
  private imagesLoaded = 0;

  // Las 7 funcionalidades del producto (Documento de Requerimientos de Software, sección 4)
  modulos = [
    {
      icono: 'document-text-outline',
      titulo: 'Historias clínicas digitales',
      descripcion: 'Vacunas, diagnósticos, tratamientos, cirugías y alergias de cada mascota, ordenados y con alertas.'
    },
    {
      icono: 'calendar-outline',
      titulo: 'Citas en línea',
      descripcion: 'Disponibilidad real de cada doctor y bloqueo automático de horarios para evitar cruces.'
    },
    {
      icono: 'medkit-outline',
      titulo: 'Doctores y urgencias 24h',
      descripcion: 'Especialidades, turnos y sedes con atención de urgencias visibles en tiempo real.'
    },
    {
      icono: 'cube-outline',
      titulo: 'Inventario',
      descripcion: 'Alertas de bajo stock y vencimiento de medicamentos, con lotes y entradas registradas.'
    },
    {
      icono: 'receipt-outline',
      titulo: 'Ventas integradas',
      descripcion: 'Facturación de productos y servicios conectada con el historial clínico del paciente.'
    },
    {
      icono: 'notifications-outline',
      titulo: 'Recordatorios',
      descripcion: 'Avisos de citas, vacunas y controles periódicos para que ningún tratamiento se interrumpa.'
    },
    {
      icono: 'heart-outline',
      titulo: 'Adopción responsable',
      descripcion: 'Refugios validados publican mascotas y gestionan solicitudes de adopción en un solo lugar.'
    }
  ];

  @ViewChildren('section') sections!: QueryList<ElementRef>;

  constructor(
    private servicioService: ServicioService,
    private testimonioService: TestimonioService,
    private urgenciasService: UrgenciasService,
    private datePipe: DatePipe,
    private route: ActivatedRoute,
    private viewportScroller: ViewportScroller
  ) {}

  ngOnInit(): void {
    this.preloadImages().then(() => {
      this.startCarousel();
    });
    this.cargarServicios();
    this.cargarTestimonios();
    this.urgenciasService.obtenerSedes().subscribe({
      next: (sedes) => this.sedesUrgencias = sedes.filter(s => s.disponibleAhora).length,
      error: () => this.sedesUrgencias = 0
    });
  }

  ngAfterViewInit(): void {
    setTimeout(() => this.checkScroll(), 500);
  }

  ngOnDestroy(): void {
    if (this.carouselInterval) {
      clearInterval(this.carouselInterval);
    }
  }

  // Precarga de imágenes
  preloadImages(): Promise<void> {
    return new Promise((resolve) => {
      const loadPromises = this.carouselItems.map(item => {
        return new Promise<void>((imgResolve) => {
          const img = new Image();
          img.onload = () => {
            this.imagesLoaded++;
            imgResolve();
          };
          img.onerror = () => {
            console.warn(`No se pudo cargar la imagen: ${item.background}`);
            imgResolve();
          };
          img.src = item.background;
        });
      });

      Promise.all(loadPromises).then(() => resolve());
    });
  }

  // Manejo del carrusel
  startCarousel(): void {
    this.carouselInterval = setInterval(() => {
      this.nextSlide();
    }, 5000);
  }

  nextSlide(): void {
    this.currentSlide = (this.currentSlide + 1) % this.carouselItems.length;
  }

  prevSlide(): void {
    this.currentSlide = (this.currentSlide - 1 + this.carouselItems.length) % this.carouselItems.length;
  }

  goToSlide(index: number): void {
    this.currentSlide = index;
    this.resetCarouselInterval();
  }

  resetCarouselInterval(): void {
    if (this.carouselInterval) {
      clearInterval(this.carouselInterval);
    }
    this.startCarousel();
  }

  // Carga de datos
  cargarServicios(): void {
    this.servicioService.obtenerServicios().subscribe({
      next: (data) => {
        this.servicios = data;
        this.isLoading = false;
        setTimeout(() => this.checkScroll(), 100);
      },
      error: (err) => {
        console.error('Error al cargar servicios:', err);
        this.errorMessage = 'Error al cargar los servicios';
        this.isLoading = false;
      }
    });
  }

  cargarTestimonios(): void {
    this.testimonioService.obtenerTestimonios().subscribe({
      next: (data: any[]) => {
        this.testimonios = data;
        this.isLoadingTestimonios = false;
        setTimeout(() => this.checkScroll(), 100);
      },
      error: (err) => {
        console.error('Error al cargar testimonios:', err);
        this.errorTestimonios = 'Error al cargar los testimonios';
        this.isLoadingTestimonios = false;
      }
    });
  }

  // Animaciones al hacer scroll
  @HostListener('window:scroll', ['$event'])
  onWindowScroll() {
    this.checkScroll();
  }

  checkScroll() {
    const windowHeight = window.innerHeight;

    this.sections.forEach(section => {
      const sectionElement = section.nativeElement;
      const sectionTop = sectionElement.getBoundingClientRect().top;

      if (sectionTop < windowHeight * 0.75) {
        sectionElement.classList.add('in-view');
      }
    });
  }

  // Métodos auxiliares
  getRatingStars(testimonio: any): string {
    if (!testimonio || !testimonio.calificacion) return '⭐';
    return '⭐'.repeat(Math.min(Math.max(1, testimonio.calificacion), 5));
  }

  formatDate(testimonio: any): string {
    if (!testimonio || !testimonio.fecha) return '';
    try {
      return this.datePipe.transform(testimonio.fecha, 'dd/MM/yyyy') || '';
    } catch (e) {
      console.error('Error formateando fecha:', e);
      return '';
    }
  }

  reintentarCarga(): void {
    this.isLoadingTestimonios = true;
    this.errorTestimonios = '';
    this.cargarTestimonios();
  }

  // Navegación a secciones
  scrollToSection(sectionId: string): void {
    this.viewportScroller.scrollToAnchor(sectionId);
  }
}
