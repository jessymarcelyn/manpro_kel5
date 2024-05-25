package manpro.kel5.proyek_manpro

data class TravelSchedule(
    val id: String = "",
    val tempatAsal: String,
    val tempatTujuan: String,
    val waktuBerangkat: Long,
    val waktu: Int = 0,
    val biaya: Double = 0.0
)
