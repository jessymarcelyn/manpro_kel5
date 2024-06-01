package manpro.kel5.proyek_manpro

data class TravelSchedule(
    val tempatAsal: String,
    val tempatTujuan: String,
    val waktuBerangkat: String,
    val waktuSampai: String,
    val waktu: Int,
    val biaya: Int,
    val idTranspor : String
)
