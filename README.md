# StarWars Backend Test Java 8

Servicio REST (Spring Boot) que expone información de **People** desde [SWAPI.tech](https://www.swapi.tech/documentation#people). Implementa **búsqueda** con paginación, **detalle** por `id`, **caché** y **seguridad JWT**. La app sigue estructura **package-by-feature**.

---

## Arquitectura & Estructura de paquetes (package-by-feature)

```
com.edisonchavez.challenge
├── auth (modulo para la configuracion de seguridad)
│    ├── dto
│    │   └── AuthRequest.java
│    │    └── AuthResponse.java
│    └── AuthController.java  Autenticacion controller
├── config (configuraciones necesarias para generacion de token, cache y seguridad)
│    └── CacheConfig.java
│    └── HttpConfig.java
│    └── JwtFilter.java
│    └── JwtUtil.java
│    └── OpenApiConfig.java
│    └── SecurityConfig.java
├── exceptions (Excepciones para devolver el estado)
│    └── NotFoundException.java
└── films (modulo para conectarme al api de films)
    ├── client
    │   └── FilmClient.java (FeignClient para conectarme al servicio)
    ├── dto
    │   ├── ApiListResponse.java
    │   ├── ApiResponse.java
    │   ├── FilmProps.java
    │   ├── PageResponse.java
    ├── service (servicio para consultar al api mediente cache / feign)
    │   ├── FilmsCacheRepo.java
    │   └── FilmsService.java
    │   └── FilmsServiceImpl.java
    └── FilmsController.java (Se disponibiliza el api para las consultas)
└── people
    ├── client
    │   └── PeopleClient.java (FeignClient para conectarme al servicio)
    ├── dto
    │   ├── ApiResponse.java
    │   ├── PersonProps.java
    │   ├── PageResponse.java
        ├── PersonListProps.java
    ├── service (servicio para consultar al api mediente cache / feign)
    │   └── PeopleService.java
    │   └── PeopleServiceImpl.java
    └── PeopleController.java (Se disponibiliza el api para las consultas)
└── starships
    ├── client
    │   └── StarshipsClient.java (FeignClient para conectarme al servicio)
    ├── dto
    │   ├── ApiResponse.java
    │   ├── StarshipsPropsProps.java
    │   ├── PageResponse.java
        ├── StarshipsListProps.java
    ├── service (servicio para consultar al api mediente cache / feign)
    │   └── StarshipsService.java
    │   └── StarshipsServiceImpl.java
    └── StarshipsController.java (Se disponibiliza el api para las consultas)
└── vehicles
    ├── client
    │   └── StarshipsClient.java (FeignClient para conectarme al servicio)
    ├── dto
    │   ├── ApiResponse.java
    │   ├── StarshipsPropsProps.java
    │   ├── PageResponse.java
        ├── StarshipsListProps.java
    ├── service (servicio para consultar al api mediente cache / feign)
    │   └── StarshipsService.java
    │   └── StarshipsServiceImpl.java
    └── StarshipsController.java (Se disponibiliza el api para las consultas)
└── shared (Contantes y dtos en comun necesarios)
    ├── Constants.java
    ├── FilterResponse.java
    ├── PageResult.java
    ├── PageUtils.java
    ├── QueryRequest.java
    ├── ResulData.java

```

---

## Endpoints

* `GET /api/people`

    * **Query params**:

        * `page` (0-based, default `0`)
        * `size` (default `10`, máx `100`)
        * `name` (opcional; si viene, filtra por nombre)
    * **Respuesta**: `Page<PersonListProps>` (ordenado por `name`).

* `GET /api/people/{id}`

    * **Respuesta**: `PersonProps`.

> Nota: el cliente externo (SWAPI.tech) usa `page` **1-based**. El servicio adapta `page + 1` y aplica **orden y paginación local** para respuestas filtradas.

---

## Instalación y Ejecución

### Prerrequisitos

* Java 8
* Maven 3.9+
* (Opcional) Docker 24+ para Redis/WireMock

### Build & Run (local)

```bash
# 1) Clonar
git clone https://github.com/tu-org/tu-repo.git
cd tu-repo

# 2) Construir sin tests
mvn -DskipTests clean package

# 3) Exportar secreto JWT (ejemplo)
export SECURITY_JWT_SECRET="E7sJr3VjKkG4p2mQ9wA1tZc6R8uM5nX0B4fH7kP2sD9yL3eT6qW8rY1uI3oP6aZ"

# 4) Ejecutar
mvn spring-boot:run
# ó
java -jar target/*.jar
```

### Ejecución con Docker (app sola)

```bash
# Construir imagen
docker build -t starwars-people:local .

# Ejecutar
docker run --rm -p 8080:8080 \
  -e SECURITY_JWT_SECRET=$SECURITY_JWT_SECRET \
  -e STARWARS_BASE_URL=https://www.swapi.tech \
  --name starwars starwars:local
```

### Docker Compose (app + redis opcional)

Ver archivos al final del README.

---

## Pruebas

### Unitarias (Mockito)

* `PeopleServiceImplTest` cubre:

    * Sin filtro (`client.list()`), orden y `total_records`.
    * Con filtro (`client.listFilter(query)`), mapeo `ResultData → PersonListProps`, orden y paginación.
    * `get(id)` devolviendo `properties`.

### Integración (WireMock)

* `PeopleIntegrationTest`:

    * Con filtro: stub `/api/people?page=1&limit=...&name=...` (1-based).
    * Sin filtro: stub `/api/people` sin query params.
    * Detalle: stub `/api/people/{id}`.

**Comandos**

```bash
mvn test
# Cobertura con JaCoCo
mvn test jacoco:report
# Abrir: target/site/jacoco/index.html
```

---

## Archivos de soporte

Para levantar local solo es necesario levantar el siguiente docker compose
### `docker-compose.yml`

```yaml
docker compose up -d
```
