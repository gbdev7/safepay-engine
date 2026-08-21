import React, { useEffect, useState } from 'react';
import { createPayment, getPayments } from './services/api';
import type { PaymentRequest, PaymentResponse } from './types/payment';
import { RefreshCw, Send, CreditCard, DollarSign } from 'lucide-react';

export default function App() {
  const [payments, setPayments] = useState<PaymentResponse[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [submitting, setSubmitting] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const [idempotencyKey, setIdempotencyKey] = useState<string>('');
  const [amount, setAmount] = useState<string>('');
  const [currency, setCurrency] = useState<string>('BRL');

  const fetchPayments = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getPayments();
      setPayments(data);
    } catch (err) {
      setError('Erro ao conectar com a API Spring Boot.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPayments();
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!idempotencyKey || !amount) return;

    setSubmitting(true);
    setError(null);

    try {
      const payload: PaymentRequest = {
        idempotencyKey,
        amount: parseFloat(amount),
        currency,
      };
      await createPayment(payload);
      setIdempotencyKey('');
      setAmount('');
      await fetchPayments();
    } catch (err) {
      setError('Falha ao processar o pagamento.');
    } finally {
      setSubmitting(false);
    }
  };

  const generateUUID = () => {
    setIdempotencyKey(`PAY-${Math.floor(100000 + Math.random() * 900000)}`);
  };

  return (
      <div className="dashboard-container">
        <div className="header">
          <h1>Processor Dashboard</h1>
          <p>Painel de Controle e Processamento de Pagamentos (Spring Boot + RabbitMQ)</p>
        </div>

        {/* Formulário */}
        <div className="card">
          <h2 className="card-title">
            <CreditCard size={20} color="#2563eb" /> Novo Pagamento
          </h2>

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Chave de Idempotência</label>
              <div className="input-row">
                <input
                    type="text"
                    value={idempotencyKey}
                    onChange={(e) => setIdempotencyKey(e.target.value)}
                    placeholder="Ex: PAY-123456"
                    style={{ flex: 1 }}
                    required
                />
                <button type="button" onClick={generateUUID} className="btn-secondary">
                  Gerar Chave
                </button>
              </div>
            </div>

            <div className="grid-2">
              <div className="form-group">
                <label>Valor</label>
                <input
                    type="number"
                    step="0.01"
                    value={amount}
                    onChange={(e) => setAmount(e.target.value)}
                    placeholder="100.00"
                    required
                />
              </div>
              <div className="form-group">
                <label>Moeda</label>
                <select value={currency} onChange={(e) => setCurrency(e.target.value)}>
                  <option value="BRL">BRL (R$)</option>
                  <option value="USD">USD ($)</option>
                  <option value="EUR">EUR (€)</option>
                </select>
              </div>
            </div>

            <button type="submit" disabled={submitting} className="btn-primary">
              <Send size={18} />
              {submitting ? 'Enviando...' : 'Processar Pagamento'}
            </button>
          </form>

          {error && <p style={{ color: '#ef4444', marginTop: '1rem', fontSize: '0.9rem' }}>{error}</p>}
        </div>

        {/* Tabela */}
        <div className="card">
          <div className="table-header">
            <h2 className="card-title" style={{ margin: 0 }}>
              <DollarSign size={20} color="#16a34a" /> Histórico de Transações
            </h2>
            <button onClick={fetchPayments} disabled={loading} className="btn-secondary" style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
              <RefreshCw size={16} /> Atualizar
            </button>
          </div>

          <table>
            <thead>
            <tr>
              <th>ID</th>
              <th>Chave Idempotência</th>
              <th>Valor</th>
              <th>Status</th>
              <th>Data</th>
            </tr>
            </thead>
            <tbody>
            {payments.length === 0 ? (
                <tr>
                  <td colSpan={5} style={{ textAlign: 'center', color: '#94a3b8', padding: '1.5rem' }}>
                    Nenhum pagamento registrado.
                  </td>
                </tr>
            ) : (
                payments.map((p) => (
                    <tr key={p.id}>
                      <td style={{ color: '#64748b', fontSize: '0.8rem' }}>{p.id.substring(0, 8)}...</td>
                      <td style={{ fontWeight: 600 }}>{p.idempotencyKey}</td>
                      <td>{p.currency} {p.amount.toFixed(2)}</td>
                      <td>
                    <span className={`badge ${p.status === 'PENDING' ? 'badge-pending' : p.status === 'SUCCESS' ? 'badge-success' : 'badge-failed'}`}>
                      {p.status}
                    </span>
                      </td>
                      <td style={{ color: '#64748b', fontSize: '0.85rem' }}>
                        {new Date(p.createdAt).toLocaleString('pt-BR')}
                      </td>
                    </tr>
                ))
            )}
            </tbody>
          </table>
        </div>
      </div>
  );
}