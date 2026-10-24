# Sistema de Gestión de Biblioteca

Aplicación de consola en Java para gestionar libros: se pueden ver todos, buscar por título, autor, rango de precios o stock mínimo, añadir y borrar. Los datos se guardan en un archivo de texto (`libros.txt`) o en una base de datos MySQL, y la aplicación pregunta con cuál trabajar al arrancar. También tiene una opción para copiar los datos de un lado al otro.

Es un proyecto Ant de NetBeans, hecho para el módulo 0488 Desarrollo de Interfaces (2º DAM, RA6).

## Requisitos

Esto es con lo que lo hemos probado:

| Software | Versión |
|---|---|
| Windows | 10 (VM de VMware) |
| JDK (Eclipse Temurin) | 26.0.2.1 |
| NetBeans | 31 |
| MySQL Community Server | 26.10.0 (vale desde la 8.0.16) |
| Git | 2.56.0 |

Además hay que descargar dos librerías de Maven Central, que no están en el repositorio: `mysql-connector-j-26.7.0.jar` y `dotenv-java-3.0.2.jar`.

## Instalación rápida

Los comandos van en cmd, no en PowerShell.

1. Clonar el repositorio: `git clone https://github.com/vhugolm2023/Gestion-de-Biblioteca.git`
2. Abrir la carpeta en NetBeans (File → Open Project). Al hacer Clean and Build falla porque el proyecto apunta a las librerías de otro ordenador. Hay que descargar los dos `.jar` (enlaces en el manual, apartado 6) y enlazarlos en Properties → Libraries → Compile → Add JAR/Folder, quitando antes las entradas rotas. Esos cambios en `nbproject/` no se suben a GitHub.
3. Crear la base de datos, desde la raíz del proyecto: `mysql -u root -p < sql\init.sql`. Ojo, borra la tabla `libro` si ya existía.
4. Configurar el `.env` (viene en el repositorio): editarlo y poner en `DB_PASSWORD` la contraseña de tu MySQL. Si no existiera, se crea con `copy .env.example .env`.
5. Clean and Build y Run en NetBeans. O desde la terminal, en la raíz del proyecto: `java -jar dist\GestionDeBiblioteca.jar`

Siempre hay que lanzarla desde la raíz del proyecto, que es donde están el `.env` y `libros.txt`.

## Configuración (.env)

| Variable | Qué es | Ejemplo |
|---|---|---|
| `DB_URL` | URL de la base de datos | `jdbc:mysql://localhost:3306/biblioteca` |
| `DB_USER` | Usuario de MySQL | `root` |
| `DB_PASSWORD` | Contraseña de ese usuario | la de tu MySQL |

La ruta del archivo de texto no se configura aquí: está fija en el código (`libros.txt`).

## Documentación

- [Manual de despliegue completo (PDF)](ManualDespliegue/manual_despliegue.pdf)
- [Javadoc](javadoc)

## Autores

Hugo Lopez y Daniel Carazo
