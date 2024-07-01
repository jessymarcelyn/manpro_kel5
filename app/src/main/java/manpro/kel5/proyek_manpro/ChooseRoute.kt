package manpro.kel5.proyek_manpro

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import manpro.kel5.proyek_manpro.profile.User

class ChooseRoute : AppCompatActivity() {
    companion object{
        const val tujuan = "efef"
        const val asal = "fefe"
        const val index = "asas"
        const val filterOpt = "ss"
        const val filterBus = "re"
        const val filterTrain = "w"
        const val arrayStopp = "csasdag"
        const val tanggal = "e"
        const val username = "err"
    }

    val listAsal = mutableListOf<String>()
    val listJamBerangkat = mutableListOf<Int>()
    val listJamSampai = mutableListOf<Int>()
    val listTranspor = mutableListOf<String>()
    val listDurasi = mutableListOf<Int>()
    private lateinit var dataAsal: String
    private lateinit var dataTujuan: String
    private lateinit var indexx: String
    private  var dataFilterOpt: Int = 0
    private  var dataFilterBus: Boolean = false
    private  var dataFilterTrain: Boolean = false
    private var arrayTujuan: ArrayList<String> = ArrayList()
    private lateinit var tanggalDate:String
    private lateinit var db: FirebaseFirestore
    private lateinit var autentikasi : FirebaseAuth
    private lateinit var transpor_first:String
    private var bookmarkName: String = ""



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
        autentikasi = FirebaseAuth.getInstance()
        db = Firebase.firestore
        val dataIntent = intent.getParcelableExtra<Rute>("kirimData")
        dataAsal = intent.getStringExtra(ChooseRoute.asal) ?: ""
        dataTujuan = intent.getStringExtra(ChooseRoute.tujuan) ?: ""
        indexx = intent.getStringExtra(ChooseRoute.index) ?: ""
        arrayTujuan = intent.getStringArrayListExtra(SelectRute.arrayStopp) ?: ArrayList()
        dataFilterOpt = intent.getIntExtra(ChooseRoute.filterOpt, 1)
        dataFilterBus = intent.getBooleanExtra(ChooseRoute.filterBus, true)
        dataFilterTrain = intent.getBooleanExtra(ChooseRoute.filterTrain, true)
        tanggalDate = intent.getStringExtra(ChooseRoute.tanggal) ?: ""
        val username = intent.getStringExtra(ChooseRoute.username) ?: ""

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
        val _tv_tanggal = findViewById<TextView>(R.id.tv_tanggal)
        val _iv_bookmark = findViewById<ImageView>(R.id.iv_bookmark)
        val _iv_unbookmark = findViewById<ImageView>(R.id.iv_unbookmark)
        val _vert1 = findViewById<View>(R.id.vert1)

        _vert1.setBackgroundColor(Color.RED)

        _tv_asal2.text = dataAsal
        _tv_asall2.text = dataAsal
        _tv_tujuan2.text = dataTujuan
        _tv_tujuann2.text = dataTujuan
        _tv_title.text = "Rute " + indexx.toString()
        _tv_tanggal.text = tanggalDate


        var totalBiaya = 0
        Log.d("mimi ", dataIntent.toString())
        Log.d("mimi ", "dataasal ; " +dataAsal.toString())
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
            dataIntent.jam_sampai.forEachIndexed { index, data ->
                if (index != 0) {
                    listJamSampai.add(data)
                }
            }
            dataIntent.id_transportasi.forEachIndexed { index, data ->
                if(index == 0){
                    transpor_first = data
                }
                else if (index != 0) {
                    listTranspor.add(data)
                }
            }
            dataIntent.durasi.forEachIndexed { index, data ->
                if (index != 0) {
                    listDurasi.add(data)
                }
            }
            _rv_choose.layoutManager = LinearLayoutManager(this)
            val adapterP = adapterChooseRoute(listAsal, listJamBerangkat, listJamSampai, listTranspor, transpor_first)
            _rv_choose.adapter = adapterP

            _iv_bookmark.visibility = View.VISIBLE
            _iv_unbookmark.visibility = View.GONE

