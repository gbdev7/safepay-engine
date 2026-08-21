import axios from 'axios';
import type { PaymentRequest, PaymentResponse } from '../types/payment';

const api = axios.create({
    baseURL: 'http://localhost:8080/api',
});

export const getPayments = async (): Promise<PaymentResponse[]> => {
    const response = await api.get<PaymentResponse[]>('/payments');
    return response.data;
};

export const createPayment = async (data: PaymentRequest): Promise<PaymentResponse> => {
    const response = await api.post<PaymentResponse>('/payments', data);
    return response.data;
};