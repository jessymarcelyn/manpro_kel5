package manpro.kel5.proyek_manpro
data class RutePerjalanan(
    var id_rute: String = "",
    val id_stop_source: String = "",
    val id_stop_dest: String = "",
    val id_transportasi: String = "",
    val jarak: Double = 0.0,
    val price: Double = 0.0,
    val estimasi: Int = 0,
    val jam_berangkat: Int = 0,
    val jam_sampai: Int = 0,
    val startDate: Int = 0,
    val finishDate: Int =0
)