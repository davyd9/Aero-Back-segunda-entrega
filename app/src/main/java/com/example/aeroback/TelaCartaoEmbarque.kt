package com.example.aeroback

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCartaoEmbarque(
    bookingId: String,
    reservas: List<Reserva>,
    voos: List<Voo>,
    navController: NavController
) {
    val reserva = reservas.find { it.id == bookingId }
    val voo = reserva?.let { r -> voos.find { it.id == r.vooId } }

    if (reserva == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Reserva não localizada.")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cartão de Embarque", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "AEROBACK BOARDING PASS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = voo?.let { "${it.origem} ➔ ${it.destino}" } ?: "Trecho Confirmado",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Companhia: ${voo?.companhia ?: "AeroBack"} • Código: ${reserva.id.take(8).uppercase()}",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = BorderColor)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("PASSAGEIRO", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            Text(reserva.passageiro, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("ASSENTO", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            Text(reserva.assento, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrandGreen)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("STATUS CHECK-IN", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            Text(
                                if (reserva.checkInRealizado) "Realizado" else "Pendente",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (reserva.checkInRealizado) BrandGreen else Color(0xFFD97706)
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("BAGAGEM", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            Text(if (reserva.bagagemDespachada) "1 Mala (23kg)" else "Apenas de Mão", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = BrandDarkNavy
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Extrato Financeiro do Voo", color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Valor Total Faturado: R$ ${"%.2f".format(reserva.precoFinal)}", color = Color(0xFF8E9EB5), fontSize = 13.sp)
                    Text("Cashback Creditado na Conta: +R$ ${"%.2f".format(reserva.cashbackGanho)}", color = BrandGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}


@Composable
fun BookingDetailScreen(bookingId: String, reservas: List<Reserva>, voos: List<Voo>, navController: androidx.navigation.NavController) = TelaCartaoEmbarque(bookingId, reservas, voos, navController)
