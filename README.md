Markdown
# 🛡️ SafePay Engine

Uma plataforma fullstack desenvolvida para demonstrar o processamento seguro e resiliente de pagamentos, com foco em **idempotência de transações** e arquitetura orientada a eventos. O sistema previne cobranças duplicadas e garante a consistência dos dados mesmo sob falhas de rede ou requisições simultâneas.

---

## 🚀 Tecnologias Utilizadas

### **Backend**
* **Java 17** & **Spring Boot 3**
* **Spring Data JPA** (Persistência de dados)
* **Spring AMQP / RabbitMQ** (Mensageria e mensageria assíncrona)
* **H2 Database / PostgreSQL** (Banco de dados)

### **Frontend**
* **React** + **TypeScript**
* **Vite** (Build tool)
* **Axios** (Integração HTTP)
* **Lucide React** (Ícones)

### **Infraestrutura**
* **Docker** & **Docker Compose** (Containerização do RabbitMQ e serviços)

---

## 🏗️ Arquitetura do Sistema

1. **Frontend (React + Vite):** O cliente envia os dados da transação contendo uma *Idempotency Key* única gerada para a operação.
2. **Backend (Spring Boot):** Intercepta a requisição, consulta a chave no banco de dados e valida se a transação já foi processada.
3. **Fila de Mensageria (RabbitMQ):** Se for uma nova requisição, a mensagem é enviada para a fila de processamento assíncrono seguro.
4. **Respostas Consistentes:** Em caso de reenvio com a mesma chave, o backend retorna o resultado anterior sem reprocessar o pagamento.

---

## 🛠️ Como Rodar o Projeto Localmente

### **Pré-requisitos**
* Docker e Docker Compose
* Node.js (v18 ou superior)
* Java JDK (v17 ou superior)

---

### **1. Subir a Infraestrutura (RabbitMQ / Banco)**
Na raiz do projeto (ou dentro da pasta `processor`), execute:

```bash
docker-compose up -d
O painel do RabbitMQ estará acessível em http://localhost:15672 (Login: guest / Senha: guest).

2. Executar o Backend (Spring Boot)
Acesse a pasta processor e rode o serviço:

Bash
cd processor
./mvnw spring-boot:run
O servidor iniciará na porta 8080.

3. Executar o Frontend (React)
Em um novo terminal, acesse a pasta payment-dashboard, instale as dependências e rode a aplicação:

Bash
cd payment-dashboard
npm install
npm run dev
Acesse a interface gráfica no endereço indicado pelo Vite (geralmente http://localhost:5173).

📂 Estrutura do Repositório
Plaintext
.
├── payment-dashboard/   # Aplicação Frontend (React + TypeScript)
├── processor/           # Aplicação Backend (Spring Boot + RabbitMQ + Docker)
├── .gitignore           # Regras de exclusão do Git
└── README.md            # Documentação do projeto


📌 Principais Endpoints da API
POST /api/payments — Envia um novo pagamento (requer idempotencyKey, amount e currency).

GET /api/payments — Lista o histórico de todas as transações cadastradas.

🛡️ Conceito de Idempotência Aplicado
Em sistemas financeiros, a idempotência garante que uma operação executada repetidamente com os mesmos parâmetros produzirá exatamente o mesmo resultado de quando foi executada pela primeira vez. Isso evita:

Duplicidade de cobranças por duplo clique do usuário.

Reprocessamento indevido em cenários de retentativa (retry) da rede.