package com.example.aeroback

import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

suspend fun buscarReservasDoBanco(reservaDao: ReservaDao): List<Reserva> {
    return try {
        reservaDao.buscarTodos()
    } catch (e: Exception) {
        Log.e("TelaListaReservas", "Erro ao buscar reservas no Room: ${e.message}", e)
        emptyList()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaListaReservas(
    navController: NavController
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val reservaDao = remember { db.reservaDao() }
    val vooDao = remember { db.vooDao() }

    var reservas by remember { mutableStateOf<List<Reserva>>(emptyList()) }
    var voos by remember { mutableStateOf<List<Voo>>(emptyList()) }

    var showCreateDialog by remember { mutableStateOf(false) }
    var reservaEmEdicao by remember { mutableStateOf<Reserva?>(null) }

    var passageiroInput by remember { mutableStateOf("") }
    var assentoInput by remember { mutableStateOf("08B") }
    var bagagemInput by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                AppDatabase.garantirDadosIniciais(context, db)
                val listaReservasBanco = buscarReservasDoBanco(reservaDao)
                val listaVoosBanco = vooDao.buscarTodos()
                withContext(Dispatchers.Main) {
                    reservas = listaReservasBanco
                    voos = listaVoosBanco
                }
            } catch (e: Exception) {
                Log.e("TelaListaReservas", "Erro ao carregar reservas no LaunchedEffect: ${e.message}", e)
            }
        }
    }

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
                onClick = {
                    passageiroInput = ""
                    assentoInput = "08B"
                    bagagemInput = false
                    showCreateDialog = true
                },
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
                text = "${reservas.size} reservas persistidas no Room",
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
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    try {
                                                        reservaDao.atualizar(reserva.copy(checkInRealizado = checked))
                                                        val atualizadas = reservaDao.buscarTodos()
                                                        withContext(Dispatchers.Main) {
                                                            reservas = atualizadas
                                                        }
                                                    } catch (e: Exception) {
                                                        Log.e("TelaListaReservas", "Erro ao atualizar check-in: ${e.message}", e)
                                                    }
                                                }
                                            }
                                        )
                                        Text("Realizar Check-in", fontSize = 12.sp, color = TextPrimary)
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = {
                                                passageiroInput = reserva.passageiro
                                                assentoInput = reserva.assento
                                                bagagemInput = reserva.bagagemDespachada
                                                reservaEmEdicao = reserva
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "Editar Reserva", tint = BrandDarkNavy.copy(alpha = 0.7f))
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                        IconButton(
                                            onClick = {
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    try {
                                                        reservaDao.deletar(reserva)
                                                        val atualizadas = reservaDao.buscarTodos()
                                                        withContext(Dispatchers.Main) {
                                                            reservas = atualizadas
                                                        }
                                                    } catch (e: Exception) {
                                                        Log.e("TelaListaReservas", "Erro ao deletar reserva: ${e.message}", e)
                                                    }
                                                }
                                            },
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

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Emitir Reserva Manual") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = passageiroInput, onValueChange = { passageiroInput = it }, label = { Text("Nome do Passageiro") }, singleLine = true)
                    OutlinedTextField(value = assentoInput, onValueChange = { assentoInput = it }, label = { Text("Assento (ex: 15A)") }, singleLine = true)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = bagagemInput, onCheckedChange = { bagagemInput = it })
                        Text("Incluir bagagem despachada")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val vooPadrao = voos.firstOrNull()
                        if (passageiroInput.isNotBlank()) {
                            val precoBase = vooPadrao?.precoBase ?: 850.0
                            val pct = vooPadrao?.cashbackPorcento ?: 10
                            val precoFinal = precoBase + if (bagagemInput) 150.0 else 0.0
                            val novaReserva = Reserva(
                                vooId = vooPadrao?.id ?: 1,
                                passageiro = passageiroInput,
                                assento = assentoInput.ifBlank { "10A" },
                                bagagemDespachada = bagagemInput,
                                assentoVip = false,
                                precoFinal = precoFinal,
                                cashbackGanho = precoFinal * (pct / 100.0),
                                checkInRealizado = false
                            )
                            showCreateDialog = false
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    reservaDao.inserir(novaReserva)
                                    val atualizadas = reservaDao.buscarTodos()
                                    withContext(Dispatchers.Main) {
                                        reservas = atualizadas
                                    }
                                } catch (e: Exception) {
                                    Log.e("TelaListaReservas", "Erro ao inserir reserva: ${e.message}", e)
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) { Text("Cancelar") }
            }
        )
    }

    if (reservaEmEdicao != null) {
        val atual = reservaEmEdicao!!
        AlertDialog(
            onDismissRequest = { reservaEmEdicao = null },
            title = { Text("Editar Reserva #${atual.id}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = passageiroInput, onValueChange = { passageiroInput = it }, label = { Text("Nome do Passageiro") }, singleLine = true)
                    OutlinedTextField(value = assentoInput, onValueChange = { assentoInput = it }, label = { Text("Assento") }, singleLine = true)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = bagagemInput, onCheckedChange = { bagagemInput = it })
                        Text("Bagagem despachada (23kg)")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val reservaEditada = atual.copy(
                            passageiro = passageiroInput.ifBlank { atual.passageiro },
                            assento = assentoInput.ifBlank { atual.assento },
                            bagagemDespachada = bagagemInput
                        )
                        reservaEmEdicao = null
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                reservaDao.atualizar(reservaEditada)
                                val atualizadas = reservaDao.buscarTodos()
                                withContext(Dispatchers.Main) {
                                    reservas = atualizadas
                                }
                            } catch (e: Exception) {
                                Log.e("TelaListaReservas", "Erro ao atualizar reserva: ${e.message}", e)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                ) {
                    Text("Atualizar")
                }
            },
            dismissButton = {
                TextButton(onClick = { reservaEmEdicao = null }) { Text("Cancelar") }
            }
        )
    }
}
