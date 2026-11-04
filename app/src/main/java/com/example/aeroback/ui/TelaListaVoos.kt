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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import com.example.aeroback.viewmodel.VooViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaListaVoos(
    navController: NavController,
    vooViewModel: VooViewModel
) {
    val uiState by vooViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Voos Dispon\u00EDveis", fontWeight = FontWeight.Bold) },
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
                onClick = { vooViewModel.abrirDialogNovoVoo() },
                containerColor = BrandGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Voo")
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
                text = "${uiState.voos.size} op\u00E7\u00F5es persistidas no banco Room",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (uiState.voos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhum voo cadastrado. Toque no + para adicionar.", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(uiState.voos, key = { it.id }) { voo ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { navController.navigate(Rotas.flightDetail(voo.id)) },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBackground)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${voo.companhia} (#${voo.id})",
                                        fontWeight = FontWeight.Bold,
                                        color = BrandDarkNavy,
                                        fontSize = 15.sp
                                    )
                                    Surface(
                                        color = Color(0xFFD6F3E6),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "+${voo.cashbackPorcento}% CASHBACK",
                                            color = BrandGreen,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("${voo.origem} \u2794 ${voo.destino}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                                        Text("Data: ${voo.dataIda} \u2022 ${voo.horario}", fontSize = 12.sp, color = TextSecondary)
                                    }
                                    Text("R$ ${"%.2f".format(voo.precoBase)}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrandGreen)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(
                                        onClick = { vooViewModel.iniciarEdicaoVoo(voo) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Editar voo",
                                            tint = BrandDarkNavy.copy(alpha = 0.7f)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = { vooViewModel.removerVoo(voo) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Remover voo",
                                            tint = Color.Red.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (uiState.exibirDialogNovoVoo) {
        AlertDialog(
            onDismissRequest = { vooViewModel.fecharDialogNovoVoo() },
            title = { Text("Cadastrar Novo Voo") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = uiState.companhiaInput, onValueChange = { vooViewModel.atualizarCompanhiaInput(it) }, label = { Text("Companhia") }, singleLine = true)
                    OutlinedTextField(value = uiState.origemInput, onValueChange = { vooViewModel.atualizarOrigemInput(it) }, label = { Text("Origem (ex: GRU)") }, singleLine = true)
                    OutlinedTextField(value = uiState.destinoInput, onValueChange = { vooViewModel.atualizarDestinoInput(it) }, label = { Text("Destino (ex: MIA)") }, singleLine = true)
                    OutlinedTextField(value = uiState.dataInput, onValueChange = { vooViewModel.atualizarDataInput(it) }, label = { Text("Data de Ida") }, singleLine = true)
                    OutlinedTextField(value = uiState.horarioInput, onValueChange = { vooViewModel.atualizarHorarioInput(it) }, label = { Text("Hor\u00E1rio") }, singleLine = true)
                    OutlinedTextField(value = uiState.precoInput, onValueChange = { vooViewModel.atualizarPrecoInput(it) }, label = { Text("Pre\u00E7o Base (R$)") }, singleLine = true)
                    OutlinedTextField(value = uiState.cashbackInput, onValueChange = { vooViewModel.atualizarCashbackInput(it) }, label = { Text("% Cashback") }, singleLine = true)
                }
            },
            confirmButton = {
                Button(
                    onClick = { vooViewModel.adicionarVoo() },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                ) {
                    Text("Adicionar")
                }
            },
            dismissButton = {
                TextButton(onClick = { vooViewModel.fecharDialogNovoVoo() }) { Text("Cancelar") }
            }
        )
    }

    val vooAtual = uiState.vooEmEdicao
    if (vooAtual != null) {
        AlertDialog(
            onDismissRequest = { vooViewModel.cancelarEdicaoVoo() },
            title = { Text("Editar Voo #${vooAtual.id}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = uiState.companhiaInput, onValueChange = { vooViewModel.atualizarCompanhiaInput(it) }, label = { Text("Companhia") }, singleLine = true)
                    OutlinedTextField(value = uiState.origemInput, onValueChange = { vooViewModel.atualizarOrigemInput(it) }, label = { Text("Origem") }, singleLine = true)
                    OutlinedTextField(value = uiState.destinoInput, onValueChange = { vooViewModel.atualizarDestinoInput(it) }, label = { Text("Destino") }, singleLine = true)
                    OutlinedTextField(value = uiState.dataInput, onValueChange = { vooViewModel.atualizarDataInput(it) }, label = { Text("Data de Ida") }, singleLine = true)
                    OutlinedTextField(value = uiState.horarioInput, onValueChange = { vooViewModel.atualizarHorarioInput(it) }, label = { Text("Hor\u00E1rio") }, singleLine = true)
                    OutlinedTextField(value = uiState.precoInput, onValueChange = { vooViewModel.atualizarPrecoInput(it) }, label = { Text("Pre\u00E7o Base (R$)") }, singleLine = true)
                    OutlinedTextField(value = uiState.cashbackInput, onValueChange = { vooViewModel.atualizarCashbackInput(it) }, label = { Text("% Cashback") }, singleLine = true)
                }
            },
            confirmButton = {
                Button(
                    onClick = { vooViewModel.salvarEdicaoVoo() },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                ) {
                    Text("Salvar Altera\u00E7\u00F5es")
                }
            },
            dismissButton = {
                TextButton(onClick = { vooViewModel.cancelarEdicaoVoo() }) { Text("Cancelar") }
            }
        )
    }
}
