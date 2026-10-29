package com.example.aeroback

import androidx.compose.foundation.background
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
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaDetalhesVoo(
    flightId: String,
    voos: List<Voo>,
    onConfirmarReserva: (Reserva) -> Unit,
    navController: NavController
) {
    val voo = voos.find { it.id == flightId }

    if (voo == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Voo n\u00E3o encontrado.")
        }
        return
    }

    var addBagagem by remember { mutableStateOf(false) }
    var addAssentoVip by remember { mutableStateOf(false) }
    var nomePassageiro by remember { mutableStateOf("Davyd Molina") }

    val valorBagagem = if (addBagagem) 150.0 else 0.0
    val valorAssento = if (addAssentoVip) 80.0 else 0.0
    val valorTotalCalculado = voo.precoBase + valorBagagem + valorAssento
    val cashbackTotalCalculado = valorTotalCalculado * (voo.cashbackPorcento / 100.0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Personaliza\u00E7\u00E3o e Detalhes", fontWeight = FontWeight.Bold) },
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
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BrandDarkNavy)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(voo.companhia, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("+${voo.cashbackPorcento}% Cashback", color = BrandGreen, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("${voo.origem} \u2794 ${voo.destino}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Hor\u00E1rio: ${voo.horario} \u2022 Data: ${voo.dataIda}", color = Color(0xFF8E9EB5), fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Opcionais da Viagem (C\u00E1lculo em Tempo Real):", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = nomePassageiro,
                onValueChange = { nomePassageiro = it },
                label = { Text("Nome do Passageiro") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardBackground, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = addBagagem, onCheckedChange = { addBagagem = it })
                Text("Despachar Bagagem (+R$ 150,00)", fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardBackground, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = addAssentoVip, onCheckedChange = { addAssentoVip = it })
                Text("Assento Espa\u00E7o Extra / VIP (+R$ 80,00)", fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE2F7ED))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Pre\u00E7o Base:", color = TextSecondary, fontSize = 13.sp)
                        Text("R$ ${"%.2f".format(voo.precoBase)}", color = TextPrimary, fontSize = 13.sp)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Adicionais:", color = TextSecondary, fontSize = 13.sp)
                        Text("R$ ${"%.2f".format(valorBagagem + valorAssento)}", color = TextPrimary, fontSize = 13.sp)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFB5E8D2))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Pre\u00E7o Final:", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("R$ ${"%.2f".format(valorTotalCalculado)}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Cashback creditado:", fontWeight = FontWeight.Bold, color = BrandGreen)
                        Text("+ R$ ${"%.2f".format(cashbackTotalCalculado)}", fontWeight = FontWeight.Bold, color = BrandGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val novaReserva = Reserva(
                        id = UUID.randomUUID().toString(),
                        vooId = voo.id,
                        passageiro = nomePassageiro.ifBlank { "Davyd Molina" },
                        assento = if (addAssentoVip) "03A (VIP)" else "14C",
                        bagagemDespachada = addBagagem,
                        assentoVip = addAssentoVip,
                        precoFinal = valorTotalCalculado,
                        cashbackGanho = cashbackTotalCalculado,
                        checkInRealizado = false
                    )
                    onConfirmarReserva(novaReserva)
                    navController.navigate(Rotas.BOOKING_LIST)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
            ) {
                Text("Confirmar Reserva com Cashback", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}