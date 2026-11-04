package com.example.aeroback.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.navigation.compose.currentBackStackEntryAsState

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
