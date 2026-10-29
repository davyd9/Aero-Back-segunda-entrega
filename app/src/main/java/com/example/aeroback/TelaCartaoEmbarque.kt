package com.example.aeroback

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCartaoEmbarque(
    bookingId: String,
    reservas: List<Reserva>,
    voos: List<Voo>,
    onUpdateReserva: (Reserva) -> Unit = {},
    onDeleteReserva: (Reserva) -> Unit = {},
    navController: NavController
) {
    val reserva = reservas.find { it.id == bookingId }
    val voo = reserva?.let { r -> voos.find { it.id == r.vooId } }

    if (reserva == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Reserva n\u00E3o localizada.")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cart\u00E3o de Embarque", fontWeight = FontWeight.Bold) },
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
                        text = voo?.let { "${it.origem} \u2794 ${it.destino}" } ?: "Trecho Confirmado",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Companhia: ${voo?.companhia ?: "AeroBack"} \u2022 C\u00F3digo: ${reserva.id.take(8).uppercase()}",
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
                            Text(
                                if (reserva.bagagemDespachada) "1 Mala (23kg)" else "Apenas de M\u00E3o",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
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

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    onUpdateReserva(reserva.copy(checkInRealizado = !reserva.checkInRealizado))
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
            ) {
                Text(
                    if (reserva.checkInRealizado) "Marcar Check-in como Pendente" else "Confirmar Check-in Agora",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    onDeleteReserva(reserva)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancelar e Excluir Reserva", color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }
    }
}