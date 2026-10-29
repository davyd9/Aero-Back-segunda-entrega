package com.example.aeroback

object Rotas {
    const val SEARCH = "search"
    const val RADAR = "radar"
    const val WALLET = "wallet"
    const val FLIGHT_LIST = "flight_list"
    const val FLIGHT_DETAIL = "flight_detail/{flightId}"
    const val BOOKING_LIST = "booking_list"
    const val BOOKING_DETAIL = "booking_detail/{bookingId}"

    fun flightDetail(flightId: String) = "flight_detail/$flightId"
    fun bookingDetail(bookingId: String) = "booking_detail/$bookingId"
}
