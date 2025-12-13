export interface PaymentMethod {
  id: 'CARD' | 'PAYPAL' | 'KLARNA';
  title: string;
  description: string;
  logo: string;
}
