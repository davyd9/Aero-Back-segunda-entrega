# Trabalho 4 — Arquitetura MVVM com Room, Repository e StateFlow (AeroBack)

**Integrantes:**
- Davyd Eduardo Ferreira Molina (RA: 42284805)
- Guilherme Brunetti (RA: 042261015)

---

## 1. Visão Geral da Evolução (Trabalho 3 ➔ Trabalho 4)

Nesta quarta entrega do projeto **AeroBack**, refatoramos a base funcional com Room do Trabalho 3 para a **Arquitetura MVVM (Model — View — ViewModel)** completa, preservando 100% das 7 telas, identidade visual, regras de negócio de cashback e persistência SQLite, sem quebrar nenhuma funcionalidade.

Toda lógica de acesso a banco, `CoroutineScope` e estados soltos em `remember` foram removidos das telas `@Composable` e estruturados em camadas desacopladas (`data`, `data.repository`, `viewmodel` e `ui`).

---

## 2. Arquitetura Implementada por Camada

### 2.1 Camada de Dados Local (`com.example.aeroback.data`)
- **Entidades Room (`Voo.kt` e `Reserva.kt`)**: Mantidas com o mesmo esquema SQLite (`voos` e `reservas`, `version = 1`) e `@PrimaryKey(autoGenerate = true) val id: Int = 0`.
- **DAOs Reativos (`VooDao.kt` e `ReservaDao.kt`)**:
  - `buscarTodos(): Flow<List<Voo>>` (sem `suspend`, emitindo atualizações automáticas do Room sempre que a tabela `voos` sofre alterações).
  - `buscarTodos(): Flow<List<Reserva>>` (sem `suspend`, emitindo atualizações automáticas da tabela `reservas`).
  - Operações pontuais `suspend`: `buscarPorId`, `contarVoos` / `contarReservas`, `inserir`, `inserirLista`, `atualizar` e `deletar`.
- **Database Singleton (`AeroBackDatabase.kt`)**: Implementação thread-safe com `@Volatile` e `synchronized(this)` apontando para `aeroback_room_v1.db`.

### 2.2 Camada Repository (`com.example.aeroback.data.repository`)
- **`VooRepository(private val vooDao: VooDao)`**: Única fonte de verdade para voos (`listarVoos`, `buscarTodos`, `buscarVooPorId`, `contarVoos`, `inserirVoo`, `inserirListaVoos`, `atualizarVoo`, `deletarVoo`).
- **`ReservaRepository(private val reservaDao: ReservaDao)`**: Única fonte de verdade para reservas (`listarReservas`, `buscarTodos`, `buscarReservaPorId`, `contarReservas`, `inserirReserva`, `inserirListaReservas`, `atualizarReserva`, `deletarReserva`).
- Nenhuma tela ou `ViewModel` acessa `VooDao` ou `ReservaDao` diretamente.

### 2.3 Camada ViewModel + UiState + Factory (`com.example.aeroback.viewmodel`)
- **Estados Imutáveis (`VooUiState.kt` e `ReservaUiState.kt`)**: Agrupam listas reativas, item selecionado nos detalhes, flags de carregamento/erro, controles de diálogos/formulários e cálculos derivados de tarifas e cashback.
- **ViewModels (`VooViewModel.kt` e `ReservaViewModel.kt`)**:
  - Encapsulamento estrito com `private val _uiState = MutableStateFlow(...UiState())` e `val uiState: StateFlow<...UiState> = _uiState.asStateFlow()`.
  - Bloco `init` verificando seed inicial (`contarVoos() == 0` / `contarReservas() == 0` protegido por `Mutex`) e coletando os `Flow` dos repositórios em `viewModelScope.launch`.
  - Métodos de ação completos com `try/catch` e `Log.e(...)`.
- **Factories (`VooViewModelFactory.kt` e `ReservaViewModelFactory.kt`)**: Implementam `ViewModelProvider.Factory` para injeção limpa dos repositórios nos construtores dos ViewModels via `viewModel(factory = ...)`.

### 2.4 Camada de Interface (`com.example.aeroback.ui`)
- As 7 telas (`TelaBuscaVoos`, `TelaListaVoos`, `TelaDetalhesVoo`, `TelaListaReservas`, `TelaCartaoEmbarque`, `TelaRadarPrecos` e `TelaCarteiraCashback`) + `ComponentesNavegacao`, `RotasNavegacao`, `Cores` e `ui.theme`.
- Zero uso de `VooDao`, `ReservaDao`, `AeroBackDatabase`, `CoroutineScope` ou `Dispatchers.IO` na UI: as telas apenas coletam `uiState.collectAsState()` e disparam eventos nos ViewModels.

