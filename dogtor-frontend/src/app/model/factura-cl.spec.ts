import { FacturaCL } from './factura-cl';
import { ClienteCL } from './cliente-cl';

describe('FacturaCL', () => {
  it('should create an instance', () => {
    expect(new FacturaCL(0, new Date(), 0, false, '', new ClienteCL())).toBeTruthy();
  });
});
