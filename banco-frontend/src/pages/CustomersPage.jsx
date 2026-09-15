import React, { useEffect, useState } from "react";
import api from "../api/axiosConfig";

const emptyForm = { firstName: "", lastName: "", accountNumber: "", balance: "" };

export default function CustomersPage() {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const loadCustomers = async () => {
    setLoading(true);
    setError("");
    try {
      const res = await api.get("/customers");
      setCustomers(res.data);
    } catch (err) {
      setError("No se pudo cargar la lista de clientes. ¿Está corriendo el backend en :8080?");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCustomers();
  }, []);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const resetForm = () => {
    setForm(emptyForm);
    setEditingId(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    setSubmitting(true);
    const payload = { ...form, balance: parseFloat(form.balance) };
    try {
      if (editingId) {
        // Endpoint opcional PUT /api/customers/{id} — agrégalo en el backend si quieres esta función.
        await api.put(`/customers/${editingId}`, payload);
        setSuccess("Cliente actualizado correctamente.");
      } else {
        await api.post("/customers", payload);
        setSuccess("Cliente creado correctamente.");
      }
      resetForm();
      loadCustomers();
    } catch (err) {
      setError(err.response?.data?.message || "No se pudo guardar el cliente. Revisa los datos.");
    } finally {
      setSubmitting(false);
    }
  };

  const handleEdit = (customer) => {
    setEditingId(customer.id);
    setForm({
      firstName: customer.firstName,
      lastName: customer.lastName,
      accountNumber: customer.accountNumber,
      balance: customer.balance,
    });
  };

  const handleDelete = async (id) => {
    if (!window.confirm("¿Eliminar este cliente?")) return;
    try {
      // Endpoint opcional DELETE /api/customers/{id} — agrégalo en el backend si quieres esta función.
      await api.delete(`/customers/${id}`);
      loadCustomers();
    } catch (err) {
      setError("No se pudo eliminar el cliente.");
    }
  };

  return (
    <div className="page">
      <div className="page-header">
        <h1>Clientes</h1>
        <p>Consulta los clientes registrados y da de alta nuevas cuentas.</p>
      </div>

      {error && <div className="alert alert-error">{error}</div>}
      {success && <div className="alert alert-success">{success}</div>}

      <div className="card" style={{ marginBottom: 24 }}>
        <h3 style={{ marginBottom: 16, fontSize: 16 }}>
          {editingId ? "Editar cliente" : "Crear nuevo cliente"}
        </h3>
        <form className="form-grid" onSubmit={handleSubmit}>
          <div className="field">
            <label>Nombre</label>
            <input name="firstName" value={form.firstName} onChange={handleChange} required />
          </div>
          <div className="field">
            <label>Apellido</label>
            <input name="lastName" value={form.lastName} onChange={handleChange} required />
          </div>
          <div className="field">
            <label>Número de cuenta</label>
            <input name="accountNumber" value={form.accountNumber} onChange={handleChange} required />
          </div>
          <div className="field">
            <label>Saldo inicial</label>
            <input
              name="balance"
              type="number"
              step="0.01"
              min="0"
              value={form.balance}
              onChange={handleChange}
              required
            />
          </div>
          <div style={{ display: "flex", gap: 10 }}>
            <button className="btn-primary" type="submit" disabled={submitting}>
              {submitting ? "Guardando..." : editingId ? "Guardar cambios" : "Crear cliente"}
            </button>
            {editingId && (
              <button className="btn-secondary" type="button" onClick={resetForm}>
                Cancelar
              </button>
            )}
          </div>
        </form>
      </div>

      <div className="card">
        <h3 style={{ marginBottom: 16, fontSize: 16 }}>Clientes registrados</h3>
        {loading ? (
          <div className="empty-state">Cargando...</div>
        ) : customers.length === 0 ? (
          <div className="empty-state">Aún no hay clientes registrados.</div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Nombre</th>
                <th>Cuenta</th>
                <th>Saldo</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {customers.map((c) => (
                <tr key={c.id}>
                  <td>{c.firstName} {c.lastName}</td>
                  <td>{c.accountNumber}</td>
                  <td><span className="balance-pill">${Number(c.balance).toLocaleString("es-CO")}</span></td>
                  <td style={{ textAlign: "right", display: "flex", gap: 8, justifyContent: "flex-end" }}>
                    <button className="btn-secondary" onClick={() => handleEdit(c)}>Editar</button>
                    <button className="btn-danger" onClick={() => handleDelete(c.id)}>Borrar</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
