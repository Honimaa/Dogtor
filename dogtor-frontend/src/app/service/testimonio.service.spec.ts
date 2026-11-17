import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { TestBed } from '@angular/core/testing';

import { TestimonioService } from './testimonio.service';

describe('TestimonioService', () => {
  let service: TestimonioService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule]
    });
    service = TestBed.inject(TestimonioService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
