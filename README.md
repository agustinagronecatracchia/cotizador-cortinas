# Cotizador de Presupuestos de Cortinas

Aplicación de escritorio interactiva desarrollada en Java para el cálculo dinámico de presupuestos según el tipo de material seleccionado. El proyecto fue construido aplicando los principios de la Programación Orientada a Objetos (POO) y una arquitectura de software estructurada.

## 🚀 Tecnologías
* Java
* Interfaz Gráfica de Usuario (GUI)

## 🏗️ Arquitectura del Proyecto
El código está diseñado separando las responsabilidades de forma clara:
* **Model:** Define la clase abstracta o interfaz principal (`Cortina`) y sus derivaciones específicas mediante herencia (`Opaca`, `Traslucida`, `Blackout`).
* **View:** Contiene las ventanas de la interfaz gráfica (`VentanaPrincipal`, `VentanaPresupuesto`) y el manejo de excepciones (`ValidacionException`).
* **Service:** Contiene la lógica de negocio y el motor de cálculos (`CalculadoraPresupuesto`).

## 💡 Habilidades Aplicadas
* Programación Orientada a Objetos (POO): Herencia, Polimorfismo y Abstracción.
* Manejo de Eventos y Excepciones.
* Estructuración del proyecto utilizando el patrón MVC (Modelo-Vista-Controlador/Servicio).
