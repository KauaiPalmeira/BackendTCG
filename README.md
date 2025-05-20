# BackendTCG
# 🧾 RelatorioTorneio – Sistema de Geração de Relatórios Pokémon TCG
Sistema backend em Java para gerar relatórios visuais de torneios de Pokémon Trading Card Game (TCG). Os dados são salvos em um banco PostgreSQL (Supabase), e a resposta da API retorna uma imagem PNG formatada com os resultados.

### 📁 Estrutura do Projeto
```bash 


src/main/java/com/torneio/relatorio/
├── controller/              # RelatorioController
├── dto/                     # RelatorioRequestDTO, RelatorioResponseDTO
├── entity/                  # Entidades JPA (Deck, Jogador, etc.)
├── repository/              # Repositórios JPA
├── service/                 # RelatorioService, PontosService, ImagemService
└── RelatorioTorneioTcgApplication (Caso queira rodar diretamente)

src/main/resources/
├── templates/       # Imagens (Template Report - League Cup.png, pikachu_volt.png, etc.)
├── application.properties (Configurar DB_URL, DB_USERNAME e DB_PASSWORD)
```
-------------
🚀 Tecnologias Utilizadas
#### Java 21 – Linguagem principal

#### Spring Boot 3.4.5 – Framework para desenvolvimento web e integração com banco

#### PostgreSQL (via Supabase) – Banco de dados relacional

#### Java AWT – Manipulação e geração de imagens

#### Maven – Gerenciador de dependências

#### Lombok – Redução de boilerplate

-------------
### ⚙️ Pré-requisitos
``` text
 Java 21 (JDK instalado)

 Maven (mvn --version para verificar)

 Conta no Supabase com projeto PostgreSQL

 Postman (ou Insomnia) para testar a API

 Imagens necessárias na pasta resources/templates (ex: Template Report - League Cup.png, gardevoir_ex.png, etc.)
 ```
-------------
### 🛠️ Configuração do Banco de Dados (Supabase)
#### 1. Crie um novo projeto no Supabase.
#### 2. Execute os scripts SQL abaixo:
```sql

CREATE TABLE tipo_torneio (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE local (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE deck (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE jogador (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    identificador UUID DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE relatorio (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tipo_torneio_id BIGINT NOT NULL REFERENCES tipo_torneio(id),
    local_id BIGINT NOT NULL REFERENCES local(id),
    data_torneio DATE NOT NULL,
    imagem BYTEA NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE relatorio_jogador (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    relatorio_id BIGINT NOT NULL REFERENCES relatorio(id),
    jogador_id BIGINT NOT NULL REFERENCES jogador(id),
    deck_id BIGINT NOT NULL REFERENCES deck(id),
    posicao INTEGER NOT NULL,
    pontos INTEGER NOT NULL
);
```
#### 3. Insira alguns dados iniciais:
``` sql
INSERT INTO tipo_torneio (nome) VALUES
    ('League Cup'), ('League Challenge'), ('Torneio Regular');

INSERT INTO local (nome) VALUES
    ('Palkia City'), ('Vermillion City');

INSERT INTO deck (nome) VALUES
    ('Gardevoir'), ('Charizard');
```
#### ⚠️ Certifique-se de que cada deck tem uma imagem correspondente na pasta resources/templates/.

-------------

### ▶️ Como Rodar o Projeto
#### 1. Clone o Rep
``` bash
git clone https://github.com/KauaiPalmeira/BackendTCG.git
```
#### 2. Substitua as variáveis de conexão no application.properties pelas suas credenciais Supabase
```properties
spring.datasource.url=jdbc:postgresql://<URLDoSeuSUPABASE>
spring.datasource.username=<USERNAMEDoSeuSUPABASE>
spring.datasource.password=<PASSWORDDoSeuSUPABASE>
```
Compile e execute:
```shell
mvn clean install
mvn spring-boot:run
```
Ou apenas execute pela IDE (classe RelatorioTorneioTcgApplication)

-------------



#### 🧪 Exemplo de Requisição (Postman)
Método: POST

URL: http://localhost:8080/api/relatorios

Headers: Content-Type: application/json

Body (raw/JSON):

```json
{
  "tipoTorneioId": 2,
  "localId": 1,
  "dataTorneio": "2025-08-13",
  "numeroParticipantes": 50,
  "jogadores": [
    {
      "nomeJogador": "Kleber Silva",
      "deckId": 2,
      "posicao": 1
    },
    {
      "nomeJogador": "Ron Nison Abreu",
      "deckId": 2,
      "posicao": 2
    },
    {
      "nomeJogador": "John Wlyls",
      "deckId": 2,
      "posicao": 3
    },
    {
      "nomeJogador": "Brunno Nevs",
      "deckId": 7,
      "posicao": 4
    },
    {
      "nomeJogador": "Raian Santos Oliveira",
      "deckId": 6,
      "posicao": 5
    },
    {
      "nomeJogador": "Fernanda Lima",
      "deckId": 5,
      "posicao": 6
    },
    {
      "nomeJogador": "Rafael Almeida",
      "deckId": 3,
      "posicao": 7
    },
    {
      "nomeJogador": "Beatriz Mendes",
      "deckId": 1,
      "posicao": 8
    }
  ]
}
```
Resposta esperada: Um arquivo .png com os dados visuais do torneio.

🔍 Verificação
Você pode confirmar os dados salvos diretamente no painel do Supabase, nas tabelas:
```sql
relatorio

jogador

relatorio_jogador
```
-----------------
 
### ❗ Solução de Problemas
#### Erro 500 (Imagem não encontrada):

Verifique se os arquivos Template Report - League Cup.png e pikachu_volt.png estão na pasta resources/templates/.

O nome do deck no banco deve coincidir com o nome do arquivo da imagem (Charizard → charizard.png, por exemplo).

#### Erro de conexão com Supabase:

Verifique a URL, username e password no application.properties.

---------------------- 
### 📫 Contato
Desenvolvido por Kauai Palmeira.
Dúvidas ou sugestões? Entre em contato:
📧 kauaipalmeira@hotmail.com
