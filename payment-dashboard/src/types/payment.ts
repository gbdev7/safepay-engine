export type PaymentStatus = 'PENDING' | 'SUCCESS' | 'FAILED';

export interface PaymentRequest {
    idempotencyKey: string;
    amount: number;
    currency: string;
}

export interface PaymentResponse {
    id: string;
    idempotencyKey: string;
    amount: number;
    currency: string;
    status: PaymentStatus;
    createdAt: string;
}