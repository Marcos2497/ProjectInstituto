# 🏛️ Sistema de Gestión Facultad  
*Trabajo Práctico Integrador - Java Full Stack*

[![Java](https://img.shields.io/badge/Java-17-007396?logo=openjdk)](https://openjdk.org/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-FF6B35?logo=javafx)](https://openjfx.io/)
[![Hibernate](https://img.shields.io/badge/Hibernate-6.4-59666C?logo=hibernate)](https://hibernate.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?logo=postgresql)](https://www.postgresql.org/)
[![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?logo=apachemaven)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

Un sistema completo de gestión académica para facultades, desarrollado con arquitectura MVC, persistencia ORM e interfaz gráfica moderna.

---

## ✨ Características Principales

### 📊 **Gestión Completa de Entidades**
- **Institutos** - Unidades académicas organizativas
- **Docentes** - Plantel docente con cargos y dedicación horaria  
- **Asignaturas** - Materias con responsables y carreras asociadas
- **Carreras** - Planes de estudio con malla curricular
- **Cargos Docentes** - Relación N:M entre Institutos y Docentes
- **Asignatura-Carrera** - Relación N:M entre asignaturas y carreras

### 🎨 **Interfaz Moderna JavaFX**
- Dashboard interactivo con tarjetas
- Menú principal con navegación intuitiva
- Formularios de ABM (Altas, Bajas, Modificaciones)
- Tablas con datos en tiempo real
- Diseño responsive y efectos visuales

### 🗄️ **Persistencia Avanzada**
- Mapeo ORM con Hibernate JPA
- Generación automática de esquema (DDL)
- Relaciones 1:N y N:M correctamente implementadas
- Transacciones ACID garantizadas
- Pool de conexiones configurado

### ⚙️ **Arquitectura Profesional**
- Patrón MVC (Modelo-Vista-Controlador)
- DAO Pattern para acceso a datos
- Separación clara de responsabilidades
- Fácil de extender y mantener

---

## 🚀 Empezando Rápido

### **Prerrequisitos**
- ✅ **Java JDK 17+** (Eclipse Temurin u OpenJDK)
- ✅ **Maven 3.9+** (incluido en el proyecto como wrapper)
- ✅ **PostgreSQL 15+** (con servicio corriendo)
- ✅ **Git** (para clonar el repositorio)

### **📥 Instalación en 5 Minutos**

#### 1. Clonar el repositorio
```bash
git clone https://github.com/tu-usuario/facultad-project.git
cd facultad-project
