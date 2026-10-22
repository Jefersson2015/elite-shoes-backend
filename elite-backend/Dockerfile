# Usar una imagen base de Java
FROM eclipse-temurin:21-jdk-alpine

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el archivo pom.xml y descargar dependencias
COPY pom.xml .
RUN apk add --no-cache maven
RUN mvn dependency:go-offline

# Copiar el código fuente
COPY src ./src

# Compilar la aplicación
RUN mvn clean package -DskipTests

# Exponer el puerto
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "target/*.jar"]