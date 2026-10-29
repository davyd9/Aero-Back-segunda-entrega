package com.example.aeroback

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

val BrandGreen = Color(0xFF00B074)
val BrandDarkNavy = Color(0xFF131D2E)
val CardBackground = Color(0xFFF6F8FB)
val TextPrimary = Color(0xFF19202D)
val TextSecondary = Color(0xFF7A8699)
val BorderColor = Color(0xFFE5E9F0)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                AeroBackMainApp()
            }
        }
    }
}

@Composable
fun AeroBackMainApp() {
    val navController = rememberNavController()

    val voos = remember {
        mutableStateListOf(
            Voo(
                id = "voo-1",
                companhia = "LATAM Airlines",
                origem = "GRU",
                destino = "MIA",
                dataIda = "15 Mar 2026",
                horario = "23:15",
                precoBase = 2850.0,
                cashbackPorcento = 12
            ),
            Voo(
                id = "voo-2",
                companhia = "TAP Portugal",
                origem = "GRU",
                destino = "LIS",
                dataIda = "18 Abr 2026",
                horario = "17:40",
                precoBase = 3200.0,
                cashbackPorcento = 15
            ),
            Voo(
                id = "voo-3",
                companhia = "Azul Linhas Aéreas",
                origem = "CWB",
                destino = "GRU",
                dataIda = "02 Mai 2026",
                horario = "08:20",
                precoBase = 450.0,
                cashbackPorcento = 8
            )
        )
    }

    val reservas = remember {
        mutableStateListOf(
            Reserva(
                id = "res-101",
                vooId = "voo-1",
                passageiro = "Davyd Molina",
                assento = "03A (VIP)",
                bagagemDespachada = true,
                assentoVip = true,
                precoFinal = 3080.0,
                cashbackGanho = 369.60,
                checkInRealizado = true
            )
        )
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        bottomBar = {
            CustomBottomNavigation(
                currentRoute = currentRoute,
                onNavigate = { rota ->
                    navController.navigate(rota) {
                        popUpTo(Rotas.SEARCH) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Rotas.SEARCH,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Rotas.SEARCH) {
                SearchScreen(navController = navController)
            }
            composable(Rotas.RADAR) {
                PriceRadarScreen(navController = navController)
            }
            composable(Rotas.WALLET) {
                WalletScreen(navController = navController)
            }
            composable(Rotas.FLIGHT_LIST) {
                FlightListScreen(navController = navController, voos = voos)
            }
            composable(
                route = Rotas.FLIGHT_DETAIL,
                arguments = listOf(navArgument("flightId") { type = NavType.StringType })
            ) { backStackEntry ->
                val flightId = backStackEntry.arguments?.getString("flightId") ?: ""
                FlightDetailScreen(
                    flightId = flightId,
                    voos = voos,
                    reservas = reservas,
                    navController = navController
                )
            }
            composable(Rotas.BOOKING_LIST) {
                BookingListScreen(navController = navController, reservas = reservas, voos = voos)
            }
            composable(
                route = Rotas.BOOKING_DETAIL,
                arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
            ) { backStackEntry ->
                val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
                BookingDetailScreen(
                    bookingId = bookingId,
                    reservas = reservas,
                    voos = voos,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun CustomBottomNavigation(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tabs = listOf(
                Triple("Início", Rotas.SEARCH, "home"),
                Triple("Voos", Rotas.FLIGHT_LIST, "search"),
                Triple("Reservas", Rotas.BOOKING_LIST, "profile"),
                Triple("Radar", Rotas.RADAR, "radar"),
                Triple("Carteira", Rotas.WALLET, "wallet")
            )

            tabs.forEach { tab ->
                val isSelected = currentRoute?.startsWith(tab.second.split("/")[0]) == true
                val itemColor = if (isSelected) BrandGreen else TextSecondary

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onNavigate(tab.second) }
                        .padding(horizontal = 6.dp)
                ) {
                    PixelNavIcon(type = tab.third, isSelected = isSelected)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tab.first,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = itemColor
                    )
                }
            }
        }
    }
}

@Composable
fun PixelNavIcon(type: String, isSelected: Boolean) {
    val color = if (isSelected) BrandGreen else TextSecondary
    Canvas(modifier = Modifier.size(24.dp)) {
        val w = size.width
        val h = size.height

        when (type) {
            "home" -> {
                val path = Path().apply {
                    moveTo(w * 0.15f, h * 0.42f)
                    lineTo(w * 0.5f, h * 0.12f)
                    lineTo(w * 0.85f, h * 0.42f)
                    lineTo(w * 0.85f, h * 0.88f)
                    lineTo(w * 0.15f, h * 0.88f)
                    close()
                }
                drawPath(path, color, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
            "search" -> {
                drawCircle(color, radius = w * 0.32f, center = Offset(w * 0.42f, h * 0.42f), style = Stroke(width = 2.2.dp.toPx()))
                drawLine(color, Offset(w * 0.65f, h * 0.65f), Offset(w * 0.88f, h * 0.88f), strokeWidth = 2.5.dp.toPx(), cap = StrokeCap.Round)
            }
            "radar" -> {
                drawCircle(color, radius = w * 0.42f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(width = 2.dp.toPx()))
                val needle = Path().apply {
                    moveTo(w * 0.62f, h * 0.38f)
                    lineTo(w * 0.52f, h * 0.62f)
                    lineTo(w * 0.38f, h * 0.62f)
                    lineTo(w * 0.48f, h * 0.38f)
                    close()
                }
                drawPath(needle, color, style = Stroke(width = 1.8.dp.toPx()))
            }
            "wallet" -> {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(w * 0.12f, h * 0.22f),
                    size = Size(w * 0.76f, h * 0.6f),
                    cornerRadius = CornerRadius(4.dp.toPx()),
                    style = Stroke(width = 2.dp.toPx())
                )
                drawRoundRect(
                    color = color,
                    topLeft = Offset(w * 0.6f, h * 0.42f),
                    size = Size(w * 0.28f, h * 0.22f),
                    cornerRadius = CornerRadius(3.dp.toPx()),
                    style = Stroke(width = 1.8.dp.toPx())
                )
                drawCircle(color, radius = 1.5.dp.toPx(), center = Offset(w * 0.72f, h * 0.53f))
            }
            "profile" -> {
                drawCircle(color, radius = w * 0.22f, center = Offset(w * 0.5f, h * 0.32f), style = Stroke(width = 2.dp.toPx()))
                val body = Path().apply {
                    moveTo(w * 0.18f, h * 0.86f)
                    quadraticTo(w * 0.5f, h * 0.54f, w * 0.82f, h * 0.86f)
                }
                drawPath(body, color, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
            }
        }
    }
}
