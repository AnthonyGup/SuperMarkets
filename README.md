# SuperMarkets - Sistema de Gestión de Catálogo de Productos

## Descripción General
Sistema avanzado para la gestión de un catálogo de productos de supermercado distribuido en múltiples sucursales interconectadas. Implementa diversas estructuras de datos desde cero (listas enlazadas, árboles AVL, B, B+, tablas hash, colas, pilas y grafos) para optimizar operaciones de almacenamiento, búsqueda y transferencia de productos.

## Requisitos del Sistema
- **Java**: JDK 11 o superior
- **Maven**: 3.6 o superior (para compilación del backend)
- **Node.js**: 14 o superior (para el frontend Angular)
- **Servidor de aplicaciones**: Tomcat 10 (integrado vía Maven Cargo plugin)
- **Sistema Operativo**: Windows/Linux/macOS

## Instrucciones de Compilación

### Backend (SuperMarkets)
1. Navegar al directorio del backend:
   ```bash
   cd SuperMarkets
   ```
2. Compilar el proyecto con Maven:
   ```bash
   mvn clean package
   ```
   Esto generará el archivo `SuperMarkets-1.0-SNAPSHOT.war` en el directorio `target/`.

### Frontend (Angular)
1. Navegar al directorio del frontend:
   ```bash
   cd frontend
   ```
2. Instalar dependencias:
   ```bash
   npm install
   ```
3. Compilar el frontend (opcional, se puede servir en desarrollo):
   ```bash
   ng build
   ```

## Instrucciones de Ejecución

### Opción 1: Servidor Integrado (Desarrollo)
1. Iniciar el backend con Tomcat embebido:
   ```bash
   cd SuperMarkets
   mvn cargo:run
   ```
   El backend estará disponible en `http://localhost:8080/SuperMarkets-1.0-SNAPSHOT/`.

2. En otra terminal, iniciar el frontend Angular:
   ```bash
   cd frontend
   ng serve
   ```
   El frontend estará disponible en `http://localhost:4200/`.

### Opción 2: Despliegue en Servidor Externo
1. Copiar el archivo `SuperMarkets-1.0-SNAPSHOT.war` al directorio `webapps` de Tomcat.
2. Iniciar Tomcat.
3. Desplegar el frontend compilado en el servidor web de su preferencia.

## Estructura del Proyecto
```
SuperMarkets/
├── SuperMarkets/          # Backend Java (Maven)
│   ├── src/main/java/com/supermarkets/
│   │   ├── api/           # Endpoints REST
│   │   ├── pojo/          # Clases de dominio (Producto, Sucursal, etc.)
│   │   ├── structures/    # Estructuras de datos implementadas
│   │   └── utils/         # Utilidades
│   └── pom.xml
├── frontend/              # Frontend Angular
│   ├── src/app/
│   └── package.json
├── csv/                   # Archivos CSV de muestra
│   ├── catalogo.csv
│   ├── sucursales.csv
│   └── conexiones.csv
└── README.md
```

## Archivos de Datos (CSV)
- **sucursales.csv**: Lista de sucursales con sus propiedades (ID, nombre, ubicación, tiempos).
- **catalogo.csv**: Catálogo de productos (≥1000 productos requeridos para entrega final).
- **conexiones.csv**: Conexiones entre sucursales con tiempos y costos.

## Características Principales
- Gestión de múltiples sucursales con inventarios independientes.
- Búsqueda eficiente por nombre (AVL), código de barras (Hash), categoría (B+), rango de fechas (B).
- Transferencia de productos entre sucursales con cálculo de rutas óptimas (tiempo/costo).
- Simulación de flujo de productos con colas de ingreso, preparación y despacho.
- Visualización gráfica de estructuras de datos y red de sucursales.
- Mecanismos de rollback (pilas) para operaciones erróneas.

## Créditos
- Universidad San Carlos de Guatemala
- Centro Universitario de Occidente
- Curso: Laboratorio de Estructura de Datos
- Semestre: 5to semestre, 2026
