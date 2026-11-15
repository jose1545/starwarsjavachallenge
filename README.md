# Star Wars Challenge API

API de Spring Boot que obtiene información de Star Wars de SWAPI ([https/](https://www.swapi.tech/)) y la expone a través de sus propios endpoint REST. Incluye autenticación con JWT, una base en memoria de H2 para el usuario y Swagger para documentación y pruebas interactivas.

## Tecnologías

- Java 21.
- Spring boot 4: configuración de la aplicación y manejo de dependencias.
- Spring MVC: Controllers Rest y manejo de solicitudes/respuestas en JSON.
- Spring Security: autenticación con contraseña, seguridad stateless con bearer-toke y validación JWT.
- Spring OAuth2 Resource Server y Nimbus JOSE: decodificación y codificación de JWT con las interfaces JWT soportadas por Spring Security.
- Spring Data JPA y Hibernate: persistencia para el user con el que se autentica en la API.
- H2: Base de datos en memoria que se recrea cada vez que la aplicación arranca.
- Bean Validation: validación de autenticación y entradas en la API.
- Spring RestClient: Comunicación HTTP con SWAPI.
- springdoc OpenAPI y Swagger UI: Documentación de la API e interfaz para pruebas interactivas

## Arquitectura

El código agrupa funcionalidad por dominio y separa responsabilidades de API, aplicación, dominio e infraestructura
- La capa API contiene los controladores REST y DTOs HTTP.
- La capa de aplicación contiene la lógica de negocio y define los puertos para intgeraciones externas
- La capa de dominio incluye los modelos de Star Wars de la aplicación
- La capa de infraestructura adapta las respuesta de de SWAPI (patrón adapter) y maneja la persistencia de lo que la aplicación necesita
- La capa de configuración contiene configuraciones de Spring, cliente http, openapi y configuración de seguridad
- La capa Shared contiene manejo de paginación y errores de API

Los controladores transforman las solicitudes y respuestas HTTP, los servicios contienen el comportamiento de la aplicación, los adpatadores SWAPI hacen las solicitudes externas y mapean las respuestas a los modelos de dominio del proyecto. La autenticación usa Spring Security para validar las credenciales almacenadas y emite JWT, mientras que JPA y H2 proporcionan la persistencia.

## Configuración y uso

- Requerimientos: Java 21
- Copiar .env.example a .env y configurar
  - AUTH_SEED_USERNAME: usuario para iniciar sesion
  - AUTH_SEED_PASSWORD: password del usre para iniciar sesión
  - JWT_SECRET: string random en base 64 de al menos 32 bytes
  - SWAPI_URL: url para integrar la API, si no está defaultea a https://www.swapi.tech/api
- Arrancar la app desde la raiz del proyecto .\mvnw.cmd spring-boot:run

## Test coverage

La suite de test usa data deterministica y mockea las llamadas HTTP a la api externa SWAPI, para no depender de que el servicio esté disponible

- Arranque de aplicación: verifica que el spring application context cargue la configuración de test
- Autenticación e integración de seguridad: arranca la app con una base de datos en memoria H2 y un user de test seedeado. Verifica que el password se guarde como un hash de BCrypt, credenciales validas devuelven un token bearer y credenciales invalidas un error 401. Credenciales incompletas devuelven 400.
- Validación de JWT: verifica que tokens malformados, expirados, con issuer incorrecto y subject vacío sean rechazados, también verifica que un token firmado con una key diferente no pueda acceder a recursos protegidos
- Protección de rutas: verifica que las rutas protegidas (people, films, etc) devuelvan un error 401 si no está autenticado. verifica que un token valido pueda acceder a endpoints protegidos
- Comportamiento stateles: confirma que una request autenticada no cree una sesión HTTP ni un JSESSIONID cooki
- OpenAPI y Swagger: verifica que el documento de OpenAPI y Swagger sean publicamente accesibles, que el login sea publico y el resto de las operaciones requieran autenticacion
- Tests de controladores: verifica el mapeo de la respuesta de la API, paginación, parametros de busqueda y manejo de requests inválidas
- Tests de servicios: verifica la validación del service y delegación a los puertos de la aplicación
- Tests de paginación: verifica las reglas compartidas de paginación y comportamiento de limites

Correr la suite con ` .\mvnw.cmd test `
