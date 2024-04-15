package manpro.kel5.proyek_manpro

data class Rute(
    var doc_rute: String,
    var id_rute: String,
    var id_stop_source: String,
    var id_stop_dest: String,
    var id_transportasi: String,
    var jarak: Int,
    var durasi: Int,
    var biaya: Int,
    var jam_berangkat: String,
    var jam_sampai: String,
    var nama_source : String,
    var latitude_source: Double,
    var longitude_source : Double,
    var nama_dest : String,
    var latitude_dest: Double,
    var longitude_dest: Double


)
