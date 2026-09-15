import React, { useEffect, useState } from "react";
import api from "../api/axiosConfig";

export default function HistoryPage() {
  const [customers, setCustomers] = useState([]);
  const [selectedAccount, setSelectedAccount] = useState("");
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    api.get("/customers").then((res) => setCustomers(res.data)).catch(() => {});
  }, []);

  useEffect(() => {
    if (!selectedAccount) {
      setTransactions([]);
      return;
    }
    setLoading(true);
    setError("");
    api
      .get(`/transactions/account/${selectedAccount}`)
      .then((res) => setTransactions(res.data))
      .catch(() => setError("No se pudo cargar el historial de esta cuenta."))
      .finally(() => setLoading(false));
  }, [selectedAccount]);

  return (
    <div className="page">
      <div className="page-header">
        <h1>Historial de transacciones</h1>
        <p>Selecciona un cliente para ver sus movimientos.</p>
      </div>

      <div className="card" style={{ marginBottom: 20 }}>
        <div className="field" style={{ maxWidth: 320 }}>
          <label>Cliente</label>
          <select
            className="select-account"
            value={selectedAccount}
            onChange={(e) => setSelectedAccount(e.target.value)}
          >
            <option value="">Selecciona una cuenta...</option>
            {customers.map((c) => (
              <option key={c.id} value={c.accountNumber}>
                {c.firstName} {c.lastName} — {c.accountNumber}
              </option>
            ))}
          </select>
        </div>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      <div className="card">
        {!selectedAccount ? (
          <div className="empty-state">Elige un cliente arriba para ver sus movimientos.</div>
        ) : loading ? (
          <div className="empty-state">Cargando historial...</div>
        ) : transactions.length === 0 ? (
          <div className="empty-state">Esta cuenta no tiene transacciones registradas.</div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Fecha</th>
                <th>Origen</th>
                <th>Destino</th>
                <th>Monto</th>
              </tr>
            </thead>
            <tbody>
              {transactions.map((t) => {
                const isOutgoing = t.senderAccountNumber === selectedAccount;
                return (
                  <tr key={t.id}>
                    <td>{t.timestamp ? new Date(t.timestamp).toLocaleString("es-CO") : "—"}</td>
                    <td>{t.senderAccountNumber}</td>
                    <td>{t.receiverAccountNumber}</td>
                    <td className={isOutgoing ? "amount-out" : "amount-in"}>
                      {isOutgoing ? "-" : "+"}${Number(t.amount).toLocaleString("es-CO")}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
