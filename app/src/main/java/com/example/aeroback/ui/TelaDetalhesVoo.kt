package com.example.aeroback.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.aeroback.viewmodel.VooViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaDetalhesVoo(
    flightId: Int,
    navController: NavController,
    vooViewModel: VooViewModel,
    reservaViewModel: ReservaViewModel
) {
    val uiState by vooViewModel.uiState.collectAsState()

    LaunchedEffect(flightId) {
        vooViewModel.carregarVooPorId(flightId)
    }

    if (uiState.carregandoDetalhes || uiState.idVooCarregado != flightId) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BrandGreen)
        }
        return
    }

    val vooSelecionado = uiState.vooSelecionado
    if (vooSelecionado == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Voo #$flightId n\u00E3o encontrado no banco Room.")
        }
        return
    }

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
                        Text("${vooSelecionado.companhia} (#${vooSelecionado.id})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("+${vooSelecionado.cashbackPorcento}% Cashback", color = BrandGreen, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("${vooSelecionado.origem} \u2794 ${vooSelecionado.destino}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Hor\u00E1rio: ${vooSelecionado.horario} \u2022 Data: ${vooSelecionado.dataIda}", color = Color(0xFF8E9EB5), fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Opcionais da Viagem (C\u00E1lculo em Tempo Real):", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.nomePassageiroDetalhes,
                onValueChange = { vooViewModel.atualizarNomePassageiroDetalhes(it) },
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
                Checkbox(
                    checked = uiState.addBagagemDetalhes,
                    onCheckedChange = { vooViewModel.alternarBagagemDetalhes(it) }
                )
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
                Checkbox(
                    checked = uiState.addAssentoVipDetalhes,
                    onCheckedChange = { vooViewModel.alternarAssentoVipDetalhes(it) }
                )
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
                        Text("R$ ${"%.2f".format(vooSelecionado.precoBase)}", color = TextPrimary, fontSize = 13.sp)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Adicionais:", color = TextSecondary, fontSize = 13.sp)
                        Text("R$ ${"%.2f".format(uiState.valorAdicionaisCalculado)}", color = TextPrimary, fontSize = 13.sp)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFB5E8D2))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Pre\u00E7o Final:", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("R$ ${"%.2f".format(uiState.valorTotalCalculado)}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Cashback creditado:", fontWeight = FontWeight.Bold, color = BrandGreen)
                        Text("+ R$ ${"%.2f".format(uiState.cashbackTotalCalculado)}", fontWeight = FontWeight.Bold, color = BrandGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    reservaViewModel.emitirReservaDoVoo(
                        voo = vooSelecionado,
                        nomePassageiro = uiState.nomePassageiroDetalhes,
                        addBagagem = uiState.addBagagemDetalhes,
                        addAssentoVip = uiState.addAssentoVipDetalhes,
                        onSucesso = {
                            vooViewModel.resetarPersonalizacaoDetalhes()
                            navController.navigate(Rotas.BOOKING_LIST)
                        }
                    )
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
