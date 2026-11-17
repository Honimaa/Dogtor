import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MascotasTratamientoComponent } from './mascotas-tratamiento.component';

describe('MascotasTratamientoComponent', () => {
  let component: MascotasTratamientoComponent;
  let fixture: ComponentFixture<MascotasTratamientoComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      imports: [HttpClientTestingModule, RouterTestingModule, FormsModule],
      declarations: [MascotasTratamientoComponent]
    });
    fixture = TestBed.createComponent(MascotasTratamientoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