---

## 3. Respostas às 6 Perguntas Obrigatórias do Enunciado

1. **Como estava o projeto no Trabalho 3, e o que mudou na reorganização?**
   No Trabalho 3, telas, entidades e DAOs estavam misturados no pacote raiz `com.example.aeroback`, e os `@Composable` falavam diretamente com o `@Dao` usando `CoroutineScope(Dispatchers.IO).launch` e `remember` espalhados. Na reorganização para o Trabalho 4, separamos o projeto em 4 pacotes (`data`, `data.repository`, `viewmodel`, `ui`), tornamos os DAOs reativos com `Flow`, isolamos o acesso a dados nos Repositories e centralizamos todo estado e regras de negócio em `VooViewModel`/`VooUiState` e `ReservaViewModel`/`ReservaUiState` expostos por `StateFlow`.

2. **Como a dupla decidiu dividir os pacotes — o que foi para `ui`, `data`, `data.repository` e `viewmodel`?**
   - `data`: Entidades Room (`Voo`, `Reserva`), interfaces `@Dao` (`VooDao`, `ReservaDao`) e o singleton `AeroBackDatabase`.
   - `data.repository`: `VooRepository` e `ReservaRepository` mediando toda comunicação com os DAOs.
   - `viewmodel`: `VooUiState`, `ReservaUiState`, `VooViewModel`, `ReservaViewModel`, `VooViewModelFactory` e `ReservaViewModelFactory`.
   - `ui`: Todas as 7 telas `@Composable`, `ComponentesNavegacao`, `RotasNavegacao`, `Cores` e `ui.theme`.

3. **Por que o `Repository` existe — o que ele resolve que não existia antes?**
   Ele atua como única fonte de verdade (*Single Source of Truth*), impedindo que a UI ou o ViewModel dependam diretamente do Room/SQLite. Se futuramente os voos passarem a vir de uma API REST (Retrofit) combinada ao cache local, apenas o `Repository` muda, mantendo ViewModels e telas intactos.

4. **O que tem dentro do `UiState` de cada tela, e por que escolheram esses campos?**
   - Em `VooUiState`: `voos`, `vooSelecionado`, `idVooCarregado`, `isLoading`, `carregandoDetalhes`, `erro`, estados dos diálogos/inputs de criação e edição de voo, estados de personalização em tempo real dos detalhes (`nomePassageiroDetalhes`, `addBagagemDetalhes`, `addAssentoVipDetalhes` e cálculos derivados) e filtros da busca/radar.
   - Em `ReservaUiState`: `reservas`, `voosDisponiveis`, `reservaSelecionada`, `vooDaReservaSelecionada`, `idReservaCarregada`, `isLoading`, `carregandoDetalhes`, `erro`, estados do diálogo/inputs de reserva e cálculos automáticos de `saldoTotalAcumulado`, `saldoConfirmado` e `saldoPendente`.
   Escolhemos esses campos para eliminar estados `remember` espalhados nas telas e garantir que a UI seja função pura de um único estado imutável sobrevivente a mudanças de configuração.

5. **Alguma funcionalidade quebrou durante a reorganização? Como encontraram e corrigiram?**
   Sim: (a) a inicialização simultânea dos dois ViewModels podia gerar corrida no *seed* inicial (e recriar itens caso todos fossem excluídos) — resolvemos sincronizando `garantirSeedInicial()` com `Mutex` e marcando a carga inicial concluída em `SharedPreferences` nos repositórios; (b) as telas de detalhes piscavam o item anterior no primeiro frame antes do `LaunchedEffect(id)` — resolvemos controlando `idVooCarregado` e `idReservaCarregada` no `UiState`; e (c) o botão de check-in no Cartão de Embarque precisava atualizar tanto a lista geral quanto `reservaSelecionada` — resolvemos recarregando `reservaSelecionada` no `ReservaViewModel` após `atualizarReserva`.

6. **Algum integrante teve dificuldade em algum ponto? Como resolveram?**
   A principal dificuldade foi configurar a injeção de múltiplos repositórios no `ReservaViewModel` (que depende de `ReservaRepository` e `VooRepository`) usando `ViewModelProvider.Factory`, além de diferenciar métodos `Flow` contínuos (sem `suspend`) de operações de escrita `suspend fun` no `@Dao`. Resolvemos criando fábricas dedicadas (`VooViewModelFactory` e `ReservaViewModelFactory`) conectadas ao `viewModel(factory = ...)` do `lifecycle-viewmodel-compose` e revisando a coleta reativa dentro do `viewModelScope.launch`.
