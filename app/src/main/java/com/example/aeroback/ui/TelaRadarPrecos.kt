package com.example.aeroback.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.aeroback.viewmodel.VooViewModel

@Composable
fun TelaRadarPrecos(
    navController: NavController,
    vooViewModel: VooViewModel
) {
    val uiState by vooViewModel.uiState.collectAsState()
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
                text = "Radar de Pre\u00E7os",
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
            border = BorderStroke(1.dp, BorderColor)
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
                    text = "GRU \u2013 S\u00E3o Paulo \u2794 LIS \u2013 Lisboa",
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
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Hist\u00F3rico 30 dias (Ida)", fontSize = 12.sp, color = TextSecondary)
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

        Text("Configurar Notifica\u00E7\u00F5es", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Pre\u00E7o m\u00E1ximo", fontSize = 12.sp, color = TextSecondary)
                    Text("R$ ${uiState.radarPrecoMaximo.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Slider(
                    value = uiState.radarPrecoMaximo,
                    onValueChange = { vooViewModel.atualizarRadarPrecoMaximo(it) },
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
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Cashback m\u00EDnimo", fontSize = 12.sp, color = TextSecondary)
                    Text("> ${uiState.radarCashbackMinimo.toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandGreen)
                }
                Slider(
                    value = uiState.radarCashbackMinimo,
                    onValueChange = { vooViewModel.atualizarRadarCashbackMinimo(it) },
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
fun PriceRadarScreen(
    navController: NavController,
    vooViewModel: VooViewModel
) = TelaRadarPrecos(navController, vooViewModel)
