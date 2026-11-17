import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VerCitasVeterinarioComponent } from './ver-citas-veterinario.component';

describe('VerCitasVeterinarioComponent', () => {
  let component: VerCitasVeterinarioComponent;
  let fixture: ComponentFixture<VerCitasVeterinarioComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      imports: [HttpClientTestingModule, RouterTestingModule, FormsModule],
      declarations: [VerCitasVeterinarioComponent]
    });
    fixture = TestBed.createComponent(VerCitasVeterinarioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
