package com.example.aeroback

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun TelaListaVoos(
    navController: NavController,
    voos: List<Voo>,
    onAddVoo: (Voo) -> Unit,
    onUpdateVoo: (Voo) -> Unit,
    onDeleteVoo: (Voo) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var vooEmEdicao by remember { mutableStateOf<Voo?>(null) }

    var origemInput by remember { mutableStateOf("") }
    var destinoInput by remember { mutableStateOf("") }
    var companhiaInput by remember { mutableStateOf("") }
    var dataInput by remember { mutableStateOf("18 Nov 2026") }
    var horarioInput by remember { mutableStateOf("14:20") }
    var precoInput by remember { mutableStateOf("") }
    var cashbackInput by remember { mutableStateOf("10") }

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
                onClick = {
                    companhiaInput = ""
                    origemInput = ""
                    destinoInput = ""
                    dataInput = "18 Nov 2026"
                    horarioInput = "14:20"
                    precoInput = ""
                    cashbackInput = "10"
                    showCreateDialog = true
                },
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
                text = "${voos.size} op\u00E7\u00F5es persistidas no banco Room",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (voos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhum voo cadastrado. Toque no + para adicionar.", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(voos, key = { it.id }) { voo ->
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
                                        text = voo.companhia,
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
                                        onClick = {
                                            companhiaInput = voo.companhia
                                            origemInput = voo.origem
                                            destinoInput = voo.destino
                                            dataInput = voo.dataIda
                                            horarioInput = voo.horario
                                            precoInput = voo.precoBase.toString()
                                            cashbackInput = voo.cashbackPorcento.toString()
                                            vooEmEdicao = voo
                                        },
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
                                        onClick = { onDeleteVoo(voo) },
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

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Cadastrar Novo Voo") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = companhiaInput, onValueChange = { companhiaInput = it }, label = { Text("Companhia") }, singleLine = true)
                    OutlinedTextField(value = origemInput, onValueChange = { origemInput = it }, label = { Text("Origem (ex: GRU)") }, singleLine = true)
                    OutlinedTextField(value = destinoInput, onValueChange = { destinoInput = it }, label = { Text("Destino (ex: MIA)") }, singleLine = true)
                    OutlinedTextField(value = precoInput, onValueChange = { precoInput = it }, label = { Text("Pre\u00E7o Base (R$)") }, singleLine = true)
                    OutlinedTextField(value = cashbackInput, onValueChange = { cashbackInput = it }, label = { Text("% Cashback") }, singleLine = true)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val preco = precoInput.replace(",", ".").toDoubleOrNull() ?: 1200.0
                        val cashback = cashbackInput.toIntOrNull() ?: 10
                        if (origemInput.isNotBlank() && destinoInput.isNotBlank()) {
                            onAddVoo(
                                Voo(
                                    id = UUID.randomUUID().toString(),
                                    companhia = companhiaInput.ifBlank { "AeroBack Linhas" },
                                    origem = origemInput.uppercase(),
                                    destino = destinoInput.uppercase(),
                                    dataIda = dataInput.ifBlank { "18 Nov 2026" },
                                    horario = horarioInput.ifBlank { "14:20" },
                                    precoBase = preco,
                                    cashbackPorcento = cashback
                                )
                            )
                            showCreateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                ) {
                    Text("Adicionar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) { Text("Cancelar") }
            }
        )
    }

    if (vooEmEdicao != null) {
        val vooAtual = vooEmEdicao!!
        AlertDialog(
            onDismissRequest = { vooEmEdicao = null },
            title = { Text("Editar Voo") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = companhiaInput, onValueChange = { companhiaInput = it }, label = { Text("Companhia") }, singleLine = true)
                    OutlinedTextField(value = origemInput, onValueChange = { origemInput = it }, label = { Text("Origem") }, singleLine = true)
                    OutlinedTextField(value = destinoInput, onValueChange = { destinoInput = it }, label = { Text("Destino") }, singleLine = true)
                    OutlinedTextField(value = precoInput, onValueChange = { precoInput = it }, label = { Text("Pre\u00E7o Base (R$)") }, singleLine = true)
                    OutlinedTextField(value = cashbackInput, onValueChange = { cashbackInput = it }, label = { Text("% Cashback") }, singleLine = true)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val preco = precoInput.replace(",", ".").toDoubleOrNull() ?: vooAtual.precoBase
                        val cashback = cashbackInput.toIntOrNull() ?: vooAtual.cashbackPorcento
                        onUpdateVoo(
                            vooAtual.copy(
                                companhia = companhiaInput.ifBlank { vooAtual.companhia },
                                origem = origemInput.uppercase().ifBlank { vooAtual.origem },
                                destino = destinoInput.uppercase().ifBlank { vooAtual.destino },
                                precoBase = preco,
                                cashbackPorcento = cashback
                            )
                        )
                        vooEmEdicao = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                ) {
                    Text("Salvar Altera\u00E7\u00F5es")
                }
            },
            dismissButton = {
                TextButton(onClick = { vooEmEdicao = null }) { Text("Cancelar") }
            }
        )
    }
}