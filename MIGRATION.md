# Migração do contexto Registry

## Estado atual

O bounded context Registry foi copiado para um serviço independente. O código
original ainda permanece no monólito para permitir uma transição reversível.

Concluído:

- aplicação Spring Boot independente;
- banco lógico `pitflow-registry-db`;
- migrations exclusivas de Registry;
- testes unitários e teste de contexto;
- imagem Docker;
- pipeline de build, publicação e deploy;
- Service `ClusterIP`;
- rota ALB `/registry`;
- HPA e probes.

## Dependências restantes no monólito

O contexto `operation` ainda usa tipos de Registry diretamente:

- `CustomerGateway`;
- `VehicleGateway`;
- value object `Email`.

Em especial, `CreateServiceOrderImp` consulta cliente e veículo por chamadas
locais. Antes de remover Registry do monólito, essas consultas precisam ser
substituídas por um contrato remoto.

## Próximas etapas

1. Definir endpoints internos para validação de cliente e veículo.
2. Criar no serviço Operation uma porta própria, sem importar gateways de
   Registry.
3. Implementar um adapter HTTP dessa porta apontando para
   `http://pitflow-registry`.
4. Substituir o value object `Email` compartilhado por um tipo pertencente ao
   próprio contexto Operation ou por um DTO de integração.
5. Remover dependências e chaves estrangeiras entre bancos de contextos
   diferentes.
6. Validar criação de ordem usando Registry remoto.
7. Somente depois remover `br.com.pitflow.registry` do monólito.

## Estratégia de publicação

Durante a transição:

- o monólito continua atendendo suas rotas atuais;
- o novo serviço pode ser implantado sem assumir tráfego;
- após validação, a rota `/registry` no ALB passa a direcionar as chamadas ao
  novo serviço;
- a remoção do código antigo ocorre em um PR separado.
