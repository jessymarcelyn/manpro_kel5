package manpro.kel5.proyek_manpro

data class TravelSchedule(
    val docId: String,
    val tempatAsal: String,
    val tempatTujuan: String,
    val waktuBerangkat: Int,
    val waktuSampai: Int,
    val waktu: Int,
    val biaya: Int,
    val idTranspor : String,
    val tanggal : String
)
