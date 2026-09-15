# Banco2025 – Frontend

Frontend en React para el laboratorio de Arquitectura de Software (banco2025).
Cubre las 3 vistas mínimas exigidas + el extra de editar/borrar clientes.

## Cómo correrlo

```bash
npm install
npm start
```

Se abre en `http://localhost:3000`. El backend debe estar corriendo en
`http://localhost:8080` (ver `src/api/axiosConfig.js` si usas otro puerto).

## IMPORTANTE: habilita CORS en el backend

Sin esto, el navegador bloqueará las peticiones desde `localhost:3000` hacia
`localhost:8080`. Agrega esta clase en tu proyecto Spring:

```java
package com.udea.bancoudea.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
```

## Endpoints que este frontend espera

Algunos ya existen en el PDF, otros los tienes que agregar tú al
`TransactionController` / `CustomerController`:

| Método | Ruta                                | Estado                | Para qué |
|--------|--------------------------------------|------------------------|----------|
| GET    | `/api/customers`                     | ✅ ya está              | Listar clientes |
| POST   | `/api/customers`                     | ✅ ya está              | Crear cliente |
| PUT    | `/api/customers/{id}`                | ⚠️ agrégalo (extra)    | Editar cliente |
| DELETE | `/api/customers/{id}`                | ⚠️ agrégalo (extra)    | Borrar cliente |
| POST   | `/api/transactions/transfer`         | ⚠️ agrégalo             | Transferir dinero (usa `TransactionService.transferMoney`) |
| GET    | `/api/transactions/account/{numero}` | ⚠️ agrégalo             | Historial de una cuenta (usa `getTransactionsForAccount`) |

Ejemplo del `TransactionController` que falta:

```java
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionDTO> transfer(@RequestBody TransactionDTO dto) {
        return ResponseEntity.ok(transactionService.transferMoney(dto));
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<TransactionDTO>> history(@PathVariable String accountNumber) {
        return ResponseEntity.ok(transactionService.getTransactionsForAccount(accountNumber));
    }
}
```

## Estructura

```
src/
  api/axiosConfig.js      -> instancia central de axios (baseURL del backend)
  components/Navbar.jsx   -> navegación entre las 3 vistas
  pages/CustomersPage.jsx -> Vista 1: consultar/crear/editar/borrar clientes
  pages/TransferPage.jsx  -> Vista 2: transferencia entre cuentas
  pages/HistoryPage.jsx   -> Vista 3: historial por cliente
```

En la arquitectura por capas del PDF, esto reemplaza la carpeta `FRONTEND`
(App.js, TransferFound.jsx, CreateAccountForm.jsx, TransactionHistory.jsx),
solo que con nombres y estructura un poco más ordenados por vista/página.
