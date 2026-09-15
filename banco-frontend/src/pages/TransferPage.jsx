import React, { useState } from "react";
import api from "../api/axiosConfig";

const emptyForm = { senderAccountNumber: "", receiverAccountNumber: "", amount: "" };

export default function TransferPage() {
  const [form, setForm] = useState(emptyForm);
  const [error, setError] = useState("");
  const [receipt, setReceipt] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setReceipt(null);

    if (form.senderAccountNumber === form.receiverAccountNumber) {
      setError("La cuenta de origen y destino no pueden ser la misma.");
      return;
    }
    if (Number(form.amount) <= 0) {
      setError("El monto debe ser mayor a cero.");
      return;
    }

    setSubmitting(true);
    try {
      const res = await api.post("/transactions/transfer", {
        senderAccountNumber: form.senderAccountNumber,
        receiverAccountNumber: form.receiverAccountNumber,
        amount: parseFloat(form.amount),
      });
      setReceipt(res.data);
      setForm(emptyForm);
    } catch (err) {
      setError(err.response?.data?.message || "No se pudo completar la transferencia.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="page">
      <div className="page-header">
        <h1>Transferir dinero</h1>
        <p>Mueve fondos entre dos cuentas registradas en el banco.</p>
      </div>

      {error && <div className="alert alert-error">{error}</div>}
      {receipt && (
        <div className="alert alert-success">
          Transferencia exitosa: ${Number(receipt.amount).toLocaleString("es-CO")} de{" "}
          {receipt.senderAccountNumber} a {receipt.receiverAccountNumber}.
        </div>
      )}

      <div className="card">
        <form className="form-grid" onSubmit={handleSubmit}>
          <div className="field">
            <label>Cuenta origen</label>
            <input
              name="senderAccountNumber"
              value={form.senderAccountNumber}
              onChange={handleChange}
              placeholder="Ej. 123456789"
              required
            />
          </div>
          <div className="field">
            <label>Cuenta destino</label>
            <input
              name="receiverAccountNumber"
              value={form.receiverAccountNumber}
              onChange={handleChange}
              placeholder="Ej. 987654321"
              required
            />
          </div>
          <div className="field">
            <label>Monto</label>
            <input
              name="amount"
              type="number"
              step="0.01"
              min="0.01"
              value={form.amount}
              onChange={handleChange}
              required
            />
          </div>
          <button className="btn-primary" type="submit" disabled={submitting}>
            {submitting ? "Procesando..." : "Confirmar transferencia"}
          </button>
        </form>
      </div>
    </div>
  );
}
