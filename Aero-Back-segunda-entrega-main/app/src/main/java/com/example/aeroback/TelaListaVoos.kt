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
fun TelaListaVoos(
    navController: NavController,
    voos: SnapshotStateList<Voo>
) {
    var showDialog by remember { mutableStateOf(false) }
    var origemInput by remember { mutableStateOf("") }
    var destinoInput by remember { mutableStateOf("") }
    var companhiaInput by remember { mutableStateOf("") }
    var precoInput by remember { mutableStateOf("") }
    var cashbackInput by remember { mutableStateOf("10") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Voos Disponíveis", fontWeight = FontWeight.Bold) },
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
                text = "${voos.size} opções com cashback acumulativo",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

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
                                    Text("${voo.origem} ➔ ${voo.destino}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                                    Text("Data: ${voo.dataIda} • ${voo.horario}", fontSize = 12.sp, color = TextSecondary)
                                }
                                Text("R$ ${"%.2f".format(voo.precoBase)}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrandGreen)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(
                                    onClick = { voos.remove(voo) },
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

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Cadastrar Novo Voo") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = companhiaInput, onValueChange = { companhiaInput = it }, label = { Text("Companhia") }, singleLine = true)
                    OutlinedTextField(value = origemInput, onValueChange = { origemInput = it }, label = { Text("Origem (ex: GRU)") }, singleLine = true)
                    OutlinedTextField(value = destinoInput, onValueChange = { destinoInput = it }, label = { Text("Destino (ex: MIA)") }, singleLine = true)
                    OutlinedTextField(value = precoInput, onValueChange = { precoInput = it }, label = { Text("Preço Base (R$)") }, singleLine = true)
                    OutlinedTextField(value = cashbackInput, onValueChange = { cashbackInput = it }, label = { Text("% Cashback") }, singleLine = true)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val preco = precoInput.toDoubleOrNull() ?: 1200.0
                        val cashback = cashbackInput.toIntOrNull() ?: 10
                        if (origemInput.isNotBlank() && destinoInput.isNotBlank()) {
                            voos.add(
                                Voo(
                                    id = UUID.randomUUID().toString(),
                                    companhia = companhiaInput.ifBlank { "AeroBack Linhas" },
                                    origem = origemInput.uppercase(),
                                    destino = destinoInput.uppercase(),
                                    dataIda = "18 Nov 2026",
                                    horario = "14:20",
                                    precoBase = preco,
                                    cashbackPorcento = cashback
                                )
                            )
                            showDialog = false
                            origemInput = ""
                            destinoInput = ""
                            companhiaInput = ""
                            precoInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                ) {
                    Text("Adicionar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
            }
        )
    }
}



@Composable
fun FlightListScreen(navController: androidx.navigation.NavController, voos: androidx.compose.runtime.snapshots.SnapshotStateList<Voo>) = TelaListaVoos(navController, voos)
