import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { of } from 'rxjs';
import { AdministradorCL } from 'src/app/model/administrador-cl';
import { AdminService } from 'src/app/service/admin.service';
import { AdminDetailComponent } from './admin-detail.component';

describe('AdminDetailComponent', () => {
  let component: AdminDetailComponent;
  let fixture: ComponentFixture<AdminDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      imports: [HttpClientTestingModule, RouterTestingModule, FormsModule],
      declarations: [AdminDetailComponent],
      providers: [{ provide: AdminService, useValue: { getAdmin: () => of(new AdministradorCL()) } }]
    });
    fixture = TestBed.createComponent(AdminDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
