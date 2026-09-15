import React from "react";
import { NavLink } from "react-router-dom";

export default function Navbar() {
  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <span className="navbar-brand-mark" />
        Banco2025
      </div>
      <div className="navbar-links">
        <NavLink to="/" end className={({ isActive }) => `navbar-link${isActive ? " active" : ""}`}>
          Clientes
        </NavLink>
        <NavLink to="/transferir" className={({ isActive }) => `navbar-link${isActive ? " active" : ""}`}>
          Transferir
        </NavLink>
        <NavLink to="/historial" className={({ isActive }) => `navbar-link${isActive ? " active" : ""}`}>
          Historial
        </NavLink>
      </div>
    </nav>
  );
}
