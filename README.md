# Register

Register é um serviço do **Nota Social** responsável por disponibilizar informações estabelecimento de forma acessível e estruturada para os usuários finais. Ele consome dados gerados pelos serviços ReceiptScan, organizando-os para atender a diferentes demandas, como consultas e análises.
Além disso, é responsável pelo gerenciamento das contas de usuários consumidores e estabelecimentos, através de integração com o Keycloak. 

## Como funciona

  - Consumo de Dados: O Register recebe informações estruturadas diretamente do ReceiptScan por meio de filas de mensagens.
  - Disponibilização de Dados: As informações são processadas e expostas através de APIs, permitindo que outros sistemas ou interfaces de usuário as consumam de forma simples e eficiente.
  - Gerenciamento de Dados: Garante a consistência e a integridade dos dados armazenados.

## Funcionalidades principais

  - Consulta de dados: APIs para buscar informações detalhadas de estabelecimentos e consumidores.
  - Gestão de Usuários: Integração com o Keycloak para criação e administração de contas de usuários consumidores e estabelecimentos.

## Tecnologias utilizadas

  - Java/Spring para desenvolvimento.
  - RabbitMQ para integração via filas de mensagens.
  - MySQL persistência e consulta de informações.
  - Keycloak para gerenciamento de usuários e suas permissões.

## Integração com o ecossistema Nota Social

O Register é a interface final do ecossistema Nota Social, conectando os dados processados e organizados pelo ReceiptScan com os usuários e sistemas que necessitam dessas informações. Ele desempenha um papel essencial na entrega do valor proposto pelo Nota Social.
 
 
