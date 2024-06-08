package manpro.kel5.proyek_manpro

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.common.reflect.TypeToken
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import manpro.kel5.proyek_manpro.BottomNav.Companion.setupBottomNavigationView
import manpro.kel5.proyek_manpro.databinding.ActivityHomeBinding
import manpro.kel5.proyek_manpro.profile.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import android.widget.ArrayAdapter
import java.util.TimeZone

class Home : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var btnSearch: Button
    private lateinit var btnLogin : Button
    private lateinit var label_bookmark : ListView
    private lateinit var tv_tanggal : TextView
    private lateinit var iv_tanggal : ImageView
    private lateinit var binding: ActivityHomeBinding
    private lateinit var autentikasi : FirebaseAuth

    companion object{
        const val dataAsall = "GETDATA1"
        const val dataTujuann = "GETDATA2"
        const val isAsall = "true"
        const val isAdapter = "asda"
        const val index = "adf"
        const val dataAdap = "asdsd"
    }

    private var normal: Boolean = true
    var listTambah = mutableListOf<String>()
    val listStop = mutableListOf<String>()
    var AllStop = mutableListOf<String>()
    private var selectedDate : String = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBottomNavigationView(this)
        FirebaseApp.initializeApp(this)

        val spinner: Spinner = findViewById(R.id.dateDropdown)
        val dates = getNextSevenDays()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dates)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter


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

        val tvTanggal: TextView = findViewById(R.id.tv_tanggal)
        val ivTanggal: ImageView = findViewById(R.id.imageView6)
        val calendarClickListener = View.OnClickListener {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"))
            val tahun = calendar.get(Calendar.YEAR)
            val bulan = calendar.get(Calendar.MONTH)
            val tanggal = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val selectedYear = year
                val selectedMonth = month
                val selectedDay = dayOfMonth

                val monthFormat = SimpleDateFormat("MMMM", Locale("in", "ID"))
                val monthName = monthFormat.format(Calendar.getInstance().apply {
                    set(Calendar.MONTH, selectedMonth)
                }.time)

                tvTanggal.text = "$selectedDay $monthName $selectedYear"
            }, tahun, bulan, tanggal)

            datePickerDialog.show()
        }
        tvTanggal.setOnClickListener(calendarClickListener)
        ivTanggal.setOnClickListener(calendarClickListener)
        val defaultCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"))
        val defaultYear = defaultCalendar.get(Calendar.YEAR)
        val defaultMonth = SimpleDateFormat("MMMM", Locale("in", "ID")).format(defaultCalendar.time)
        val defaultDay = defaultCalendar.get(Calendar.DAY_OF_MONTH)
        tvTanggal.text = "$defaultDay $defaultMonth $defaultYear"

        loadListTambah()

        var _tv_asal2 = findViewById<TextView>(R.id.tv_asal2)

        var _tv_tujuan2 = findViewById<TextView>(R.id.tv_tujuan2)

//        val terimaDataAsal  = intent.getStringExtra(Home.dataAsall) ?: "Arief Rahman Hakim 1"
//        val terimaDataTujuan = intent.getStringExtra(Home.dataTujuann) ?: "Arief Rahman Hakim 2"
//        val terimaDataAsal  = intent.getStringExtra(Home.dataAsall) ?: "Graha Famili"
//        val terimaDataTujuan = intent.getStringExtra(Home.dataTujuann) ?: "PTC"
        val terimaDataAsal  = intent.getStringExtra(Home.dataAsall) ?: "1Test A"
        val terimaDataTujuan = intent.getStringExtra(Home.dataTujuann) ?: "1Test D"
//        val terimaDataAsal  = intent.getStringExtra(Home.dataAsall) ?: "Pakuwon City Mall"
//        val terimaDataTujuan = intent.getStringExtra(Home.dataTujuann) ?: "Galaxy Mall 2"
        Log.d("pipi", "balik")
        Log.d("pipi", "terimaDataAsal" + terimaDataAsal)
        Log.d("pipi", "terimaDataTujuan" + terimaDataTujuan)
        val _isAsall = intent.getBooleanExtra(isAsall, false)

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
            }
            startActivity(intentWithData)
        }

        // BOOKMARK
        autentikasi = FirebaseAuth.getInstance()
        label_bookmark = findViewById(R.id.label_bookmark)
        btnLogin = findViewById(R.id.login_button_home)
        if(autentikasi.currentUser != null){
            // Jika currentUser ada --> udah login
            btnLogin.visibility = View.GONE
            label_bookmark.visibility = View.VISIBLE
        }
        // belum login
        else {
            label_bookmark.visibility = View.GONE
            btnLogin.setOnClickListener{
                intent = Intent(this, Login::class.java)
                startActivity(intent)
            }
        }

    }

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



