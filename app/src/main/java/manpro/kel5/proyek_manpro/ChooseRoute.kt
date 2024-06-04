package manpro.kel5.proyek_manpro

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
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

class ChooseRoute : AppCompatActivity() {
    companion object{
        const val tujuan = "efef"
        const val asal = "fefe"
        const val index = "asas"
        const val filterOpt = "ss"
        const val filterBus = "re"
        const val filterTrain = "w"
        const val arrayStopp = "csasdag"
    }

    val listAsal = mutableListOf<String>()
    val listJamBerangkat = mutableListOf<String>()
    val listTranspor = mutableListOf<String>()
    val listDurasi = mutableListOf<Int>()
    private lateinit var dataAsal: String
    private lateinit var dataTujuan: String
    private lateinit var indexx: String
    private  var dataFilterOpt: Int = 0
    private  var dataFilterBus: Boolean = false
    private  var dataFilterTrain: Boolean = false
    private var arrayTujuan: ArrayList<String> = ArrayList()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_choose_route)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val db = Firebase.firestore
        val dataIntent = intent.getParcelableExtra<Rute>("kirimData")
        dataAsal = intent.getStringExtra(ChooseRoute.asal) ?: ""
        dataTujuan = intent.getStringExtra(ChooseRoute.tujuan) ?: ""
        indexx = intent.getStringExtra(ChooseRoute.index) ?: ""
        arrayTujuan = intent.getStringArrayListExtra(SelectRute.arrayStopp) ?: ArrayList()
        dataFilterOpt = intent.getIntExtra(ChooseRoute.filterOpt, 1)
        dataFilterBus = intent.getBooleanExtra(ChooseRoute.filterBus, true)
        dataFilterTrain = intent.getBooleanExtra(ChooseRoute.filterTrain, true)

        Log.d("fdfd", "_filterOpt " + dataFilterOpt)
        Log.d("fdfd", "bus " + dataFilterBus)
        Log.d("fdfd", "train " + dataFilterTrain)


        val _tv_title = findViewById<TextView>(R.id.tv_title)
        val _tv_asal2 = findViewById<TextView>(R.id.tv_asal2)
        val _tv_asall2 = findViewById<TextView>(R.id.tv_asall2)
        val _tv_tujuan2 = findViewById<TextView>(R.id.tv_tujuan2)
        val _tv_tujuann2 = findViewById<TextView>(R.id.tv_tujuann2)
        val _tv_hargaa = findViewById<TextView>(R.id.tv_hargaa)
        val _rv_choose = findViewById<RecyclerView>(R.id.rv_choose)
        val _tv_jamBerangkatt = findViewById<TextView>(R.id.tv_jamBerangkatt)
        val _tv_jamSampai = findViewById<TextView>(R.id.tv_jamSampai)
        val _tv_durasi = findViewById<TextView>(R.id.tv_durasi)
        val _btnView = findViewById<Button>(R.id.btnView)
        val _btnChoose = findViewById<Button>(R.id.btnChoose)
        val _tv_buss = findViewById<TextView>(R.id.tv_buss)
        val _tv_brgkt = findViewById<TextView>(R.id.tv_brgkt)
        val _tv_sampai = findViewById<TextView>(R.id.tv_sampai)

        _tv_asal2.text = dataAsal
        _tv_asall2.text = dataAsal
        _tv_tujuan2.text = dataTujuan
        _tv_tujuann2.text = dataTujuan
        _tv_title.text = "Rute " + indexx.toString()

        Log.d("lklk", dataIntent.toString())

        var totalBiaya = 0

        if (dataIntent != null) {
            for (harga in dataIntent.biaya) {
                totalBiaya += harga
            }
            val firstJamBerangkat = dataIntent.jam_berangkat.first()
            val lastJamSampai = dataIntent.jam_sampai.last()
            val formattedFirstJamBerangkat = formatTime(firstJamBerangkat)
            val formattedLastJamSampai = formatTime(lastJamSampai)

            val differenceInMinutes = calculateTimeDifference(firstJamBerangkat, lastJamSampai)
            _tv_brgkt.text = formattedFirstJamBerangkat
            _tv_sampai.text = formattedLastJamSampai
            _tv_durasi.text = differenceInMinutes.toString() + " Menit"
        }

        val formattedBiaya = "Rp. " + String.format("%,d", totalBiaya)
        _tv_hargaa.text = formattedBiaya


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
            _rv_choose.layoutManager = LinearLayoutManager(this)
            val adapterP = adapterChooseRoute(listAsal, listJamBerangkat, listTranspor)
            _rv_choose.adapter = adapterP
        }

        val _btn_back = findViewById<ImageView>(R.id.btn_back_c_rute)
        _btn_back.setOnClickListener {
            val intentWithData = Intent(this@ChooseRoute, SelectRute::class.java).apply {
                putExtra(SelectRute.asal, dataAsal)
                putExtra(SelectRute.tujuan, dataTujuan)
                putExtra(SelectRute.filterOpt, dataFilterOpt)
                putExtra(SelectRute.filterBus, dataFilterBus)
                putExtra(SelectRute.filterTrain, dataFilterTrain)
                putStringArrayListExtra(SelectRute.arrayStopp, arrayTujuan)
            }
            startActivity(intentWithData)
        }

        _btnView.setOnClickListener{
            val intent = Intent(this@ChooseRoute, SelectRuteDetail::class.java)
            intent.putExtra(ChooseRoute.asal, dataAsal)
            intent.putExtra(ChooseRoute.tujuan, dataTujuan)
            intent.putExtra(ChooseRoute.filterOpt, dataFilterOpt)
            intent.putExtra(ChooseRoute.filterBus, dataFilterBus)
            intent.putExtra(ChooseRoute.filterTrain, dataFilterTrain)
            intent.putExtra("kirimData", dataIntent)
            intent.putExtra("index", indexx)
            startActivity(intent)
        }
    }
    // Convert "HHMM" string format to total minutes since midnight
    fun convertToMinutes(time: String): Int {
        val hours = time.substring(0, 2).toInt()
        val minutes = time.substring(2).toInt()
        return hours * 60 + minutes
    }

    // Calculate the difference between two times in minutes
    fun calculateTimeDifference(start: String, end: String): Int {
        val startMinutes = convertToMinutes(start)
        val endMinutes = convertToMinutes(end)
        return if (endMinutes >= startMinutes) {
            endMinutes - startMinutes
        } else {
            // Handle case when the end time is on the next day
            endMinutes + (24 * 60) - startMinutes
        }
    }
    fun formatTime(time: String): String {
        // Insert colon at the correct position
        return time.substring(0, 2) + ":" + time.substring(2, 4)
    }
}