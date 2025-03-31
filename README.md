# Introducción
Implementación del **commonutilitylibrary** (librería común). Dicho componente será reutilizado por todas las APIs que se implementen bajo la tecnología Springboot.

## Funcionalidades Generales
La librería **commonutilitylibrary** proporciona las siguientes funcionalidades:

* **Paginación:** Incluye la clase genérica **PagedResponse** para los endpoints que incluyan paginado. Esta clase permite obtener los resultados de una consulta paginada.
* **Filtrado:** Proporciona un método **setFilter** en la clase **CommonsMethods** para establecer todos los filtros de búsqueda. Este método permite agregar filtros a una consulta.
* **Ordenamiento:** Contiene el método **getOrders** en la clase **CommonsMethods** para agregar a una lista los parámetros de orden. Este método permite ordenar los resultados de una consulta.
* **Consultas SQL** Se incorporan las clases **CriteriaUtil** y **PredicateSpecification** para construir consultas complejas que filtren los resultados de una consulta de base de datos.
* **Constantes:** Ofrece un conjunto de constantes de uso común por todas las APIs.

# Como Empezar?

## 1. Proceso de Instalación local
- instalar Java: el JDK para Java 11
- instalar Maven: a partir de la version 3.*
- configurar las variables de entorno del S.O: Las variables a crear son JAVA_HOME y MAVEN_HOME, con el propósito de poder invocar los comandos de dichas tecnologías a travez de la terminal o consola.
- crear el archivo **settings.xml**: este archivo es necesario para configurar la autenticación hacia **Nexus Repository**, a donde se va a subir la dependencia commonutilitylibrary. Dicho archivo debe crearse en el directorio home de Maven (habitualmente dicho directorio .m2 se encuentra en el directorio del usuario). El contenido de dicho archivo debe ser el siguiente: <br>
```xml 
    <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
    https://maven.apache.org/xsd/settings-1.0.0.xsd">
        <servers>
            <server>
                <id>repositoryId</id>
                <username>develop</username>
                <password>[password]</password>
            </server>
        </servers>
        <mirrors>
            <mirror>
                <id>repositoryId</id>
                <mirrorOf>*</mirrorOf>
                <name>repositoryName</name>
                <url>https://repository.com/repository/maven-public/</url>
            </mirror>
        </mirrors>
    </settings>
```  
el password lo debe solicitar al administrador de APIs del CEN.

## 2. Estructura
La estructura a nivel del paquete **com.jotalml.utils** es el siguiente:
- **CommonUtilityLibraryApplication**: clase principal de la aplicación
- **PagedResponse**: Clase creada para todos los endpoints que utilicen paginado
- **PredicateSpecification**: Clase creada para agregar un nuevo predicado a la lista de predicados
- **CommonsMethods**: Clase encargada de declarar métodos comunes a reutilizar por todas las APIs

## 3. Publicación
- debe agregar el tag **repository** en la sección **distributionManagement**.
   ```xml
    <distributionManagement>
        <repository>
            <id>repositoryId</id>
            <name>repositoryName</name>
            <url>https://repository.com/repository/maven-public/</url>
        </repository>
    </distributionManagement>
   ```
Una vez finalizado los cambios en el código, lo que resta de hacer es la publicación de la nueva versión del artefacto. Para poder llevar a cabo la publicación desde el entorno local, debe haber cumplido todo lo que se mencionó anteriormente en el **1. Proceso de Instalación local**, además de lo que se declara a continuación:
- abrir una terminal en el directorio raíz del presente repositorio git: verifique que tenga debidamente configurado Java y Maven, ejecutando el comando **mvn --version**

Ya cumplido lo anterior, desde la terminal, simplemente deberá hacer lo siguiente:
- Publicación del nuevo artefacto: **mvn deploy**

## 4. Uso de la dependencia
Después de haber realizado todo lo mencionado en **1. Proceso de Instalación local**, en el proyecto maven de la API, deberá hacer lo siguiente:
1. configurar el pom.xml para descargar la dependencia:
  - debe agregar el tag xml **dependency**:
  ```xml 
      <dependency>
         <groupId>com.jotalml.utils</groupId>
         <artifactId>commonutilitylibrary</artifactId>
         <version>{version}</version>
      </dependency>
   ```
  **Nota:** El contenido **_{version}_** debe ser reemplazado por la última versión estable de la librería.
  - debe agregar el tag **repository** en las sección **repositories**.
   ```xml
        <repository>
            <id>repositoryId</id>
            <name>repositoryName</name>
            <url>https://repository.com/repository/maven-public/</url>
            <releases>
                <enabled>true</enabled>
                <updatePolicy>never</updatePolicy>
            </releases>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
   ```

2. Utilizar las constantes, métodos y clases presentes en la librería

## 5. Latest releases
- 1.0.0-RELEASE : Primera liberación. Incluye:
   - Clase genérica **PagedResponse** para los endpoints que incluyan paginado. 
   - Método _addPredicate_ contenido en la clase **PredicateSpecification** para agregar un nuevo predicado a la lista de predicados. 
   - Clase **CommonsMethods** con los métodos _getOrders_ para agregar a una lista los parámetros de orden, __composePageable__ para crear la paginación con los parámetros de orden aplicados y _setFilter_ para establecer todos los filtros de búsqueda.
   - Constantes de uso común por todas las APIs.
   - método _buildFilterSort_ a la clase **CommonsMethods** para APIs que incluyan ordenamiento (Utilizar este método en las APIs que tengan un solo endpoint, las que incluyan ordenamiento en más de uno de sus endpoints, el método lo deben implementar en cada API).
   - clase **CriteriaUtil** como clase reutilizable para la creación de consultas en bases de datos Mongo.
   - método para crear paginado **composePageableWithPageSize**. Recibe opcionalmente como parámetro el tamaño de página.
   - clase SQLUtils para uso en las APIs con métodos CRUD de SINCRON.
   - Si el  valor del campo _pageSize_  en el metodo **getPageRequest** es igual a -1 devuelve como resultado toddos los elementos en una página.
   - clase **PagedResponse2** que cuenta con los nuevos atributos(next,previous,count,result).
   - método **setNextAndPreviousUrl** a la clase **CommonsMethods** para construir los parámetros **previous** y **next** de la clase **PagedResponse2**
   - Si la **Api** no cuenta con el parámetro **pageSize** se toma por defecto el paseSize=10