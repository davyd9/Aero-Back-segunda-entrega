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
fun TelaListaReservas(
    navController: NavController,
    reservas: SnapshotStateList<Reserva>,
    voos: List<Voo>
) {
    var showDialog by remember { mutableStateOf(false) }
    var passageiroInput by remember { mutableStateOf("") }
    var assentoInput by remember { mutableStateOf("08B") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minhas Viagens", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = BrandGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nova Reserva")
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "${reservas.size} reservas registradas",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (reservas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nenhuma viagem reservada no momento.", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(reservas, key = { it.id }) { reserva ->
                        val vooCorrespondente = voos.find { it.id == reserva.vooId }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate(Rotas.bookingDetail(reserva.id))
                                },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBackground)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = vooCorrespondente?.let { "${it.origem} ➔ ${it.destino}" } ?: "Voo AeroBack",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = TextPrimary
                                    )
                                    Surface(
                                        color = if (reserva.checkInRealizado) Color(0xFFD6F3E6) else Color(0xFFFFF0D4),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = if (reserva.checkInRealizado) "CHECK-IN CONFIRMADO" else "CHECK-IN PENDENTE",
                                            color = if (reserva.checkInRealizado) BrandGreen else Color(0xFFD97706),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Passageiro: ${reserva.passageiro} • Assento: ${reserva.assento}", fontSize = 13.sp, color = TextSecondary)
                                Text("Faturado: R$ ${"%.2f".format(reserva.precoFinal)} (+R$ ${"%.2f".format(reserva.cashbackGanho)} cashback)", fontSize = 12.sp, color = BrandGreen, fontWeight = FontWeight.SemiBold)

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = BorderColor)
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = reserva.checkInRealizado,
                                            onCheckedChange = { checked ->
                                                val index = reservas.indexOf(reserva)
                                                if (index != -1) {
                                                    reservas[index] = reserva.copy(checkInRealizado = checked)
                                                }
                                            }
                                        )
                                        Text("Realizar Check-in", fontSize = 12.sp, color = TextPrimary)
                                    }

                                    IconButton(
                                        onClick = { reservas.remove(reserva) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Cancelar Reserva", tint = Color.Red.copy(alpha = 0.7f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Emitir Reserva Manual") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = passageiroInput, onValueChange = { passageiroInput = it }, label = { Text("Nome do Passageiro") }, singleLine = true)
                    OutlinedTextField(value = assentoInput, onValueChange = { assentoInput = it }, label = { Text("Assento (ex: 15A)") }, singleLine = true)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val vooPadrao = voos.firstOrNull()
                        if (passageiroInput.isNotBlank() && vooPadrao != null) {
                            reservas.add(
                                Reserva(
                                    id = UUID.randomUUID().toString(),
                                    vooId = vooPadrao.id,
                                    passageiro = passageiroInput,
                                    assento = assentoInput.ifBlank { "10A" },
                                    bagagemDespachada = false,
                                    assentoVip = false,
                                    precoFinal = vooPadrao.precoBase,
                                    cashbackGanho = vooPadrao.precoBase * (vooPadrao.cashbackPorcento / 100.0),
                                    checkInRealizado = false
                                )
                            )
                            showDialog = false
                            passageiroInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
            }
        )
    }
}



@Composable
fun BookingListScreen(navController: androidx.navigation.NavController, reservas: androidx.compose.runtime.snapshots.SnapshotStateList<Reserva>, voos: List<Voo>) = TelaListaReservas(navController, reservas, voos)
