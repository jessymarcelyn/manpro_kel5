package manpro.kel5.proyek_manpro

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class track_route : AppCompatActivity() {
    companion object{
        const val tujuan = "gf"
        const val asal = "df"
        const val index = "asghas"
        const val filterOpt = "ghgh"
        const val filterBus = "rer"
        const val filterTrain = "wqwe"
        const val tanggal = "e"
    }
    private var arStop = arrayListOf<Stop>()
    private var arRute = arrayListOf<Rutee>()
    private lateinit var dataAsal: String
    private lateinit var dataTujuan: String
    private lateinit var indexx: String
    private  var dataFilterOpt: Int = 0
    private  var dataFilterBus: Boolean = false
    private  var dataFilterTrain: Boolean = false
    private var arrayTujuan: ArrayList<String> = ArrayList()
    private lateinit var tanggalDate:String
    val listAsal = mutableListOf<String>()
    val listJamBerangkat = mutableListOf<Int>()
    val listJamSampai = mutableListOf<Int>()
    val listTranspor = mutableListOf<String>()
    val listDurasi = mutableListOf<Int>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_track_route)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = Firebase.firestore
        val dataIntent = intent.getParcelableExtra<Rute>("kirimData")
        val dataIndex = intent.getStringExtra("index") ?: ""

        dataAsal = intent.getStringExtra(ChooseRoute.asal) ?: ""
        dataTujuan = intent.getStringExtra(ChooseRoute.tujuan) ?: ""
        dataFilterOpt = intent.getIntExtra(ChooseRoute.filterOpt, 1)
        dataFilterBus = intent.getBooleanExtra(ChooseRoute.filterBus, true)
        dataFilterTrain = intent.getBooleanExtra(ChooseRoute.filterTrain, true)
        arrayTujuan = intent.getStringArrayListExtra(SelectRute.arrayStopp) ?: ArrayList()
        tanggalDate = intent.getStringExtra(ChooseRoute.tanggal) ?: ""

        val tv_asall2 = findViewById<TextView>(R.id.tv_asall2)
        tv_asall2.text = dataAsal
        val tv_tujuann2 = findViewById<TextView>(R.id.tv_tujuann2)
        tv_tujuann2.text = dataTujuan
        val _tv_jamBerangkatt = findViewById<TextView>(R.id.tv_jamBerangkatt)
        val _tv_jamSampai = findViewById<TextView>(R.id.tv_jamSampai)
        val _tv_buss = findViewById<TextView>(R.id.tv_buss)
        val _rv_track = findViewById<RecyclerView>(R.id.rv_track)
        val _tv_title = findViewById<TextView>(R.id.tv_title)
        _tv_title.text = "Rute " + dataIndex.toString()

        if (dataIntent != null) {
            var jamStr = dataIntent.jam_berangkat.get(0).toString().padStart(4, '0')
            var formattedJam= "${jamStr.substring(0, 2)}:${jamStr.substring(2, 4)}"

            _tv_jamBerangkatt.text = formattedJam

            jamStr = dataIntent.jam_sampai.last().toString().padStart(4, '0')
            formattedJam = "${jamStr.substring(0, 2)}:${jamStr.substring(2, 4)}"
            _tv_jamSampai.text = formattedJam

            _tv_buss.text = dataIntent.id_transportasi.get(0)

            dataIntent.nama_source.forEachIndexed { index, data ->
                if (index != 0) {
                    listAsal.add(data)
                }
            }
            dataIntent.jam_berangkat.forEachIndexed { index, data ->
                if (index != 0) {
                    listJamBerangkat.add(data)
                }
            }
            dataIntent.jam_sampai.forEachIndexed { index, data ->
                if (index != 0) {
                    listJamSampai.add(data)
                }
            }
            dataIntent.id_transportasi.forEachIndexed { index, data ->
                if (index != 0) {
                    listTranspor.add(data)
                }
            }
            dataIntent.durasi.forEachIndexed { index, data ->
                if (index != 0) {
                    listDurasi.add(data)
                }
            }
            _rv_track.layoutManager = LinearLayoutManager(this)
            val adapterP = adapterTrack(listAsal, listJamBerangkat, listJamSampai, listTranspor)
            _rv_track.adapter = adapterP
        }

        val _btn_back_c_rute = findViewById<ImageView>(R.id.btn_back_c_rute)
        _btn_back_c_rute.setOnClickListener{
            val intentWithData = Intent(this@track_route, ChooseRoute::class.java).apply {
                putExtra("kirimData", dataIntent)
                putExtra(ChooseRoute.asal, dataAsal)
                putExtra(ChooseRoute.tujuan, dataTujuan)
                putExtra(ChooseRoute.filterOpt, dataFilterOpt)
                putExtra(ChooseRoute.index, dataIndex);
                putExtra(ChooseRoute.filterBus, dataFilterBus)
                putExtra(ChooseRoute.filterTrain, dataFilterTrain)
                putStringArrayListExtra(SelectRute.arrayStopp, arrayTujuan)
                putExtra(ChooseRoute.tanggal, tanggalDate)

            }
            startActivity(intentWithData)

        }





    }
    fun formatTime(timeInMinutes: Int): String {
        val hours = timeInMinutes / 100
        val minutes = timeInMinutes % 100
        return String.format("%02d:%02d", hours, minutes)
    }
}