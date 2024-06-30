package manpro.kel5.proyek_manpro

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.common.reflect.TypeToken
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import manpro.kel5.proyek_manpro.BottomNav.Companion.setupBottomNavigationView
import manpro.kel5.proyek_manpro.databinding.ActivityHomeBinding
import manpro.kel5.proyek_manpro.profile.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.widget.ArrayAdapter

class Home : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var btnSearch: Button
    private lateinit var btnLogin : Button
    private lateinit var label_bookmark : ListView

    private lateinit var binding: ActivityHomeBinding
    private lateinit var autentikasi : FirebaseAuth

    companion object{
        const val dataAsall = "GETDATA1"
        const val dataTujuann = "GETDATA2"
        const val isAsall = "true"
        const val isAdapter = "asda"
        const val index = "adf"
        const val dataAdap = "asdsd"
        const val tanggal = "mmm"
        const val username = "fsf"
    }

    private var normal: Boolean = true
    var listTambah = mutableListOf<String>()
    val listStop = mutableListOf<String>()
    var AllStop = mutableListOf<String>()
    private var selectedDate : String = ""

    private lateinit var adapterBookmark: AdapterBookmarkHome
    private lateinit var bookmarks: MutableList<BookmarkData>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBottomNavigationView(this)
        FirebaseApp.initializeApp(this)

        val spinner: Spinner = findViewById(R.id.dateDropdown)
        val dates = getNextSevenDays()
        val adapter = ArrayAdapter(this, R.layout.dropdrown_item, dates)
        adapter.setDropDownViewResource(R.layout.dropdrown_item)
        spinner.adapter = adapter

        val tanggalDate = intent.getStringExtra(Home.tanggal) ?: ""
        val initialPosition = dates.indexOf(tanggalDate)
        if (initialPosition != -1) {
            spinner.setSelection(initialPosition)
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedDate = parent?.getItemAtPosition(position).toString()
                // Use the selectedDate string as needed
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle nothing selected if needed
            }
        }

        val sharedPreferences = getSharedPreferences("FilterPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }

        loadListTambah()

        var _tv_asal2 = findViewById<TextView>(R.id.tv_asal2)
        var _tv_tujuan2 = findViewById<TextView>(R.id.tv_tujuan2)

//        val terimaDataAsal  = intent.getStringExtra(Home.dataAsall) ?: "Arief Rahman Hakim 1"
//        val terimaDataTujuan = intent.getStringExtra(Home.dataTujuann) ?: "Arief Rahman Hakim 2"
//        val terimaDataAsal  = intent.getStringExtra(Home.dataAsall) ?: "Graha Famili"
//        val terimaDataTujuan = intent.getStringExtra(Home.dataTujuann) ?: "PTC"
        val terimaDataAsal  = intent.getStringExtra(Home.dataAsall) ?: "Kebon Rojo"
        val terimaDataTujuan = intent.getStringExtra(Home.dataTujuann) ?: "Bubutan 2"
//        val terimaDataAsal  = intent.getStringExtra(Home.dataAsall) ?: "Surabaya Kota"
//        val terimaDataTujuan = intent.getStringExtra(Home.dataTujuann) ?: "Gubeng"
//        val terimaDataAsal  = intent.getStringExtra(Home.dataAsall) ?: "Pakuwon City Mall"
//        val terimaDataTujuan = intent.getStringExtra(Home.dataTujuann) ?: "Galaxy Mall 2"
        Log.d("pipi", "balik")
        Log.d("pipi", "terimaDataAsal" + terimaDataAsal)
        Log.d("pipi", "terimaDataTujuan" + terimaDataTujuan)
        val _isAsall = intent.getBooleanExtra(isAsall, false)
        val username = intent.getStringExtra(Home.username) ?: ""
        Log.d("mxmx", "username Home " + username)
        _tv_asal2.text = terimaDataAsal
        _tv_tujuan2.text = terimaDataTujuan

        val _rv_stop = findViewById<RecyclerView>(R.id.rv_stop)
        _rv_stop.layoutManager = LinearLayoutManager(this)
        val adapterP = AdapterHomeLoc(listTambah)
        _rv_stop.adapter = adapterP

        var _iv_switch = findViewById<ImageView>(R.id.iv_switch)
        var _iv_tambah = findViewById<ImageView>(R.id.iv_tambah)

        _iv_switch.setOnClickListener {
            if (normal) {
                _tv_asal2.text = terimaDataTujuan
                _tv_tujuan2.text = terimaDataAsal
            } else {
                _tv_asal2.text = terimaDataAsal
                _tv_tujuan2.text = terimaDataTujuan
            }

            listTambah.reverse()
            adapterP.notifyDataSetChanged()
            saveListTambah()
            normal = !normal
        }


        val _isAdapter = intent.getBooleanExtra(Home.isAdapter, false)
        val _index = intent.getIntExtra(Home.index, 0)
        val _dataAdap = intent.getStringExtra(Home.dataAdap) ?: "Graha Famili"

        if(_isAdapter){
            listTambah[_index] = _dataAdap
            adapterP.notifyDataSetChanged()
            saveListTambah()
        }

        updateListStop()

        _iv_tambah.setOnClickListener {
            listTambah.add("Tambahkan ekstra stop")
            updateListStop()
            _rv_stop.adapter?.notifyDataSetChanged()
            saveListTambah()
        }
        adapterP.setOnItemClickCallback(object : AdapterHomeLoc.OnItemClickCallback {
            override fun onItemClicked(data: String, index: Int) {
                Log.d("nbnb", "data " + data)
                val intentWithData = Intent(this@Home, SelectLocation::class.java).apply {
                    putExtra(SelectLocation.isAsal, false)
                    putExtra(SelectLocation.asal, _tv_asal2.text)
                    putExtra(SelectLocation.tujuan, _tv_tujuan2.text)
                    putExtra(SelectLocation.isAdapter, true)
                    putExtra(SelectLocation.index, index)
                    putExtra(SelectLocation.adap, data)
                }
                startActivity(intentWithData)
            }

            override fun delData(pos: Int) {
                if (pos in listTambah.indices) {  // Check if position is valid
                    listTambah.removeAt(pos)
                    adapterP.notifyDataSetChanged()
                    saveListTambah()
                }
            }
        })

        btnSearch = findViewById(R.id.btn_search)

        _tv_asal2.setOnClickListener {
            val intentWithData = Intent(this@Home, SelectLocation::class.java).apply {
                putExtra(SelectLocation.isAsal, true)
                putExtra(SelectLocation.asal, _tv_asal2.text)
                putExtra(SelectLocation.tujuan, _tv_tujuan2.text)
                putExtra(SelectLocation.isAdapter, false)
                putExtra(SelectLocation.index, index)
                putExtra(SelectLocation.adap, data)
            }
            startActivity(intentWithData)
        }
        _tv_asal2.setOnClickListener {
            val intentWithData = Intent(this@Home, SelectLocation::class.java).apply {
                putExtra(SelectLocation.isAsal, true)
                putExtra(SelectLocation.asal, _tv_asal2.text)
                putExtra(SelectLocation.tujuan, _tv_tujuan2.text)
                putExtra(SelectLocation.isAdapter, false)
                putExtra(SelectLocation.index, index)
                putExtra(SelectLocation.adap, data)
            }
            startActivity(intentWithData)
        }
        _tv_tujuan2.setOnClickListener {
            val intentWithData = Intent(this@Home, SelectLocation::class.java).apply {
                putExtra(SelectLocation.isAsal, false)
                putExtra(SelectLocation.asal, _tv_asal2.text)
                putExtra(SelectLocation.tujuan, _tv_tujuan2.text)
                putExtra(SelectLocation.isAdapter, false)
                putExtra(SelectLocation.index, index)
                putExtra(SelectLocation.adap, data)
            }
            startActivity(intentWithData)
        }
        _tv_tujuan2.setOnClickListener {
            val intentWithData = Intent(this@Home, SelectLocation::class.java).apply {
                putExtra(SelectLocation.isAsal, false)
                putExtra(SelectLocation.asal, _tv_asal2.text)
                putExtra(SelectLocation.tujuan, _tv_tujuan2.text)
                putExtra(SelectLocation.isAdapter, false)
                putExtra(SelectLocation.index, index)
                putExtra(SelectLocation.adap, data)
            }
            startActivity(intentWithData)
        }

        btnSearch.setOnClickListener {
            val AllStop = mutableListOf<String>()
            AllStop.add(_tv_asal2.text.toString())

            if (listTambah.isNotEmpty()) {
                // Filter elements that are not "Tambahkan ekstra stop" and add them to AllStop
                val filteredListTambah = listTambah.filter { it != "Tambahkan ekstra stop" }
                AllStop.addAll(filteredListTambah)
            }

            AllStop.add(_tv_tujuan2.text as String)

            Log.d("cbcb", ArrayList(AllStop).toString())
            val intentWithData = Intent(this@Home, SelectRute::class.java).apply {
                putStringArrayListExtra(SelectRute.arrayStopp, ArrayList(AllStop))
                putExtra(SelectRute.asal, _tv_asal2.text)
                putExtra(SelectRute.tujuan, _tv_tujuan2.text)
                putExtra(SelectRute.tanggal, selectedDate)
                putExtra(SelectRute.username, username)
            }
            startActivity(intentWithData)
        }

        // BOOKMARK
        autentikasi = FirebaseAuth.getInstance()
        label_bookmark = findViewById(R.id.label_bookmark)
        btnLogin = findViewById(R.id.login_button_home)

        bookmarks = mutableListOf()
        adapterBookmark = AdapterBookmarkHome(this, bookmarks)
        label_bookmark.adapter = adapterBookmark



        if (autentikasi.currentUser != null) {

        }

        if(autentikasi.currentUser != null){
            val email = autentikasi.currentUser!!.email
            db.collection("User")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val user = documents.first().toObject(User::class.java)
                        val username = user.username

                        btnLogin.visibility = View.GONE
                        label_bookmark.visibility = View.VISIBLE
//            fetchBookmarks(username)
                        fetchBookmarks(username)
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("kuku", "Error getting user document", e)
                }

            // Jika currentUser ada --> udah login
        }
        // belum login
        else {
            label_bookmark.visibility = View.GONE
            btnLogin.setOnClickListener{
                intent = Intent(this, Login::class.java)
                startActivity(intent)
            }
        }

        var _viewAll = findViewById<TextView>(R.id.viewAll)

        _viewAll.setOnClickListener {
            val intentWithData = Intent(this@Home, Bookmark::class.java).apply {
                putExtra(SelectRute.username, username)
//                putExtra(Home.username, "admin2")
                putExtra(SelectRute.asal, _tv_asal2.text)
                putExtra(SelectRute.tujuan, _tv_tujuan2.text)
                putExtra(SelectRute.tanggal, selectedDate)
                putExtra(SelectRute.username, username)
            }
            startActivity(intentWithData)
        }


    }

    // Modify the fetchBookmarks function to only add the first two bookmarks to the list
    private fun fetchBookmarks(userId: String) {
        db.collection("Bookmark")
            .whereEqualTo("id_user", userId)
            .get()
            .addOnSuccessListener { documents ->
                bookmarks.clear() // Clear the list to avoid duplicates
                var count = 0
                for (document in documents) {
                    if (count >= 2) break // Exit the loop if two bookmarks have been added
                    val idRuteList = document.get("id_rute") as? List<String>
                    val idStopDest = document.getString("id_stop_dest")
                    val idStopSource = document.getString("id_stop_source")
                    val bookmarkName = document.getString("bookmarkName")

                    // Check if all necessary fields are not null
                    if (idStopSource != null && idStopDest != null && idRuteList != null) {
                        // Create a new BookmarkData object and add it to the list
                        val bookmark = BookmarkData(document.id, idStopSource, idStopDest, userId, idRuteList, bookmarkName)
                        bookmarks.add(bookmark)
                        count++
                    }
                }
                Log.d("fafa", bookmarks.toString())
                adapterBookmark.notifyDataSetChanged() // Notify adapter of changes
            }
            .addOnFailureListener { e ->
                Log.w("FirestoreError", "Error getting documents", e)
            }
    }



