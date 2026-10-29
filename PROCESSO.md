# Trabalho 3 â€” PersistÃªncia de Dados com Room (AeroBack)

**Aluno:** Davyd Eduardo Ferreira Molina

---

## 1. VisÃ£o Geral da EvoluÃ§Ã£o
Nesta terceira entrega do projeto **AeroBack**, as listas volÃ¡teis em memÃ³ria (`mutableStateListOf`) foram substituÃ­das pela persistÃªncia relacional local utilizando o **Android Room (SQLite)** integrado com **Kotlin Coroutines**, **Flow/StateFlow** e **ViewModel**.

---

## 2. Estrutura TÃ©cnica Implementada

### 2.1 Entidades Room (`ModelosDados.kt`)
Foram modeladas duas tabelas relacionais principais no banco local:
- **`Voo` (`@Entity(tableName = "voos")`)**: Armazena o identificador Ãºnico (`@PrimaryKey val id: String`), companhia aÃ©rea, origem, destino, data de ida, horÃ¡rio, preÃ§o base e percentual de cashback.
- **`Reserva` (`@Entity(tableName = "reservas")`)**: Armazena o identificador Ãºnico (`@PrimaryKey val id: String`), referÃªncia ao voo (`vooId`), nome do passageiro, assento, opcionais de bagagem despachada e assento VIP, preÃ§o final calculado, cashback ganho e status de check-in (`checkInRealizado`).

### 2.2 DAOs e OperaÃ§Ãµes CRUD (`BancoDadosRoom.kt`)
- **`VooDao` (`@Dao`)**:
  - `listarVoos(): Flow<List<Voo>>`: Consulta reativa (`@Query`) que emite automaticamente atualizaÃ§Ãµes para a UI sempre que a tabela `voos` sofre alteraÃ§Ãµes.
  - `inserirVoo` / `inserirListaVoos` (`@Insert(onConflict = OnConflictStrategy.REPLACE)`): CriaÃ§Ã£o de novos voos e carga inicial (*seed*).
  - `atualizarVoo` (`@Update`): EdiÃ§Ã£o de dados de voos existentes.
  - `deletarVoo` (`@Delete`): ExclusÃ£o permanente de voos.
- **`ReservaDao` (`@Dao`)**:
  - `listarReservas(): Flow<List<Reserva>>`: Consulta reativa (`@Query`) para listagem de reservas e cÃ¡lculo em tempo real da carteira de cashback.
  - `inserirReserva` / `inserirListaReservas` (`@Insert`): PersistÃªncia de reservas originadas tanto da tela de detalhes do voo quanto da emissÃ£o manual.
  - `atualizarReserva` (`@Update`): AtualizaÃ§Ã£o do status de check-in e ediÃ§Ã£o dos dados do passageiro/assento/bagagem.
  - `deletarReserva` (`@Delete`): Cancelamento e remoÃ§Ã£o da reserva do banco local.

### 2.3 ConfiguraÃ§Ã£o do Banco (`AeroBackDatabase`)
- Implementado utilizando o padrÃ£o **Singleton thread-safe** (`@Volatile` + `synchronized`) sobre `RoomDatabase`, garantindo uma Ãºnica instÃ¢ncia do arquivo `aeroback_database.db` durante todo o ciclo de vida do aplicativo.

### 2.4 Gerenciamento de Estado e Arquitetura (`AeroBackViewModel.kt`)
- **`AeroBackRepository`**: Isola o acesso aos DAOs da camada de apresentaÃ§Ã£o.
- **`AeroBackViewModel`**: Converte os `Flow` do Room em `StateFlow` via `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())` e executa todas as escritas (`Insert`, `Update`, `Delete`) de forma assÃ­ncrona dentro de `viewModelScope.launch`.
- **PrÃ©-populaÃ§Ã£o automÃ¡tica (*Seed*)**: No bloco `init`, o ViewModel verifica se o banco estÃ¡ vazio (`contarVoos() == 0`) e insere os registros iniciais apenas na primeira execuÃ§Ã£o.