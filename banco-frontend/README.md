# Banco2025 - Frontend

Frontend del proyecto Banco2025 para el laboratorio de Arquitectura de Software. Está desarrollado en React y permite consultar, crear, editar y eliminar clientes, realizar transferencias entre cuentas y consultar el historial de transacciones.

## Cómo ejecutar el proyecto

Instalar las dependencias:

```bash
npm install
```

Iniciar la aplicación:

```bash
npm start
```

El frontend se ejecuta en `http://localhost:3000`.

El backend debe estar ejecutándose en `http://localhost:8080`. La configuración de conexión se encuentra en `src/api/axiosConfig.js`.

## Configuración de CORS

Para permitir las peticiones desde el frontend, se puede agregar la siguiente configuración al proyecto Spring Boot:

```java
package com.udea.bancoudea.config;

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

## Endpoints del backend

| Método | Ruta                                        | Función                              |
| ------ | ------------------------------------------- | ------------------------------------ |
| GET    | `/api/customers`                            | Listar clientes                      |
| POST   | `/api/customers`                            | Crear un cliente                     |
| PUT    | `/api/customers/{id}`                       | Editar un cliente                    |
| DELETE | `/api/customers/{id}`                       | Eliminar un cliente                  |
| POST   | `/api/transactions/transfer`                | Realizar una transferencia           |
| GET    | `/api/transactions/account/{accountNumber}` | Consultar el historial de una cuenta |

## TransactionController

El controlador de transacciones puede incluir los siguientes métodos para realizar transferencias y consultar el historial de una cuenta:

```java
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionDTO> transfer(
            @RequestBody TransactionDTO dto) {
        return ResponseEntity.ok(
                transactionService.transferMoney(dto));
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<TransactionDTO>> history(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(
                transactionService.getTransactionsForAccount(accountNumber));
    }
}
```

## Estructura del proyecto

```text
src/
├── api/
│   └── axiosConfig.js
├── components/
│   └── Navbar.jsx
└── pages/
    ├── CustomersPage.jsx
    ├── TransferPage.jsx
    └── HistoryPage.jsx
```

* **axiosConfig.js:** configura las peticiones HTTP al backend mediante Axios.
* **Navbar.jsx:** contiene la navegación entre las vistas.
* **CustomersPage.jsx:** permite gestionar los clientes.
* **TransferPage.jsx:** permite realizar transferencias entre cuentas.
* **HistoryPage.jsx:** muestra el historial de transacciones de una cuenta.

## Organización del frontend

El frontend está dividido en componentes y páginas para separar las funcionalidades de la aplicación. Esta estructura organiza las vistas de clientes, transferencias e historial en archivos independientes, en lugar de concentrarlas en los archivos utilizados en la estructura original del material de referencia, como `App.js`, `TransferFound.jsx`, `CreateAccountForm.jsx` y `TransactionHistory.jsx`.
