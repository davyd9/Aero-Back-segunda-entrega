package com.example.aeroback.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aeroback.viewmodel.ReservaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaListaReservas(
    navController: NavController,
    reservaViewModel: ReservaViewModel
) {
    val uiState by reservaViewModel.uiState.collectAsState()

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
                onClick = { reservaViewModel.abrirDialogNovaReserva() },
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
                text = "${uiState.reservas.size} reservas persistidas no Room",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (uiState.reservas.isEmpty()) {
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
                    items(uiState.reservas, key = { it.id }) { reserva ->
                        val vooCorrespondente = uiState.voosDisponiveis.find { it.id == reserva.vooId }

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
                                        text = vooCorrespondente?.let { "${it.origem} \u2794 ${it.destino} (Reserva #${reserva.id})" }
                                            ?: "Voo AeroBack (Reserva #${reserva.id})",
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
                                Text("Passageiro: ${reserva.passageiro} \u2022 Assento: ${reserva.assento}", fontSize = 13.sp, color = TextSecondary)
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
                                                reservaViewModel.alternarCheckIn(reserva, checked)
                                            }
                                        )
                                        Text("Realizar Check-in", fontSize = 12.sp, color = TextPrimary)
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = { reservaViewModel.iniciarEdicaoReserva(reserva) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "Editar Reserva", tint = BrandDarkNavy.copy(alpha = 0.7f))
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                        IconButton(
                                            onClick = { reservaViewModel.removerReserva(reserva) },
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
    }

    if (uiState.exibirDialogNovaReserva) {
        AlertDialog(
            onDismissRequest = { reservaViewModel.fecharDialogNovaReserva() },
            title = { Text("Emitir Reserva Manual") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = uiState.passageiroInput,
                        onValueChange = { reservaViewModel.atualizarPassageiroInput(it) },
                        label = { Text("Nome do Passageiro") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = uiState.assentoInput,
                        onValueChange = { reservaViewModel.atualizarAssentoInput(it) },
                        label = { Text("Assento (ex: 15A)") },
                        singleLine = true
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = uiState.bagagemInput,
                            onCheckedChange = { reservaViewModel.atualizarBagagemInput(it) }
                        )
                        Text("Incluir bagagem despachada")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { reservaViewModel.adicionarReservaManual() },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { reservaViewModel.fecharDialogNovaReserva() }) { Text("Cancelar") }
            }
        )
    }

    val atual = uiState.reservaEmEdicao
    if (atual != null) {
        AlertDialog(
            onDismissRequest = { reservaViewModel.cancelarEdicaoReserva() },
            title = { Text("Editar Reserva #${atual.id}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = uiState.passageiroInput,
                        onValueChange = { reservaViewModel.atualizarPassageiroInput(it) },
                        label = { Text("Nome do Passageiro") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = uiState.assentoInput,
                        onValueChange = { reservaViewModel.atualizarAssentoInput(it) },
                        label = { Text("Assento") },
                        singleLine = true
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = uiState.bagagemInput,
                            onCheckedChange = { reservaViewModel.atualizarBagagemInput(it) }
                        )
                        Text("Bagagem despachada (23kg)")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { reservaViewModel.salvarEdicaoReserva() },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                ) {
                    Text("Atualizar")
                }
            },
            dismissButton = {
                TextButton(onClick = { reservaViewModel.cancelarEdicaoReserva() }) { Text("Cancelar") }
            }
        )
    }
}
