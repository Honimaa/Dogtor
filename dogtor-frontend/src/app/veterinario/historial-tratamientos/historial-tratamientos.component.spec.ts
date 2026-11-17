import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HistorialTratamientosComponent } from './historial-tratamientos.component';

describe('HistorialTratamientosComponent', () => {
  let component: HistorialTratamientosComponent;
  let fixture: ComponentFixture<HistorialTratamientosComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      imports: [HttpClientTestingModule, RouterTestingModule, FormsModule],
      declarations: [HistorialTratamientosComponent]
    });
    fixture = TestBed.createComponent(HistorialTratamientosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
