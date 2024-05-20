package manpro.kel5.proyek_manpro

data class Stop(
    var doc_id : String = "",
    var id_stop : String,
    var nama : String,
    var latitude: Double,
    var longitude : Double,
    var jenis: String
)
