import React from "react";
import { Routes, Route } from "react-router-dom";
import Navbar from "./components/Navbar";
import CustomersPage from "./pages/CustomersPage";
import TransferPage from "./pages/TransferPage";
import HistoryPage from "./pages/HistoryPage";

export default function App() {
  return (
    <div className="app-shell">
      <Navbar />
      <Routes>
        <Route path="/" element={<CustomersPage />} />
        <Route path="/transferir" element={<TransferPage />} />
        <Route path="/historial" element={<HistoryPage />} />
      </Routes>
    </div>
  );
}