            if (autentikasi.currentUser != null) {
                val email = autentikasi.currentUser!!.email
                db.collection("User")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (!documents.isEmpty) {
                            val user = documents.first().toObject(User::class.java)
                            val usrname = user.username

                            _iv_bookmark.setOnClickListener {
                                val builder = AlertDialog.Builder(this)
                                builder.setTitle("Enter Bookmark Name")

                                val input = EditText(this)
                                input.inputType = InputType.TYPE_CLASS_TEXT
                                builder.setView(input)

                                builder.setPositiveButton("OK") { dialog, which ->
                                    val bookmarkName = input.text.toString()
                                    if (bookmarkName.isNotEmpty()) {
                                        addBookmark(dataAsal, dataTujuan, usrname, dataIntent.doc_rute, bookmarkName)
                                        _iv_bookmark.visibility = View.GONE
                                        _iv_unbookmark.visibility = View.VISIBLE
                                    } else {
                                        Toast.makeText(this, "Bookmark name cannot be empty", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                builder.setNegativeButton("Cancel") { dialog, which ->
                                    dialog.cancel()
                                }
                                builder.show()
                            }

                            _iv_unbookmark.setOnClickListener {
                                Log.d("BookmarkName", "Bookmark Name: $bookmarkName")
                                deleteBookmark(dataAsal, dataTujuan, usrname, dataIntent.doc_rute)
                                _iv_bookmark.visibility = View.VISIBLE
                                _iv_unbookmark.visibility = View.GONE
                            }

                            Log.d("BookmarkCheck", "Checking bookmark for user: $usrname, dataAsal: $dataAsal, dataTujuan: $dataTujuan, doc_rute: ${dataIntent.doc_rute}")

                            // Check if the bookmark already exists
                            db.collection("Bookmark")
                                .get()
                                .addOnSuccessListener { bookmarkDocuments ->
                                    Log.d("BookmarkCheck", "Bookmark check successful, documents found: ${bookmarkDocuments.size()}")

                                    var bookmarkExists = false
                                    for (document in bookmarkDocuments) {
                                        val idUser = document.getString("id_user")
                                        val idStopSource = document.getString("id_stop_source")
                                        val idStopDest = document.getString("id_stop_dest")
                                        val idRuteList = document.get("id_rute") as? List<String>
                                        val dataIntentRuteList = dataIntent.doc_rute as? List<String>

                                        if (idUser == usrname && idStopSource == dataAsal && idStopDest == dataTujuan) {
                                            if (idRuteList != null && dataIntentRuteList != null && idRuteList.size == dataIntentRuteList.size) {
                                                var listsMatch = true
                                                for (i in idRuteList.indices) {
                                                    if (idRuteList[i] != dataIntentRuteList[i]) {
                                                        listsMatch = false
                                                        break
                                                    }
                                                }
                                                if (listsMatch) {
                                                    bookmarkExists = true
                                                    break
                                                }
                                            }
                                        }
                                    }

                                    if (bookmarkExists) {
                                        Log.d("BookmarkCheck", "Bookmark exists. Setting _iv_bookmark visibility to GONE and _iv_unbookmark visibility to VISIBLE")
                                        _iv_bookmark.visibility = View.GONE
                                        _iv_unbookmark.visibility = View.VISIBLE
                                    } else {
                                        Log.d("BookmarkCheck", "Bookmark does not exist. Setting _iv_bookmark visibility to VISIBLE and _iv_unbookmark visibility to GONE")
                                        _iv_bookmark.visibility = View.VISIBLE
                                        _iv_unbookmark.visibility = View.GONE
                                    }
                                    }
                        } else {
                            Log.w("BookmarkCheck", "No user document found")
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.w("BookmarkCheck", "Error getting user document", e)
                    }
            }
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
                putExtra(SelectRute.tanggal, tanggalDate)
                putExtra(SelectRute.username, username)
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
            intent.putStringArrayListExtra(SelectRute.arrayStopp, arrayTujuan)
            intent.putExtra(ChooseRoute.tanggal, tanggalDate)
            intent.putExtra(ChooseRoute.username, username)
            startActivity(intent)
        }

        _btnChoose.setOnClickListener{
            val intent = Intent(this@ChooseRoute, track_route::class.java)
            intent.putExtra(ChooseRoute.asal, dataAsal)
            intent.putExtra(ChooseRoute.tujuan, dataTujuan)
            intent.putExtra(ChooseRoute.filterOpt, dataFilterOpt)
            intent.putExtra(ChooseRoute.filterBus, dataFilterBus)
            intent.putExtra(ChooseRoute.filterTrain, dataFilterTrain)
            intent.putExtra("kirimData", dataIntent)
            intent.putExtra("index", indexx)
            intent.putStringArrayListExtra(SelectRute.arrayStopp, arrayTujuan)
            intent.putExtra(ChooseRoute.tanggal, tanggalDate)
            intent.putExtra(ChooseRoute.username, username)
            startActivity(intent)
        }

    }
    private fun addBookmark(
        idStopSource: String,
        idStopDest: String,
        idUser : String,
        idRute: List <String>,
        bookmarkName: String
    ) {

        val route = hashMapOf(
            "id_stop_source" to idStopSource,
            "id_stop_dest" to idStopDest,
            "id_user" to idUser,
            "id_rute" to idRute,
            "bookmarkName" to bookmarkName
        )

        db.collection("Bookmark")
            .add(route)
            .addOnSuccessListener { documentReference ->
                Log.d("kuku", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("kuku", "Error adding document", e)
            }
    }

    private fun deleteBookmark(
        idStopSource: String,
        idStopDest: String,
        idUser: String,
        idRute: List<String>,
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Bookmark")
            .whereEqualTo("id_stop_source", idStopSource)
            .whereEqualTo("id_stop_dest", idStopDest)
            .whereEqualTo("id_user", idUser)
            .whereEqualTo("id_rute", idRute)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    for (document in querySnapshot.documents) {
                        db.collection("Bookmark")
                            .document(document.id)
                            .delete()
                            .addOnSuccessListener {
                                Log.d("kuku", "DocumentSnapshot successfully deleted!")
                            }
                            .addOnFailureListener { e ->
                                Log.w("kuku", "Error deleting document", e)
                            }
                    }
                } else {
                    Log.d("kuku", "No matching documents found.")
                }
            }
            .addOnFailureListener { e ->
                Log.w("kuku", "Error searching for bookmark", e)
            }
    }




    // Convert "HHMM" string format to total minutes since midnight
    fun convertToMinutes(time: String): Int {
        val hours = time.substring(0, 2).toInt()
        val minutes = time.substring(2).toInt()
        return hours * 60 + minutes
    }

    fun formatTime(timeInMinutes: Int): String {
        val hours = timeInMinutes / 100
        val minutes = timeInMinutes % 100
        return String.format("%02d:%02d", hours, minutes)
    }

    // Calculate the difference between two times in minutes
    fun calculateTimeDifference(start: Int, end: Int): Int {
        return if (end >= start) {
            end - start
        } else {
            // Handle case when the end time is on the next day
            end + (24 * 60) - start
        }
    }
}