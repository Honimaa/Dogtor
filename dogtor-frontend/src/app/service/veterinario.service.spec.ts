import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { TestBed } from '@angular/core/testing';

import { VeterinarioService } from './veterinario.service';

describe('VeterinarioService', () => {
  let service: VeterinarioService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule]
    });
    service = TestBed.inject(VeterinarioService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
