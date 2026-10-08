# Relatório de Decisões e Processo de Desenvolvimento — MAF (AeroBack)

## 1. Como estava o projeto no Trabalho 1, e o que mudou para chegar até aqui?
No Trabalho 1, o AeroBack contava com 3 telas estáticas: Busca de Voos (`SearchScreen`), Radar de Preços (`PriceRadarScreen`) e Carteira Digital de Cashback (`WalletScreen`). Os botões não realizavam trocas de telas reais e a navegação da barra inferior alternava apenas uma variável de estado estática (`when (selectedTab)`).

Para o MAF (Trabalho 2), integramos uma arquitetura completa de navegação com `NavHost`, `NavController` e rotas fortemente tipadas em um `object Rotas`. Além disso, implementamos duas coleções reativas (`mutableStateListOf`) gerenciadas no nível da aplicação para as entidades de `Voo` e `Reserva`, totalizando 7 telas com adição, remoção, alteração de estado e navegação parametrizada.

## 2. Por que essas telas novas — o que cada uma faz e por que o trio escolheu elas?
Criamos 4 novas telas navegáveis divididas em duas entidades de domínio do app:
- `FlightListScreen` (Lista de Voos): Permite listar os voos que oferecem cashback, filtrar destinos e adicionar novas ofertas de voos dinamicamente.
- `FlightDetailScreen` (Detalhe do Voo): Apresenta o itinerário detalhado e permite customizar adicionais de viagem antes de emitir a reserva.
- `BookingListScreen` (Lista de Reservas): Permite visualizar as viagens adquiridas, emitir novas reservas manuais, cancelar passagens e realizar o check-in online.
- `BookingDetailScreen` (Cartão de Embarque): Exibe o bilhete eletrônico completo combinando as informações da reserva e do voo selecionado.

Essas telas foram escolhidas porque representam o fluxo de valor central do AeroBack: pesquisar um voo, calcular benefícios em tempo real e emitir a passagem.

## 3. Que decisão de configuração/organização do código o trio tomou, e por quê?
- **Rotas centralizadas em `AppNavigation.kt`:** Definimos um `object Rotas` contendo constantes `const val String` para todas as telas e funções utilitárias que montam rotas com argumentos (`Rotas.flightDetail(id)` e `Rotas.bookingDetail(id)`), prevenindo erros de digitação.
- **Estado reativo no nível da aplicação:** As listas reativas (`mutableStateListOf<Voo>` e `mutableStateListOf<Reserva>`) foram declaradas na função raiz `AeroBackMainApp()`. Isso assegura que os itens adicionados ou removidos persistam em memória durante toda a navegação e recomposição das rotas.
- **Navegação com `popBackStack()`:** Adicionamos `TopAppBar` nas telas de detalhes e listas filhas permitindo o retorno à pilha anterior sem recriar o grafo de navegação.

## 4. Qual foi a complexidade extra que o trio colocou na tela de Detalhes, e por que escolheram justamente essa?
Na tela `FlightDetailScreen`, implementamos uma **calculadora de adicionais e cashback em tempo real**:
1. O usuário pode alternar adicionais por meio de `Checkbox` (como Bagagem Despachada de +R$ 150,00 e Assento Conforto de +R$ 80,00).
2. O composable recalcula instantaneamente o valor final da passagem somado aos adicionais e o valor líquido em Reais de cashback creditado na carteira (`valorTotal * (cashbackPorcento / 100)`).
3. Ao clicar em "Confirmar Reserva com Cashback", os dados calculados são convertidos diretamente em uma nova instância de `Reserva` vinculada ao ID do voo e adicionados à lista da segunda entidade.

Essa complexidade foi escolhida por refletir diretamente o diferencial de negócio do AeroBack (conversão de compra em saldo de cashback).

## 5. Algum integrante teve dificuldade em algum ponto? Como resolveram?
A principal dificuldade encontrada foi garantir que a remoção e alteração do status de check-in na lista de reservas refletisse imediatamente na recomposição da `LazyColumn`. Resolvemos isso utilizando a cópia imutável do objeto com `reserva.copy(checkInRealizado = checked)` associada à atribuição indexada na `SnapshotStateList`, garantindo que o Jetpack Compose detectasse a mutação de estado.
