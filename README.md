\# Projeto Spring Boot - Customer API



API REST desenvolvida em \*\*Spring Boot\*\* para cadastro e consulta de clientes, utilizando \*\*PostgreSQL\*\* como banco de dados.



\---



\## 🚀 Tecnologias

\- Java 17+

\- Spring Boot

\- Maven

\- PostgreSQL

\- JUnit + Mockito



\---



\## 📋 Pré-requisitos

\- \[Java 17+](https://adoptium.net/)

\- \[Maven](https://maven.apache.org/)

\- \[PostgreSQL](https://www.postgresql.org/)



\---



\## ⚙️ Configuração do ambiente



\### Banco de Dados

Este projeto utiliza \*\*PostgreSQL\*\*. Crie o banco de dados local:



```sql

CREATE DATABASE customer\_db;



Configure o arquivo src/main/resources/application.properties com os parâmetros abaixo:

\# Porta do servidor

server.port=8081



\# Configuração do banco PostgreSQL

spring.datasource.url=jdbc:postgresql://localhost:5432/customer\_db

spring.datasource.username=seu\_usuario

spring.datasource.password=sua\_senha

spring.datasource.driver-class-name=org.postgresql.Driver



\# JPA / Hibernate

spring.jpa.hibernate.ddl-auto=update

spring.jpa.show-sql=true

spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect





Como rodar localmente

Clone o repositório:

git clone https://github.com/seu-usuario/customer-api.git

cd customer-api



Exemplos de requisição

Cadastro de cliente:



curl -X POST http://localhost:8081/customer \\

&#x20; -H "Content-Type: application/json" \\

&#x20; -d '{

&#x20;   "name": "Rafael",

&#x20;   "email": "rafael@email.com",

&#x20;   "cpf": "12345678900",

&#x20;   "phone": "11999999999",

&#x20;   "address": "Rua das Flores, 123",

&#x20;   "city": "Carapicuíba",

&#x20;   "password": "senhaSegura123",

&#x20;   "zipcode": "06320-000",

&#x20;   "terms": true

&#x20; }'



curl -X GET http://localhost:8081/customer/12345678900





Rodando os testes: mvn test

Os testes cobrem:

CustomerServiceTest → lógica de negócio.

CustomerControllerTest → endpoints REST com MockMvc.

