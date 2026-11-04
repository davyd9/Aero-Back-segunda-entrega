package com.example.aeroback.ui

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aeroback.viewmodel.ReservaViewModel
import com.example.aeroback.viewmodel.VooViewModel

@Composable
fun TelaBuscaVoos(
    navController: NavController,
    vooViewModel: VooViewModel,
    reservaViewModel: ReservaViewModel
) {
    val context = LocalContext.current
    val vooUiState by vooViewModel.uiState.collectAsState()
    val reservaUiState by reservaViewModel.uiState.collectAsState()

    val scrollState = rememberScrollState()
    val saldoExibido = reservaUiState.saldoTotalAcumulado

    if (vooUiState.dialogDataAbertaPara != null) {
        val tipoData = vooUiState.dialogDataAbertaPara ?: "Ida"
        val opcoesDatas = listOf("15 Mar 2026", "22 Mar 2026", "29 Mar 2026", "05 Abr 2026", "12 Abr 2026")

        AlertDialog(
            onDismissRequest = { vooViewModel.abrirDialogDataBusca(null) },
            title = { Text("Selecionar Data de $tipoData", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    opcoesDatas.forEach { opcao ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    vooViewModel.selecionarDataBusca(tipoData, opcao)
                                    Toast.makeText(context, "Data de $tipoData definida para $opcao", Toast.LENGTH_SHORT).show()
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = CardBackground
                        ) {
                            Text(
                                text = opcao,
                                modifier = Modifier.padding(12.dp),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { vooViewModel.abrirDialogDataBusca(null) }) {
                    Text("Fechar", color = BrandGreen)
                }
            }
        )
    }

    if (vooUiState.dialogPassageirosAberto) {
        val opcoesPassageiros = listOf(
            "1 Adulto, Econ\u00F4mica",
            "2 Adultos, Econ\u00F4mica",
            "1 Adulto, Executiva",
            "2 Adultos, Executiva",
            "Fam\u00EDlia (2 Adultos + 1 Crian\u00E7a)"
        )

        AlertDialog(
            onDismissRequest = { vooViewModel.definirDialogPassageirosAberto(false) },
            title = { Text("Passageiros e Cabine", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    opcoesPassageiros.forEach { opcao ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    vooViewModel.selecionarPassageirosConfig(opcao)
                                    Toast.makeText(context, "Configura\u00E7\u00E3o alterada para: $opcao", Toast.LENGTH_SHORT).show()
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = CardBackground
                        ) {
                            Text(
                                text = opcao,
                                modifier = Modifier.padding(12.dp),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { vooViewModel.definirDialogPassageirosAberto(false) }) {
                    Text("Fechar", color = BrandGreen)
                }
            }
        )
    }

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD6F3E6)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "DM",
                        color = BrandGreen,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Bem-vindo de volta,",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "Ol\u00E1, Davyd",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable {
                        Toast.makeText(
                            context,
                            "Banco Room sincronizado (${reservaUiState.reservas.size} reservas ativas).",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                shape = CircleShape,
                color = Color.White,
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(18.dp)) {
                        val w = size.width
                        val h = size.height
                        val bellPath = Path().apply {
                            moveTo(w * 0.5f, h * 0.15f)
                            lineTo(w * 0.72f, h * 0.55f)
                            lineTo(w * 0.85f, h * 0.7f)
                            lineTo(w * 0.15f, h * 0.7f)
                            lineTo(w * 0.28f, h * 0.55f)
                            close()
                        }
                        drawPath(bellPath, TextPrimary, style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                        drawCircle(TextPrimary, radius = 1.6.dp.toPx(), center = Offset(w * 0.5f, h * 0.82f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate(Rotas.WALLET) },
            colors = CardDefaults.cardColors(containerColor = BrandDarkNavy)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Saldo AeroBack acumulado (Room)",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Surface(
                        color = Color(0xFF13382C),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "CARTEIRA ATIVA",
                            color = BrandGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "R$ ${"%.2f".format(saldoExibido)}",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandGreen
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Calculado a partir de ${reservaUiState.reservas.size} reservas salvas localmente",
                    fontSize = 11.sp,
                    color = Color(0xFF8E9EB5)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardBackground, RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("Ida e Volta", "Somente Ida", "M\u00FAltiplos").forEach { type ->
                        val isSelected = vooUiState.tripType == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color.White else Color.Transparent)
                                .clickable { vooViewModel.selecionarTipoViagem(type) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = type,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) TextPrimary else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = CardBackground
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Canvas(modifier = Modifier.size(14.dp)) {
                                        val path = Path().apply {
                                            moveTo(size.width * 0.2f, size.height * 0.8f)
                                            lineTo(size.width * 0.8f, size.height * 0.2f)
                                        }
                                        drawPath(path, BrandGreen, style = Stroke(width = 2.dp.toPx()))
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Origem", fontSize = 10.sp, color = TextSecondary)
                                    Text(vooUiState.origemBusca, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                            }
                        }

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = CardBackground
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Canvas(modifier = Modifier.size(14.dp)) {
                                        val path = Path().apply {
                                            moveTo(size.width * 0.2f, size.height * 0.8f)
                                            lineTo(size.width * 0.8f, size.height * 0.2f)
                                        }
                                        drawPath(path, BrandGreen, style = Stroke(width = 2.dp.toPx()))
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Destino", fontSize = 10.sp, color = TextSecondary)
                                    Text(vooUiState.destinoBusca, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 16.dp)
                            .size(36.dp)
                            .clickable {
                                vooViewModel.inverterOrigemDestinoBusca()
                                Toast.makeText(context, "Origem e destino invertidos", Toast.LENGTH_SHORT).show()
                            },
                        shape = CircleShape,
                        color = Color.White,
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Canvas(modifier = Modifier.size(16.dp)) {
                                val w = size.width
                                val h = size.height
                                val path1 = Path().apply {
                                    moveTo(w * 0.2f, h * 0.35f)
                                    lineTo(w * 0.8f, h * 0.35f)
                                    lineTo(w * 0.65f, h * 0.15f)
                                }
                                val path2 = Path().apply {
                                    moveTo(w * 0.8f, h * 0.65f)
                                    lineTo(w * 0.2f, h * 0.65f)
                                    lineTo(w * 0.35f, h * 0.85f)
                                }
                                drawPath(path1, TextPrimary, style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                                drawPath(path2, TextPrimary, style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { vooViewModel.abrirDialogDataBusca("Ida") },
                        shape = RoundedCornerShape(12.dp),
                        color = CardBackground
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Data de Ida (Toque)", fontSize = 10.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(vooUiState.dataIdaBusca, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }

                    if (vooUiState.tripType == "Ida e Volta") {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { vooViewModel.abrirDialogDataBusca("Volta") },
                            shape = RoundedCornerShape(12.dp),
                            color = CardBackground
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Data de Volta (Toque)", fontSize = 10.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(vooUiState.dataVoltaBusca, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { vooViewModel.definirDialogPassageirosAberto(true) },
                    shape = RoundedCornerShape(12.dp),
                    color = CardBackground
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Passageiros e Classe", fontSize = 10.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(vooUiState.passageirosConfig, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Text("Alterar", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandGreen)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate(Rotas.FLIGHT_LIST) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                ) {
                    Text("Buscar Voos", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Ofertas com Maior Retorno", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(
                text = "Ver tudo",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = BrandGreen,
                modifier = Modifier.clickable { navController.navigate(Rotas.FLIGHT_LIST) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate(Rotas.FLIGHT_LIST) },
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFD6F3E6)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(24.dp)) {
                        val path = Path().apply {
                            moveTo(size.width * 0.15f, size.height * 0.65f)
                            lineTo(size.width * 0.5f, size.height * 0.25f)
                            lineTo(size.width * 0.85f, size.height * 0.65f)
                        }
                        drawPath(
                            path = path,
                            color = BrandGreen,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Surface(
                        color = Color(0xFFD8F3E5),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "+15% CASHBACK",
                            color = BrandGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Voos Internacionais Promocionais", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Toque para ver a lista de rotas dispon\u00EDveis", fontSize = 11.sp, color = TextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SearchScreen(
    navController: NavController,
    vooViewModel: VooViewModel,
    reservaViewModel: ReservaViewModel
) = TelaBuscaVoos(navController, vooViewModel, reservaViewModel)
