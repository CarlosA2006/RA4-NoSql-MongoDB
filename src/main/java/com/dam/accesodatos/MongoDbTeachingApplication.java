package com.dam.accesodatos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * Aplicación Spring Boot para proyecto pedagógico de MongoDB
 * 
 * Este proyecto enseña acceso a datos con MongoDB en dos modalidades:
 * 1. API Nativa de MongoDB (MongoClient, MongoCollection, Document)
 * 2. Spring Data MongoDB (MongoRepository, MongoTemplate)
 * 
 * Características:
 * - MongoDB embebido (Flapdoodle) - no requiere instalación
 * - 10 métodos de ejemplo implementados (5 por módulo)
 * - 8 métodos TODO para que estudiantes implementen (4 por módulo)
 * - Tests unitarios completos
 * - Documentación exhaustiva en español
 * 
 * @author Proyecto Pedagógico RA4
 * @version 1.0.0
 */
@SpringBootApplication
@EnableMongoAuditing // Habilita @CreatedDate y @LastModifiedDate automáticos
public class MongoDbTeachingApplication {

    public static void main(String[] args) {
        SpringApplication.run(MongoDbTeachingApplication.class, args);
        
        System.out.println("\n" +
                "====================================================================\n" +
                "  🎓 Proyecto Pedagógico MongoDB - Iniciado con éxito\n" +
                "====================================================================\n" +
                "  Puerto: http://localhost:8083\n" +
                "  MongoDB Embebido: localhost:27017\n" +
                "  Base de Datos: pedagogico_db\n" +
                "  \n" +
                "  📚 Módulos disponibles:\n" +
                "    1. API Nativa MongoDB     (/api/native/*)\n" +
                "    2. Spring Data MongoDB    (/api/springdata/*)\n" +
                "  \n" +
                "  ✅ 10 métodos implementados (ejemplos)\n" +
                "  ⚠️  8 métodos TODO (para estudiantes)\n" +
                "====================================================================\n");
    }
}
