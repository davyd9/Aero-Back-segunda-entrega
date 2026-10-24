package com.example.aeroback

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun TelaCarteiraCashback(navController: NavController) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val saldoDisponivel = "R$ 340,00"

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
                text = "Carteira AeroBack",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(CardBackground),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(18.dp)) {
                    drawCircle(TextSecondary, radius = size.width * 0.45f, style = Stroke(width = 1.8.dp.toPx()))
                    drawCircle(TextSecondary, radius = 1.5.dp.toPx(), center = Offset(size.width * 0.5f, size.height * 0.35f))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BrandGreen)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Saldo Disponivel", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(saldoDisponivel, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Livre para usar ou abater", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            Toast.makeText(
                                context,
                                "Solicitacao de resgate Pix de $saldoDisponivel enviada para processamento!",
                                Toast.LENGTH_LONG
                            ).show()
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(
                            text = "Resgatar Pix",
                            color = BrandGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BrandDarkNavy)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Saldo Pendente", color = Color(0xFF8E9EB5), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("R$ 92,00", color = BrandGreen, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Liberado apos o voo", color = Color(0xFF7E8EA4), fontSize = 10.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickActionButton(
                type = "pix",
                label = "Resgatar Pix",
                onClick = {
                    Toast.makeText(
                        context,
                        "Chave Pix cadastrada identificada. Resgate de $saldoDisponivel em analise.",
                        Toast.LENGTH_LONG
                    ).show()
                },
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                type = "invest",
                label = "Comprar Voos",
                onClick = { navController.navigate(Rotas.FLIGHT_LIST) },
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                type = "vip",
                label = "Minhas Viagens",
                onClick = { navController.navigate(Rotas.BOOKING_LIST) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Historico de Recompensas",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                TransactionItem("Ontem", "Abatimento compra GRU -> MIA", "-R$ 150,00", "USADO", false)
                TransactionItem("15 Fev 2026", "Cashback Voo CWB -> GRU", "+R$ 145,00", "RECEBIDO", true)
                TransactionItem("10 Fev 2026", "Bonus Campanha Miami", "+R$ 120,00", "RECEBIDO", true)
                TransactionItem("02 Fev 2026", "Resgate via Pix", "-R$ 100,00", "PIX RESGATE", false, isLast = true)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun QuickActionButton(
    type: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(86.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(if (type == "vip") Color(0xFFFFF7E6) else Color(0xFFE2F7ED)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(18.dp)) {
                    val w = size.width
                    val h = size.height
                    when (type) {
                        "pix" -> {
                            val path = Path().apply {
                                moveTo(w * 0.5f, h * 0.15f)
                                lineTo(w * 0.85f, h * 0.5f)
                                lineTo(w * 0.5f, h * 0.85f)
                                lineTo(w * 0.15f, h * 0.5f)
                                close()
                            }
                            drawPath(path, BrandGreen, style = Stroke(width = 2.dp.toPx()))
                        }
                        "invest" -> {
                            val path = Path().apply {
                                moveTo(w * 0.2f, h * 0.75f)
                                lineTo(w * 0.45f, h * 0.45f)
                                lineTo(w * 0.65f, h * 0.65f)
                                lineTo(w * 0.85f, h * 0.25f)
                            }
                            drawPath(path, BrandGreen, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
                        }
                        "vip" -> {
                            val path = Path().apply {
                                moveTo(w * 0.5f, h * 0.15f)
                                lineTo(w * 0.62f, h * 0.38f)
                                lineTo(w * 0.88f, h * 0.38f)
                                lineTo(w * 0.68f, h * 0.56f)
                                lineTo(w * 0.75f, h * 0.82f)
                                lineTo(w * 0.5f, h * 0.66f)
                                lineTo(w * 0.25f, h * 0.82f)
                                lineTo(w * 0.32f, h * 0.56f)
                                lineTo(w * 0.12f, h * 0.38f)
                                lineTo(w * 0.38f, h * 0.38f)
                                close()
                            }
                            drawPath(path, Color(0xFFF5A623), style = Stroke(width = 1.8.dp.toPx()))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
        }
    }
}

@Composable
fun TransactionItem(
    date: String,
    title: String,
    amount: String,
    tag: String,
    isCredit: Boolean,
    isLast: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = date, fontSize = 11.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = amount,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCredit) BrandGreen else TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Surface(
                color = if (isCredit) Color(0xFFE2F7ED) else CardBackground,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = tag,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCredit) BrandGreen else TextSecondary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
    if (!isLast) {
        HorizontalDivider(color = BorderColor)
    }
}

@Composable
fun WalletScreen(navController: androidx.navigation.NavController) = TelaCarteiraCashback(navController)
