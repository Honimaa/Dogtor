import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientePaginaPrincipalComponent } from './cliente-pagina-principal.component';

describe('ClientePaginaPrincipalComponent', () => {
  let component: ClientePaginaPrincipalComponent;
  let fixture: ComponentFixture<ClientePaginaPrincipalComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      imports: [HttpClientTestingModule, RouterTestingModule, FormsModule],
      declarations: [ClientePaginaPrincipalComponent]
    });
    fixture = TestBed.createComponent(ClientePaginaPrincipalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
