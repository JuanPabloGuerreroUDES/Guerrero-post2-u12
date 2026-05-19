# Mi Spring App — Guerrero-post2-u12

![CI/CD Status](https://github.com/guerrero/Guerrero-post2-u12/actions/workflows/ci.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-brightgreen?logo=springboot)
![Docker](https://img.shields.io/badge/Docker-Hub-blue?logo=docker)

## Descripción

API REST desarrollada con **Spring Boot 3** y **Java 21** para la gestión de productos, como parte del laboratorio **Post-Contenido 2 — Unidad 12: Despliegue y CI/CD** de la asignatura Programación Web (Ingeniería de Sistemas — UDES 2026).

El proyecto implementa un pipeline completo de **Integración y Entrega Continua (CI/CD)** con GitHub Actions que automatiza:

1. Compilación con Maven y ejecución de pruebas unitarias
2. Generación de reporte de cobertura con **JaCoCo**
3. Construcción de imagen Docker con **multi-stage build**
4. Publicación automática en **Docker Hub** en cada push a `main`

---

## Pipeline CI/CD

### Diagrama del Pipeline

```
Push a main
     │
     ▼
┌─────────────────────────────┐
│  Job 1: build-and-test      │
│  ─────────────────────────  │
│  • Checkout del código      │
│  • Setup JDK 21 + caché     │
│  • mvn clean verify         │
│  • Upload JaCoCo artifact   │
└────────────┬────────────────┘
             │ (si exitoso)
             ▼
┌─────────────────────────────┐
│  Job 2: docker-publish      │  ← Solo en push a main
│  ─────────────────────────  │
│  • Checkout del código      │
│  • Login en Docker Hub      │
│  • Extraer metadata/tags    │
│  • Build & Push imagen      │
└─────────────────────────────┘
             │
             ▼
   Docker Hub: latest + sha-XXXXXXX
```

### Job 1 — build-and-test (CI)

Responsable de garantizar la calidad del código. Se ejecuta en **todos los push y pull requests** a `main`.

| Paso | Descripción |
|------|-------------|
| Checkout | Clona el repositorio en el runner de GitHub |
| Setup JDK 21 | Instala Temurin 21 con caché de `~/.m2/repository` |
| `mvn clean verify` | Compila, ejecuta pruebas y genera reporte JaCoCo |
| Upload JaCoCo | Sube `target/site/jacoco/` como artefacto descargable (7 días) |

### Job 2 — docker-publish (CD)

Construye y publica la imagen Docker. Solo se ejecuta en **push directo a `main`** (no en pull requests).

| Paso | Descripción |
|------|-------------|
| Login Docker Hub | Autenticación usando Secrets del repositorio |
| Metadata action | Genera tags `latest` y `sha-XXXXXXX` automáticamente |
| Build & Push | Construye con Dockerfile multi-stage y publica en Docker Hub |

---

## Configuración de GitHub Secrets

Para que el pipeline funcione correctamente se deben crear **dos Secrets** en el repositorio:

**Ruta:** `Settings → Secrets and variables → Actions → New repository secret`

| Secret | Valor | Cómo obtenerlo |
|--------|-------|----------------|
| `DOCKERHUB_USERNAME` | Tu nombre de usuario de Docker Hub | Visible en hub.docker.com al iniciar sesión |
| `DOCKERHUB_TOKEN` | Access Token de Docker Hub | `hub.docker.com → Account Settings → Security → New Access Token` |

> ⚠️ **Importante:** Usa siempre un **Access Token**, nunca la contraseña de tu cuenta.  
> Los tokens se pueden revocar individualmente sin comprometer la cuenta.

---

## Imagen Docker

### Descargar y ejecutar

```bash
# Descargar la última versión
docker pull guerrero/mi-spring-app:latest

# Ejecutar en modo desarrollo
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=dev \
  guerrero/mi-spring-app:latest

# Ejecutar una versión específica por commit SHA
docker pull guerrero/mi-spring-app:sha-a1b2c3d
docker run -p 8080:8080 guerrero/mi-spring-app:sha-a1b2c3d
```

La aplicación queda disponible en: `http://localhost:8080`

### Endpoints disponibles

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/productos` | Lista todos los productos |
| `GET` | `/api/productos/{id}` | Busca producto por ID |
| `POST` | `/api/productos` | Crea un nuevo producto |
| `DELETE` | `/api/productos/{id}` | Elimina un producto |
| `GET` | `/api/productos/inventario/valor` | Valor total del inventario |

### Ejemplo de uso

```bash
# Listar productos
curl http://localhost:8080/api/productos

# Crear producto
curl -X POST http://localhost:8080/api/productos \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Auriculares","precio":89.99,"stock":20}'

# Valor total del inventario
curl http://localhost:8080/api/productos/inventario/valor
```

---

## Estructura del Proyecto

```
Guerrero-post2-u12/
├── .github/
│   └── workflows/
│       └── ci.yml              ← Pipeline CI/CD
├── src/
│   ├── main/
│   │   ├── java/com/guerrero/miapp/
│   │   │   ├── MiAppApplication.java
│   │   │   ├── controller/
│   │   │   │   └── ProductoController.java
│   │   │   ├── model/
│   │   │   │   └── Producto.java
│   │   │   └── service/
│   │   │       └── ProductoService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/guerrero/miapp/service/
│           └── ProductoServiceTest.java  ← 10 pruebas unitarias
├── Dockerfile                  ← Multi-stage build
├── pom.xml                     ← JaCoCo configurado
├── .gitignore
└── README.md
```

---

## Ejecución Local (sin Docker)

```bash
# Clonar el repositorio
git clone https://github.com/guerrero/Guerrero-post2-u12.git
cd Guerrero-post2-u12

# Compilar y ejecutar pruebas con reporte de cobertura
mvn clean verify

# El reporte JaCoCo queda en:
# target/site/jacoco/index.html

# Iniciar la aplicación
mvn spring-boot:run
```

---

## Historial de Commits

| # | Commit | Descripción |
|---|--------|-------------|
| 1 | `ci: agregar pipeline GitHub Actions con Docker Hub publish` | Workflow YAML inicial con jobs build-and-test y docker-publish |
| 2 | `fix: corregir condición de rama en docker-publish y agregar caché Maven` | Mejoras al pipeline: condición `if` y optimización de caché |
| 3 | `docs: actualizar README con badge CI/CD, instrucciones Docker y Secrets` | Documentación completa del pipeline y guía de configuración |
