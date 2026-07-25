# PitFlow Registry

Microserviço responsável pelo cadastro e autenticação do domínio Registry:

- clientes;
- veículos;
- mecânicos;
- autenticação e emissão de JWT para mecânicos.

## Stack

- Java 21;
- Spring Boot 4;
- Spring Data JPA;
- Spring Security e JWT;
- PostgreSQL;
- Liquibase;
- Maven;
- Kubernetes.

## Execução local

Variáveis obrigatórias:

```text
DB_HOST
DB_PORT
DB_NAME
DB_USERNAME
DB_PASSWORD
JWT_SECRET
```

Build e testes:

```bash
./mvnw clean verify
```

Execução:

```bash
./mvnw spring-boot:run
```

## Banco de dados

O serviço utiliza o banco lógico `pitflow-registry-db`. As chaves esperadas no
secret `pitflow/bootstrap` são:

```text
PITFLOW_REGISTRY_DB_HOST
PITFLOW_REGISTRY_DB_PORT
PITFLOW_REGISTRY_DB_NAME
PITFLOW_REGISTRY_DB_USERNAME
PITFLOW_REGISTRY_DB_PASSWORD
```

As migrations deste repositório criam somente as tabelas `customer`, `vehicle`
e `mechanics`.

## Container e ECR

O projeto utiliza o ECR compartilhado indicado por `ECR_URL`. As imagens seguem
a convenção:

```text
registry-<commit-sha>
```

## Kubernetes

O pipeline aplica:

- recursos no namespace compartilhado `pitflow`;
- `ConfigMap` e `Secret` próprios;
- `Deployment`;
- `Service` do tipo `ClusterIP`;
- `HPA`;
- `Ingress` de classe `alb`.

A rota `/registry` participa do grupo compartilhado `pitflow` criado pelo AWS
Load Balancer Controller.

## Limite da primeira extração

O código continua temporariamente no monólito. Esta primeira etapa cria uma
cópia independente e testável. A remoção do pacote original ocorrerá somente
depois que a rota do novo serviço e as integrações dos demais contextos forem
validadas.
