# Post-contenido Unidad 5: Integración en Aplicaciones Web - Sistema de Reservas de Laboratorios

## Descripción
Proyecto backend desarrollado en Spring Boot (`reservas-labs-api`) para la gestión y reserva de laboratorios de cómputo de la universidad. Implementa una arquitectura en capas completa con soporte para API REST y base de datos H2 en memoria.

# Decisiones de diseño

## Punto de decisión 1: Ubicación de la validación de solapamiento de horarios

El filtrado de solapamientos se delega al motor de base de datos mediante la consulta JPQL personalizada `buscarSolapamientos` en `ReservaRepository`.

Esta elección responde a razones de **rendimiento y escalabilidad**: realizar el filtrado en memoria, trayendo todas las reservas hacia Java, degradaría el desempeño a medida que crezca el volumen de datos.

Sin embargo, la decisión de negocio (interpretar si la lista no está vacía y lanzar `ReservaConflictException`) vive exclusivamente en `ReservaService`.

El **Repository** solo responde a una consulta técnica de datos:

> "¿Qué reservas se cruzan con este rango?"

Mientras que el **Service** responde a una regla de dominio:

> "¿Es válido crear esta reserva?"

### ¿Qué pasaría si el Controller llamara directamente a `buscarSolapamientos()`?

Se rompería la arquitectura en capas y el principio de encapsulamiento.

El controlador asumiría la responsabilidad de evaluar las reglas de negocio, acoplando la capa HTTP con el dominio y obligando a duplicar esa lógica si mañana se agrega otra interfaz, como una vista web MVC o una aplicación móvil.

---

## Punto de decisión 2: Reglas con y sin apoyo del Repository

Las reglas de negocio del sistema se clasifican según su necesidad de acceso a datos.

### Reglas con apoyo del Repository

La validación de solapamiento requiere comparar la solicitud entrante contra reservas previamente guardadas.

Al depender del estado global persistido, es indispensable apoyarse en el `Repository`.

### Reglas sin apoyo del Repository (Java puro)

La validación de horario de atención (**07:00 a 21:00**) y de duración (**entre 30 minutos y 3 horas**) vive enteramente en el método `validarHorarioDuracion` de `ReservaService`.

Dado que estas reglas dependen únicamente de los atributos del propio objeto `Reserva` (`inicio` y `fin`), no hay razón para consultar la base de datos.

### Criterio general

> Si una regla requiere consultar el estado de otros registros en el sistema, se apoya en el `Repository`.
>
> Si depende únicamente de las propiedades de la entidad que se está recibiendo, se valida en memoria dentro del `Service`.

---

# Herramientas utilizadas

- **Java 17 / Spring Boot 3.2**
- **Spring Data JPA / Base de datos H2**
- **Apache Maven**
- **Git & GitHub**