//    private fun fetchBookmarks(userId: String) {
//        db.collection("Bookmark")
//            .whereEqualTo("id_user", userId)
//            .get()
//            .addOnSuccessListener { documents ->
//                bookmarks.clear() // Clear the list to avoid duplicates
//                for (document in documents) {
//                    Log.d("uquq", "ada")
//                    Log.d("uquq", "docId : "+ document.id)
//                    val idRuteList = document.get("id_rute") as? List<String>
//                    val idStopDest = document.getString("id_stop_dest")
//                    val idStopSource = document.getString("id_stop_source")
//
//                    // Check if all necessary fields are not null
//                    if (idStopSource != null && idStopDest != null && idRuteList != null) {
//                        // Create a new BookmarkData object and add it to the list
//                        val bookmark = BookmarkData(idStopSource, idStopDest, userId, idRuteList)
//                        bookmarks.add(bookmark)
//                        Log.d("uquq", "bookmarks :  " + bookmarks)
//                    }
//                }
//
//                adapterBookmark.notifyDataSetChanged() // Notify adapter of changes
//            }
//            .addOnFailureListener { e ->
//                Log.w("FirestoreError", "Error getting documents", e)
//            }
//    }

    private fun getNextSevenDays(): List<String> {
        val dates = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

        for (i in 0 until 7) {
            dates.add(dateFormat.format(calendar.time))
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return dates
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            else -> return super.onOptionsItemSelected(item)
        }
    }
    private fun updateListStop() {
        listStop.clear()
        listStop.add(dataAsall)
        listStop.addAll(listTambah)
        listStop.add(dataTujuann)

    }
    private val PREF_NAME = "PrefTambah"
    private val LIST_TAMBAH_KEY = "listTambah"

    // Simpan listTambah ke SharedPreferences
    private fun saveListTambah() {
        val sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(listTambah)
        editor.putString(LIST_TAMBAH_KEY, json)
        editor.apply()
    }

    // Muat listTambah dari SharedPreferences
    private fun loadListTambah() {
        val sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(LIST_TAMBAH_KEY, null)
        val type = object : TypeToken<List<String>>() {}.type
        listTambah.clear()
        if (json != null) {
            val savedListTambah: List<String> = gson.fromJson(json, type)
            listTambah.addAll(savedListTambah)
        }
    }

    //gk jalan
    override fun onDestroy() {
        super.onDestroy()

        // Clear SharedPreferences
        val specificSharedPreferences = getSharedPreferences("PrefTambah", Context.MODE_PRIVATE)
        specificSharedPreferences.edit().clear().apply()
    }

    override fun onResume() {
        super.onResume()
        if(autentikasi.currentUser != null){
            val email = autentikasi.currentUser!!.email
            db.collection("User")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val user = documents.first().toObject(User::class.java)
                        val username = user.username

                        btnLogin.visibility = View.GONE
                        label_bookmark.visibility = View.VISIBLE
//            fetchBookmarks(username)
                        fetchBookmarks(username)
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("kuku", "Error getting user document", e)
                }
        }
        val userId = "your_user_id"

        val _rv_stop = findViewById<RecyclerView>(R.id.rv_stop)

        // Filter elements in listTambah and remove "Tambahkan ekstra stop"
        val iterator = listTambah.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item == "Tambahkan ekstra stop") {
                iterator.remove()
                updateListStop()
                _rv_stop.adapter?.notifyDataSetChanged()
                saveListTambah()
            }
        }
    }
}




