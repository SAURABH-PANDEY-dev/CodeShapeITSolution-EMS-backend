# Start with OpenJDK base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy your Maven project
COPY . .

# Give execution permission to Maven Wrapper
RUN chmod +x mvnw

# Build the app
RUN ./mvnw clean package -DskipTests

# Expose port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "target/expenses-0.0.1-SNAPSHOT.jar"]