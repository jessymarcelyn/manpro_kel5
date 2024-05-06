package manpro.kel5.proyek_manpro

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Rute(
//    var doc_rute: String,
//    var id_rute: String,
//    var id_stop_source: String,
//    var id_stop_dest: String,
//    var id_transportasi: String,
//    var jarak: Int,
//    var durasi: Int,
//    var biaya: Int,
//    var jam_berangkat: String,
//    var jam_sampai: String,
//    var nama_source : String,
//    var latitude_source: Double,
//    var longitude_source : Double,
//    var nama_dest : String,
//    var latitude_dest: Double,
//    var longitude_dest: Double

//    var doc_rute: List<String>,
//    var id_rute: List<String>,
//    var id_stop_source: List<String>,
//    var id_stop_dest: List<String>,
//    var id_transportasi: List<String>,
//    var jarak: List<Int>,
//    var durasi: List<Int>,
//    var biaya: List<Int>,
//    var jam_berangkat: List<String>,
//    var jam_sampai: List<String>,
//    var nama_source : List<String>,
//    var latitude_source: List<Double>,
//    var longitude_source : List<Double>,
//    var nama_dest : List<String>,
//    var latitude_dest: List<Double>,
//    var longitude_dest: List<Double>

    var doc_rute: List<String>,
    var nama_source : List<String>,
    var nama_dest : List<String>,
    var durasi: List<Int>,
    var biaya: List<Int>,
    var jam_berangkat: List<String>,
    var jam_sampai: List<String>,




):Parcelable
