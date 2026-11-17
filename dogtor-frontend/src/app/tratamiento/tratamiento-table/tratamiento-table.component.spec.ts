import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TratamientoTableComponent } from './tratamiento-table.component';

describe('TratamientoTableComponent', () => {
  let component: TratamientoTableComponent;
  let fixture: ComponentFixture<TratamientoTableComponent>;

  beforeEach(() => {
    history.replaceState({}, '');
    TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      imports: [HttpClientTestingModule, RouterTestingModule, FormsModule],
      declarations: [TratamientoTableComponent]
    });
    fixture = TestBed.createComponent(TratamientoTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
