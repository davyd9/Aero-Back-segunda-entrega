# Trabalho 3 — Persistência de Dados com Room (AeroBack)

**Integrantes:**
- Davyd Eduardo Ferreira Molina (RA: 42284805)
- Guilherme Brunetti (RA: 042261015)

---

## 1. Visão Geral da Evolução (Trabalho 2 ➔ Trabalho 3)
Nesta terceira entrega do projeto **AeroBack**, substituímos as listas voláteis em memória (`mutableStateListOf`) pela persistência local no SQLite utilizando o **Android Jetpack Room** com **KSP**, **Coroutines (`Dispatchers.IO`)** e **`LaunchedEffect(Unit)`**, mantendo as 7 telas e o fluxo de navegação do Trabalho 2.

---

## 2. Estrutura Técnica do Room Implementada

### 2.1 Entidades Room (`ModelosDados.kt`)
As duas `data class` do Trabalho 2 foram adaptadas para entidades do Room com chave primária autogerada (`Int`):
- **`Voo` (`@Entity(tableName = "voos")`)**:
  - `@PrimaryKey(autoGenerate = true) val id: Int = 0`
  - `companhia`, `origem`, `destino`, `dataIda`, `horario`, `precoBase`, `cashbackPorcento`
- **`Reserva` (`@Entity(tableName = "reservas")`)**:
  - `@PrimaryKey(autoGenerate = true) val id: Int = 0`
  - `vooId: Int`, `passageiro`, `assento`, `bagagemDespachada`, `assentoVip`, `precoFinal`, `cashbackGanho`, `checkInRealizado`

### 2.2 Interfaces `@Dao` (`BancoDadosRoom.kt`)
Todas as operações são `suspend fun` e executadas fora da Main Thread:
- **`VooDao` (`@Dao`)**:
  - `@Insert suspend fun inserir(voo: Voo)`
  - `@Query("SELECT * FROM voos ORDER BY id ASC") suspend fun buscarTodos(): List<Voo>`
  - `@Query("SELECT * FROM voos WHERE id = :id") suspend fun buscarPorId(id: Int): Voo?`
  - `@Update suspend fun atualizar(voo: Voo)`
  - `@Delete suspend fun deletar(voo: Voo)`
- **`ReservaDao` (`@Dao`)**:
  - `@Insert suspend fun inserir(reserva: Reserva)`
  - `@Query("SELECT * FROM reservas ORDER BY id DESC") suspend fun buscarTodos(): List<Reserva>`
  - `@Query("SELECT * FROM reservas WHERE id = :id") suspend fun buscarPorId(id: Int): Reserva?`
  - `@Update suspend fun atualizar(reserva: Reserva)`
  - `@Delete suspend fun deletar(reserva: Reserva)`

### 2.3 Banco de Dados Singleton (`AppDatabase`)
- Classe abstrata `AppDatabase : RoomDatabase()` anotada com `@Database(entities = [Voo::class, Reserva::class], version = 1)`.
- Segue o padrão **Singleton** visto em aula (`companion object`, `@Volatile private var INSTANCE` e bloco `synchronized(this)` com `Room.databaseBuilder`).

### 2.4 Operações CRUD nas Telas e Busca por ID nos Detalhes
- **Listas (`TelaListaVoos.kt` e `TelaListaReservas.kt`)**: Ao abrir a tela, um `LaunchedEffect(Unit)` chama `dao.buscarTodos()` dentro de `CoroutineScope(Dispatchers.IO).launch` com `try/catch` e `Log.e(...)`. Os botões e diálogos de adicionar, editar e remover chamam `dao.inserir(...)`, `dao.atualizar(...)` e `dao.deletar(...)` no `Dispatchers.IO` e atualizam a lista exibida.
- **Telas de Detalhes (`TelaDetalhesVoo.kt` e `TelaCartaoEmbarque.kt`)**: Recebem apenas o `id: Int` pela rota de navegação (`NavType.IntType`) e buscam o registro diretamente no banco SQLite via `vooDao.buscarPorId(flightId)` e `reservaDao.buscarPorId(bookingId)` dentro de `LaunchedEffect(Unit)`.
