import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VeterinarioFormComponent } from './veterinario-form.component';

describe('VeterinarioFormComponent', () => {
  let component: VeterinarioFormComponent;
  let fixture: ComponentFixture<VeterinarioFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      imports: [HttpClientTestingModule, RouterTestingModule, FormsModule],
      declarations: [VeterinarioFormComponent]
    });
    fixture = TestBed.createComponent(VeterinarioFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
