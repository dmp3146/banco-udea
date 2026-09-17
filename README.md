# BancoUdea

Aplicación bancaria full-stack para la gestión de clientes y transferencias entre cuentas, desarrollada como proyecto académico. El backend expone una API REST en Spring Boot y el frontend es una SPA en React que consume esa API.

## Introducción

Las entidades financieras necesitan sistemas que permitan administrar clientes y sus movimientos de dinero de forma segura, trazable y en tiempo real. Este proyecto simula ese escenario a menor escala: un banco donde se pueden registrar clientes, consultarlos, transferir dinero entre sus cuentas y revisar el historial de transacciones de cada una.

BancoUdea se construyó como ejercicio práctico para aplicar los conceptos de una arquitectura en capas sobre Spring Boot (Controller → Service → Repository → Entity), separación entre entidades y DTOs, persistencia con JPA/Hibernate sobre MySQL, y consumo de esa API desde un cliente React independiente.

## Objetivos

### Objetivo general

Desarrollar una aplicación web full-stack que permita administrar clientes bancarios y gestionar transferencias de dinero entre sus cuentas, exponiendo la lógica de negocio a través de una API REST y un frontend en React.

### Objetivos específicos

- Diseñar y persistir un modelo de datos para clientes y transacciones usando JPA/Hibernate sobre una base de datos MySQL.
- Implementar una API REST en Spring Boot que permita crear y consultar clientes, y transferir dinero entre cuentas validando reglas de negocio (cuentas existentes, saldo suficiente).
- Aplicar el patrón DTO y mappers (MapStruct) para desacoplar las entidades de persistencia de lo que se expone por la API.
- Construir un frontend en React que consuma la API para las tres operaciones principales: consultar clientes, transferir dinero y ver el historial de transacciones por cuenta.
- Habilitar la comunicación entre frontend y backend (CORS) y manejar errores de negocio de forma visible para el usuario.

## Herramientas de software empleadas

**Backend**
- Java 17
- Spring Boot 3.3.10 (Spring Web, Spring Data JPA, Spring Validation)
- MySQL (driver `mysql-connector-j`)
- Hibernate (vía Spring Data JPA), con `ddl-auto=update`
- Lombok — reducción de código repetitivo (getters/setters)
- MapStruct 1.5.5 — mapeo automático Entity ↔ DTO
- Maven (`mvnw`) como gestor de dependencias y build

**Frontend**
- React 18 (creado con Create React App / `react-scripts` 5.0.1)
- React Router DOM 6 — navegación entre las 3 vistas
- Axios — cliente HTTP para consumir la API
- CSS plano (sin framework de UI)

**Herramientas de desarrollo**
- IntelliJ IDEA — desarrollo del backend
- Git y GitHub — control de versiones
- Postman / navegador — pruebas manuales de los endpoints

## Arquitectura propuesta

El proyecto sigue una **arquitectura cliente-servidor** con el backend y el frontend desacoplados y comunicándose por HTTP/JSON:

```
React (puerto 3000)  <---- HTTP/JSON ---->  Spring Boot (puerto 8080)  <---->  MySQL (puerto 3306)
```

El backend sigue el patrón en capas de Spring Boot (**Controller → Service → Repository → Entity**), con DTOs y mappers de MapStruct para no exponer las entidades directamente por la API. El frontend es una SPA en React con tres vistas (Clientes, Transferir, Historial) que consumen esa API mediante Axios.

## Procedimiento

1. Modelado de datos (`Customer`, `Transaction`) y conexión a MySQL.
2. Repositorios JPA para persistencia y consulta.
3. Lógica de negocio en los services, incluyendo validaciones de la transferencia.
4. DTOs y mappers para no exponer las entidades directamente.
5. Controllers REST y configuración de CORS.
6. Frontend en React consumiendo la API.
7. Pruebas manuales end-to-end y control de versiones con Git/GitHub.

## Conclusiones

- Separar el proyecto en capas (Controller, Service, Repository, Entity/DTO) facilitó aislar la lógica de negocio de la transferencia de dinero, permitiendo validarla y probarla sin depender de la capa web.
- El uso de DTOs evitó exponer directamente las entidades de persistencia por la API, lo que sería un riesgo de seguridad y acoplaría el contrato de la API a los detalles internos de la base de datos.
- Separar el frontend del backend en repositorios/carpetas independientes, comunicándose solo por HTTP, permitió avanzar en ambos de forma independiente, a costa de tener que configurar explícitamente CORS para que el navegador aceptara las peticiones.
- Quedan pendientes mejoras de robustez (edición/eliminación de clientes, manejo más granular de errores HTTP, pruebas automatizadas) que se plantean como trabajo futuro del proyecto.
