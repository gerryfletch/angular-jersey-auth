import { TestBed, inject } from '@angular/core/testing';

import { InterceptorRequestsService } from './interceptor-requests.service';

describe('InterceptorRequestsService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [InterceptorRequestsService]
    });
  });

  it('should be created', inject([InterceptorRequestsService], (service: InterceptorRequestsService) => {
    expect(service).toBeTruthy();
  }));
});
