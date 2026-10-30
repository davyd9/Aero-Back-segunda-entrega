package com.example.aeroback

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCartaoEmbarque(
    bookingId: Int,
    navController: NavController
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val reservaDao = remember { db.reservaDao() }
    val vooDao = remember { db.vooDao() }

    var reserva by remember { mutableStateOf<Reserva?>(null) }
    var voo by remember { mutableStateOf<Voo?>(null) }
    var carregando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val reservaBanco = reservaDao.buscarPorId(bookingId)
                val vooBanco = reservaBanco?.let { vooDao.buscarPorId(it.vooId) }
                withContext(Dispatchers.Main) {
                    reserva = reservaBanco
                    voo = vooBanco
                    carregando = false
                }
            } catch (e: Exception) {
                Log.e("TelaCartaoEmbarque", "Erro ao buscar reserva por id ($bookingId): ${e.message}", e)
                withContext(Dispatchers.Main) {
                    carregando = false
                }
            }
        }
    }

    if (carregando) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BrandGreen)
        }
        return
    }

    val reservaAtual = reserva
    if (reservaAtual == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Reserva #$bookingId n\u00E3o localizada no banco Room.")
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
                        text = "Companhia: ${voo?.companhia ?: "AeroBack"} \u2022 C\u00F3digo: RES-${reservaAtual.id}",
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
                            Text(reservaAtual.passageiro, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("ASSENTO", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            Text(reservaAtual.assento, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrandGreen)
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
                                if (reservaAtual.checkInRealizado) "Realizado" else "Pendente",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (reservaAtual.checkInRealizado) BrandGreen else Color(0xFFD97706)
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("BAGAGEM", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            Text(
                                if (reservaAtual.bagagemDespachada) "1 Mala (23kg)" else "Apenas de M\u00E3o",
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
                    Text("Valor Total Faturado: R$ ${"%.2f".format(reservaAtual.precoFinal)}", color = Color(0xFF8E9EB5), fontSize = 13.sp)
                    Text("Cashback Creditado na Conta: +R$ ${"%.2f".format(reservaAtual.cashbackGanho)}", color = BrandGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val atualizada = reservaAtual.copy(checkInRealizado = !reservaAtual.checkInRealizado)
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            reservaDao.atualizar(atualizada)
                            val recarregada = reservaDao.buscarPorId(bookingId)
                            withContext(Dispatchers.Main) {
                                reserva = recarregada
                            }
                        } catch (e: Exception) {
                            Log.e("TelaCartaoEmbarque", "Erro ao atualizar check-in: ${e.message}", e)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
            ) {
                Text(
                    if (reservaAtual.checkInRealizado) "Marcar Check-in como Pendente" else "Confirmar Check-in Agora",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            reservaDao.deletar(reservaAtual)
                            withContext(Dispatchers.Main) {
                                navController.popBackStack()
                            }
                        } catch (e: Exception) {
                            Log.e("TelaCartaoEmbarque", "Erro ao deletar reserva: ${e.message}", e)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancelar e Excluir Reserva", color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }
    }
}
