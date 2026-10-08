package com.example.aeroback

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                AeroBackApp()
            }
        }
    }
}

@Composable
fun AeroBackApp() {
    val navController = rememberNavController()

    val voos = remember {
        mutableStateListOf(
            Voo(
                id = "voo-1",
                companhia = "LATAM Airlines",
                origem = "GRU",
                destino = "MIA",
                dataIda = "15 Mar 2026",
                horario = "22:45",
                precoBase = 2850.0,
                cashbackPorcento = 12
            ),
            Voo(
                id = "voo-2",
                companhia = "Gol Linhas Aereas",
                origem = "CWB",
                destino = "GIG",
                dataIda = "20 Mar 2026",
                horario = "09:15",
                precoBase = 620.0,
                cashbackPorcento = 15
            ),
            Voo(
                id = "voo-3",
                companhia = "Azul Linhas Aereas",
                origem = "CWB",
                destino = "GRU",
                dataIda = "25 Mar 2026",
                horario = "14:30",
                precoBase = 490.0,
                cashbackPorcento = 10
            )
        )
    }

    val reservas = remember {
        mutableStateListOf(
            Reserva(
                id = "RES-101",
                vooId = "voo-1",
                passageiro = "Davyd Molina",
                assento = "03A (VIP)",
                bagagemDespachada = true,
                assentoVip = true,
                precoFinal = 3080.0,
                cashbackGanho = 369.60,
                checkInRealizado = true
            ),
            Reserva(
                id = "RES-102",
                vooId = "voo-2",
                passageiro = "Davyd Molina",
                assento = "14C",
                bagagemDespachada = false,
                assentoVip = false,
                precoFinal = 620.0,
                cashbackGanho = 93.00,
                checkInRealizado = false
            )
        )
    }

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Rotas.SEARCH,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Rotas.SEARCH) {
                TelaBuscaVoos(navController = navController)
            }
            composable(Rotas.FLIGHT_LIST) {
                TelaListaVoos(navController = navController, voos = voos)
            }
            composable(
                route = Rotas.FLIGHT_DETAIL,
                arguments = listOf(navArgument("flightId") { type = NavType.StringType })
            ) { backStackEntry ->
                val flightId = backStackEntry.arguments?.getString("flightId") ?: ""
                TelaDetalhesVoo(
                    flightId = flightId,
                    voos = voos,
                    reservas = reservas,
                    navController = navController
                )
            }
            composable(Rotas.BOOKING_LIST) {
                TelaListaReservas(
                    navController = navController,
                    reservas = reservas,
                    voos = voos
                )
            }
            composable(
                route = Rotas.BOOKING_DETAIL,
                arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
            ) { backStackEntry ->
                val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
                TelaCartaoEmbarque(
                    bookingId = bookingId,
                    reservas = reservas,
                    voos = voos,
                    navController = navController
                )
            }
            composable(Rotas.RADAR) {
                TelaRadarPrecos(navController = navController)
            }
            composable(Rotas.WALLET) {
                TelaCarteiraCashback(navController = navController)
            }
        }
    }
}

@Composable
fun CustomBottomNavigation(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val itens = listOf(
        Triple("In\u00EDcio", Rotas.SEARCH, Icons.Default.Home as ImageVector?),
        Triple("Voos", Rotas.FLIGHT_LIST, Icons.Default.Search as ImageVector?),
        Triple("Reservas", Rotas.BOOKING_LIST, Icons.Default.Person as ImageVector?),
        Triple("Radar", Rotas.RADAR, Icons.Default.Place as ImageVector?),
        Triple("Carteira", Rotas.WALLET, null as ImageVector?)
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(64.dp)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            itens.forEach { (titulo, rota, icone) ->
                val isSelected = when (rota) {
                    Rotas.SEARCH -> currentRoute == Rotas.SEARCH
                    Rotas.FLIGHT_LIST -> currentRoute == Rotas.FLIGHT_LIST || currentRoute?.startsWith("flight_detail") == true
                    Rotas.BOOKING_LIST -> currentRoute == Rotas.BOOKING_LIST || currentRoute?.startsWith("booking_detail") == true
                    Rotas.RADAR -> currentRoute == Rotas.RADAR
                    Rotas.WALLET -> currentRoute == Rotas.WALLET
                    else -> currentRoute == rota
                }

                val corAtiva = BrandGreen
                val corInativa = TextSecondary

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            navController.navigate(rota) {
                                popUpTo(Rotas.SEARCH) {
                                    inclusive = (rota == Rotas.SEARCH)
                                    saveState = false
                                }
                                launchSingleTop = true
                                restoreState = false
                            }
                        }
                        .padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (icone != null) {
                        Icon(
                            imageVector = icone,
                            contentDescription = titulo,
                            tint = if (isSelected) corAtiva else corInativa,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        IconeCarteira(
                            tint = if (isSelected) corAtiva else corInativa,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = titulo,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) corAtiva else corInativa
                    )
                }
            }
        }
    }
}

@Composable
fun IconeCarteira(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val espessura = 2.dp.toPx()

        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.12f, h * 0.22f),
            size = Size(w * 0.76f, h * 0.58f),
            cornerRadius = CornerRadius(4.dp.toPx()),
            style = Stroke(width = espessura)
        )

        drawLine(
            color = tint,
            start = Offset(w * 0.22f, h * 0.36f),
            end = Offset(w * 0.52f, h * 0.36f),
            strokeWidth = espessura,
            cap = StrokeCap.Round
        )

        drawRoundRect(
            color = Color.White,
            topLeft = Offset(w * 0.54f, h * 0.38f),
            size = Size(w * 0.36f, h * 0.26f),
            cornerRadius = CornerRadius(3.5.dp.toPx())
        )

        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.54f, h * 0.38f),
            size = Size(w * 0.36f, h * 0.26f),
            cornerRadius = CornerRadius(3.5.dp.toPx()),
            style = Stroke(width = espessura)
        )

        drawCircle(
            color = tint,
            radius = 1.6.dp.toPx(),
            center = Offset(w * 0.73f, h * 0.51f)
        )
    }
}