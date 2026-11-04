package com.example.aeroback

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.aeroback.data.AeroBackDatabase
import com.example.aeroback.data.repository.ReservaRepository
import com.example.aeroback.data.repository.VooRepository
import com.example.aeroback.ui.CustomBottomNavigation
import com.example.aeroback.ui.Rotas
import com.example.aeroback.ui.TelaBuscaVoos
import com.example.aeroback.ui.TelaCartaoEmbarque
import com.example.aeroback.ui.TelaCarteiraCashback
import com.example.aeroback.ui.TelaDetalhesVoo
import com.example.aeroback.ui.TelaListaReservas
import com.example.aeroback.ui.TelaListaVoos
import com.example.aeroback.ui.TelaRadarPrecos
import com.example.aeroback.viewmodel.ReservaViewModel
import com.example.aeroback.viewmodel.ReservaViewModelFactory
import com.example.aeroback.viewmodel.VooViewModel
import com.example.aeroback.viewmodel.VooViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                AeroBackApp()
            }
        }
    }
}

@Composable
fun AeroBackApp() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val database = remember { AeroBackDatabase.getDatabase(context) }
    val vooRepository = remember { VooRepository(database.vooDao()) }
    val reservaRepository = remember { ReservaRepository(database.reservaDao()) }

    val vooViewModel: VooViewModel = viewModel(
        factory = VooViewModelFactory(vooRepository)
    )
    val reservaViewModel: ReservaViewModel = viewModel(
        factory = ReservaViewModelFactory(reservaRepository, vooRepository)
    )

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Rotas.SEARCH,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Rotas.SEARCH) {
                TelaBuscaVoos(
                    navController = navController,
                    vooViewModel = vooViewModel,
                    reservaViewModel = reservaViewModel
                )
            }
            composable(Rotas.FLIGHT_LIST) {
                TelaListaVoos(
                    navController = navController,
                    vooViewModel = vooViewModel
                )
            }
            composable(
                route = Rotas.FLIGHT_DETAIL,
                arguments = listOf(navArgument("flightId") { type = NavType.IntType })
            ) { backStackEntry ->
                val flightId = backStackEntry.arguments?.getInt("flightId") ?: 0
                TelaDetalhesVoo(
                    flightId = flightId,
                    navController = navController,
                    vooViewModel = vooViewModel,
                    reservaViewModel = reservaViewModel
                )
            }
            composable(Rotas.BOOKING_LIST) {
                TelaListaReservas(
                    navController = navController,
                    reservaViewModel = reservaViewModel
                )
            }
            composable(
                route = Rotas.BOOKING_DETAIL,
                arguments = listOf(navArgument("bookingId") { type = NavType.IntType })
            ) { backStackEntry ->
                val bookingId = backStackEntry.arguments?.getInt("bookingId") ?: 0
                TelaCartaoEmbarque(
                    bookingId = bookingId,
                    navController = navController,
                    reservaViewModel = reservaViewModel
                )
            }
            composable(Rotas.RADAR) {
                TelaRadarPrecos(
                    navController = navController,
                    vooViewModel = vooViewModel
                )
            }
            composable(Rotas.WALLET) {
                TelaCarteiraCashback(
                    navController = navController,
                    reservaViewModel = reservaViewModel
                )
            }
        }
    }
}
