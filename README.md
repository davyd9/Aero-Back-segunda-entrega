# AeroBack - Minimo Aplicativo Funcional (MAF) com Persistencia Room

Aplicativo Android nativo desenvolvido em Jetpack Compose voltado para busca, emissao e gestao de passagens aereas com programa integrado de cashback acumulativo e persistencia de dados local em banco SQLite via Android Jetpack Room.

## Integrantes da Dupla

* Integrante 1: Davyd Eduardo Ferreira - RA / Matricula: 42284805
* Integrante 2: Guilherme Brunetti - RA / Matricula: 042261015

## Tecnologias e Dependencias

* Linguagem: Kotlin 2.2+
* Toolkit UI: Jetpack Compose (BOM 2024.x)
* Design System: Material Design 3 (androidx.compose.material3:material3)
* Navegacao: Navigation Compose (androidx.navigation:navigation-compose:2.7.7)
* Persistencia de Dados Local: Android Jetpack Room / SQLite (androidx.room:room-runtime, androidx.room:room-ktx, androidx.room:room-compiler via kotlin-kapt)
* Arquitetura e Gerenciamento de Estado: MVVM com ViewModel (androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7), Repository Pattern, Kotlin Coroutines e StateFlow / Flow reativo (collectAsState)
* Iconografia: Material Icons Extended (androidx.compose.material:material-icons-extended)
* Compilacao: compileSdk 34, minSdk 24, targetSdk 34

## Como Rodar o Projeto

### Pre-requisitos

* Android Studio (versao Hedgehog 2023.1.1 ou superior).
* JDK 17 configurado no ambiente de execucao do Gradle.
* Emulador Android (API 30 ou superior) ou dispositivo fisico com depuracao USB ativada (minSdk 24).

### Passo a Passo

1. Clone o repositorio: git clone https://github.com/davyd9/Aero-Back-segunda-entrega.git
2. Abra o Android Studio e selecione "File > Open", apontando para a pasta raiz clonada.
3. Aguarde o download das dependencias e sincronizacao do Gradle ("Sync Project with Gradle Files").
4. Selecione a configuracao de execucao "app" e o dispositivo de destino.
5. Pressione "Run 'app'" (Shift + F10) ou clique no botao de execucao verde.

---

## Documentacao e Decisoes do Projeto

### 1. Evolucao do Trabalho 1 e 2 para o Trabalho 3 (Persistencia com Room)

No Trabalho 1, o AeroBack era composto por tres telas de layout estatico: Busca (SearchScreen), Radar de Precos (PriceRadarScreen) e Carteira (WalletScreen), sem navegacao de pilha real nem persistencia.

No Trabalho 2 (Minimo Aplicativo Funcional), o projeto recebeu navegacao centralizada com NavHost e NavController em MainActivity.kt, rotas parametrizadas em RotasNavegacao.kt e expansao para 7 telas reais utilizando listas reativas em memoria (mutableStateListOf).

No Trabalho 3 (Persistencia de Dados com Room), a volatilidade em memoria foi substituida por um banco de dados relacional SQLite local utilizando o Android Jetpack Room integrado a arquitetura MVVM:

* Modelagem de entidades relacionais anotadas com `@Entity` e `@PrimaryKey` para `Voo` e `Reserva` (`ModelosDados.kt`).
* Criacao das interfaces de acesso a dados (`VooDao` e `ReservaDao`) anotadas com `@Dao`, expondo leituras reativas via `Flow<List<T>>` e operacoes de escrita assincronas (`suspend`) para `@Insert`, `@Update` e `@Delete` (`BancoDadosRoom.kt`).
* Implementacao da classe `AeroBackDatabase` estendendo `RoomDatabase` com padrao Singleton thread-safe (`@Volatile` e `synchronized`), garantindo instancia unica do arquivo `aeroback_database.db`.
* Camada de repositorio (`AeroBackRepository`) e `AeroBackViewModel` (`AeroBackViewModel.kt`) convertendo os fluxos do Room em `StateFlow` via `stateIn` e realizando a carga inicial automatica (*seed*) caso o banco esteja vazio na primeira abertura do app.
* Integracao completa do CRUD persistente em todas as telas do aplicativo.

### 2. Relacao das Telas do Aplicativo e Integracao com o Banco Room

O aplicativo possui 7 telas navegaveis integradas ao estado reativo do banco de dados:

