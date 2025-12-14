export interface ReservationDto {
  id: number;
  userId: number;
  reservationNumber: string;
  createdAt: string;
  ticketIds: number[];
}
