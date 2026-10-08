package com.example.aeroback

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun TelaRadarPrecos(navController: NavController) {
    var maxPrice by remember { mutableFloatStateOf(2500f) }
    var minCashback by remember { mutableFloatStateOf(10f) }
    var alert1Active by remember { mutableStateOf(true) }
    var alert2Active by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Radar de Preços",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Surface(
                color = Color(0xFFE2F7ED),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "MONITORANDO",
                    color = BrandGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate(Rotas.FLIGHT_LIST) },
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Canvas(modifier = Modifier.size(20.dp)) {
                    drawCircle(BrandGreen, radius = size.width * 0.45f, style = Stroke(width = 2.dp.toPx()))
                    drawCircle(BrandGreen, radius = size.width * 0.2f, style = Stroke(width = 2.dp.toPx()))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "GRU – São Paulo ➔ LIS – Lisboa",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Histórico 30 dias (Ida)", fontSize = 12.sp, color = TextSecondary)
                    Text(text = "Menor: R$ 2.390", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandGreen)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val path = Path().apply {
                            moveTo(0f, size.height * 0.7f)
                            lineTo(size.width * 0.25f, size.height * 0.45f)
                            lineTo(size.width * 0.5f, size.height * 0.8f)
                            lineTo(size.width * 0.75f, size.height * 0.15f)
                            lineTo(size.width, size.height * 0.4f)
                        }
                        drawPath(
                            path = path,
                            color = BrandGreen,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawCircle(Color(0xFF1E293B), radius = 4.dp.toPx(), center = Offset(0f, size.height * 0.7f))
                        drawCircle(Color(0xFF1E293B), radius = 4.dp.toPx(), center = Offset(size.width * 0.25f, size.height * 0.45f))
                        drawCircle(Color(0xFF1E293B), radius = 4.dp.toPx(), center = Offset(size.width * 0.5f, size.height * 0.8f))
                        drawCircle(BrandGreen, radius = 5.dp.toPx(), center = Offset(size.width * 0.75f, size.height * 0.15f))
                        drawCircle(Color(0xFF1E293B), radius = 4.dp.toPx(), center = Offset(size.width, size.height * 0.4f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Configurar Notificações", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Preço máximo", fontSize = 12.sp, color = TextSecondary)
                    Text("R$ ${maxPrice.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Slider(
                    value = maxPrice,
                    onValueChange = { maxPrice = it },
                    valueRange = 1500f..5000f,
                    colors = SliderDefaults.colors(thumbColor = BrandGreen, activeTrackColor = BrandGreen)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Cashback mínimo", fontSize = 12.sp, color = TextSecondary)
                    Text("> ${minCashback.toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandGreen)
                }
                Slider(
                    value = minCashback,
                    onValueChange = { minCashback = it },
                    valueRange = 1f..30f,
                    colors = SliderDefaults.colors(thumbColor = BrandGreen, activeTrackColor = BrandGreen)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate(Rotas.FLIGHT_LIST) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
        ) {
            Text(text = "Explorar Voos no Radar", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}


@Composable
fun PriceRadarScreen(navController: androidx.navigation.NavController) = TelaRadarPrecos(navController)