1. Busca de Voos (SearchScreen / TelaBuscaVoos.kt): Ponto de entrada do usuario. Calcula e exibe dinamicamente o saldo de cashback acumulado a partir das reservas gravadas no Room, alem de permitir configurar filtros de viagem, datas e passageiros.
2. Radar de Precos (PriceRadarScreen / TelaRadarPrecos.kt): Apresenta historico de oscilacao de precos e configuracao de alertas tarifarios e meta minima de cashback.
3. Carteira Digital (WalletScreen / TelaCarteiraCashback.kt): Painel financeiro conectado em tempo real a tabela `reservas` do Room, separando automaticamente o Saldo Disponivel (reservas com check-in confirmado) do Saldo Pendente, listando o historico persistido e permitindo solicitacao de resgate via Pix.
4. Catalogo de Voos (FlightListScreen / TelaListaVoos.kt): Primeira tela de listagem dinamica em LazyColumn alimentada pelo `VooDao`. Permite cadastrar novos voos (`@Insert`), editar dados de voos existentes (`@Update`) e remover rotas (`@Delete`) com persistencia imediata.
5. Detalhes e Customizacao do Voo (FlightDetailsScreen / TelaDetalhesVoo.kt): Recebe o `flightId` via argumento de rota, consulta o voo correspondente e oferece calculadora em tempo real para adicionais (Bagagem Despachada e Assento VIP). Ao confirmar, grava a nova `Reserva` diretamente no banco Room.
6. Minhas Viagens (BookingListScreen / TelaListaReservas.kt): Segunda tela de listagem dinamica baseada em LazyColumn alimentada pelo `ReservaDao`. Permite emissao manual de bilhete (`@Insert`), edicao completa de passageiro/assento/bagagem (`@Update`), alternancia reativa de Check-in (`@Update`) e cancelamento/exclusao da reserva (`@Delete`).
7. Cartao de Embarque (BookingDetailScreen / TelaCartaoEmbarque.kt): Tela de detalhes da reserva que recupera o `bookingId`, cruza os dados da `Reserva` com o `Voo` correspondente (`vooId`) e permite confirmar/reverter o check-in ou cancelar e excluir a reserva diretamente no banco local.

### 3. Decisoes de Organizacao e Arquitetura

* Arquitetura em Camadas (UI -> ViewModel -> Repository -> DAO -> RoomDatabase): Desacopla a interface Jetpack Compose da logica de banco de dados SQLite, garantindo que nenhuma query rode na Main Thread.
* Estado Reativo Unidirecional (StateFlow + collectAsState): Os metodos `@Query` dos DAOs retornam `Flow<List<Voo>>` e `Flow<List<Reserva>>`. Sempre que ocorre uma insercao, edicao ou exclusao no SQLite, o Room emite uma nova lista automaticamente e o Compose recompoe a tela em tempo real sem necessidade de recarregamento manual.
* Pre-populacao Inteligente (Database Seed): No bloco `init` do `AeroBackViewModel`, o aplicativo verifica via `contarVoos()` e `contarReservas()` se o banco local esta vazio e insere os registros iniciais apenas na primeira execucao, preservando todas as alteracoes futuras do usuario entre reinicializacoes do app.
* Centralizacao das Rotas (RotasNavegacao.kt) e Barra Inferior (CustomBottomNavigation): Mantem as boas praticas de navegacao com passagem segura de IDs (`flightId` e `bookingId`) e preservacao da pilha.

### 4. Complexidade Extra e Operacoes de Detalhes

1. Calculo Tarifario Dinamico Persistente (TelaDetalhesVoo.kt): Os opcionais selecionados pelo usuario recalculam instantaneamente o valor final da passagem e o cashback proporcional, persistindo o objeto `Reserva` consolidado no SQLite.
2. Composicao Relacional e Acoes Diretas no Bilhete (TelaCartaoEmbarque.kt): Alem de unir os dados relacionais entre `Reserva` e `Voo`, a tela de detalhes atua diretamente sobre o banco Room permitindo alterar o status de check-in ou excluir o registro com retorno automatico na pilha (`popBackStack`).

### 5. Desafios Tecnicos e Solucoes Adotadas

* Compatibilidade do Room Kapt com Kotlin 2.2+: Durante a configuracao do processador de anotacoes (`kotlin-kapt`), versoes anteriores do `room-compiler` suportavam leitura de metadados apenas ate o Kotlin 2.1 (`Provided Metadata instance has version 2.2.0`). O problema foi solucionado atualizando a dependencia do Room para a versao estavel mais recente do Google Maven compativel com metadados Kotlin 2.2.
* Sincronizacao Reativa em Substituicao a SnapshotStateList: Na migracao da lista em memoria para o Room, os callbacks das telas (`onAdd`, `onUpdate`, `onDelete`) foram conectados a corrotinas no `viewModelScope.launch`, garantindo imutabilidade com `data class.copy()` e atualizacao instantanea em todas as 7 telas, incluindo os somatorios da Carteira e da Home.
* Validacao Defensiva em Formularios de Criacao e Edicao: Conversoes numericas protegidas com `replace(",", ".").toDoubleOrNull()` e `toIntOrNull()` com valores padrao seguros via operador Elvis (`?:`).

---
## Capturas de Tela do Aplicativo

### 1. Catalogo de Voos Disponiveis (TelaListaVoos)
Demonstracao da listagem reativa via LazyColumn com taxa de cashback e acao de exclusao de trechos.

![Catalogo de Voos](docs/screenshots/01_busca.png)

### 2. Detalhes e Recalculo Tarifario em Tempo Real (TelaDetalhesVoo)
Demonstracao do diferencial tecnico: calculo dinamico de opcionais (bagagem e assento VIP) e projecao monetaria de cashback.

![Detalhes e Recalculo do Voo](docs/screenshots/02_lista_voos.png)

### 3. Gestao de Reservas e Check-in Reativo (TelaListaReservas)
Demonstracao da segunda lista reativa: status do bilhete, confirmacao de check-in e remocao via icone de exclusao.

![Minhas Viagens e Reservas](docs/screenshots/03_detalhes_voo.png)

### 4. Cartao de Embarque Consolidado (TelaCartaoEmbarque)
Demonstracao da integracao relacional: cruzamento dos dados da reserva com o voo original gerando o bilhete final.

![Cartao de Embarque](docs/screenshots/05_cartao_embarque.png)

Link da ApresentaÃ§Ã£o Com Videos no Canva: https://canva.link/b155s2fcb1umoiu
