package manpro.kel5.proyek_manpro

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class SelectRute : AppCompatActivity() {
    companion object{
        const val isAsal = "true"
        const val tujuan = "ccc"
        const val tanggal = "see"
        const val asal = "ddd"
        const val filterOpt = "sSs"
        const val filterBus = "rEe"
        const val filterTrain = "Ww"
        const val arrayStopp = "csfg"
        const val username = "fdgdg"
    }
    private  var _isAsal: Boolean = false
    private lateinit var dataAsal: String
    private lateinit var dataTujuan: String
    private  var newRoute: List<String> = emptyList()
    private val validDiscoveredRoutes = mutableListOf<List<String>>()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var tv_jalan: TextView
    private var arRute = arrayListOf<Rute>()
    private lateinit var _rvRute: RecyclerView
    private  var _filterOpt: Int = 1
    var ruteText = StringBuilder()
    private var bus : Boolean = true
    private var train : Boolean = true
    private var isRuteBaruValid : Boolean = false
    private var arrayTujuan: ArrayList<String> = ArrayList()
    private lateinit var tanggalDate: String
    private var ruteBaruStartDateParsed : String =""
    private var ruteBaruFinishDateParsed : String = ""
    private lateinit var _tv_kosong1: TextView
    private lateinit var _tv_kosong2: TextView
    private lateinit var _iv_kosong: ImageView
    private var isFetchRutesCalled = false
    private lateinit var tanggalDateParsed : Date



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selectrute)

        _isAsal = intent.getBooleanExtra(isAsal, false)
        dataAsal = intent.getStringExtra(asal) ?: ""
        dataTujuan = intent.getStringExtra(tujuan) ?: ""
        _filterOpt = intent.getIntExtra(SelectRute.filterOpt, 1)
        bus = intent.getBooleanExtra(SelectRute.filterBus, true)
        train = intent.getBooleanExtra(SelectRute.filterTrain, true)
        tanggalDate = intent.getStringExtra(SelectRute.tanggal) ?: ""
//        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
//        tanggalDate = dateFormat.parse(dateString) ?: Date()


//        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
//        tanggalDate = dateFormat.parse(tanggal)!!

        val username = intent.getStringExtra(SelectRute.username) ?: ""

        Log.d("mxmx", "username SelctRute " + username)

        Log.d("sfsf", "tanggal SelectRute: " + tanggalDate)
        Log.d("egh", "dataAsal : " + dataAsal)
        Log.d("egh", "dataTujuan : " + dataTujuan)
        Log.d("rre", "_filterOpt " + _filterOpt)
        Log.d("rre", "bus " + bus)
        Log.d("rre", "train " + train)

        val _btn_back = findViewById<ImageView>(R.id.btn_back_c_rute)
        _btn_back.setOnClickListener {
            val intentWithData = Intent(this@SelectRute, Home::class.java).apply {
                putExtra(Home.dataAsall, dataAsal)
                putExtra(Home.dataTujuann, dataTujuan)
                putExtra(Home.isAsall, false)
                putExtra(Home.tanggal, tanggalDate)
                putExtra(Home.username, username)
            }
            startActivity(intentWithData)
        }
        arrayTujuan = intent.getStringArrayListExtra(SelectRute.arrayStopp) ?: ArrayList()
        Log.d("fkfk", arrayTujuan.toString())


        val btnFilter = findViewById<ImageButton>(R.id.btnFilter1)

        btnFilter.setOnClickListener{
            showFilterDialog()
        }

        Log.d("eueu", "dataAsal " + dataAsal)
        Log.d("eueu", "dataTujuan " +  dataTujuan)


        _tv_kosong1 = findViewById<TextView>(R.id.tv_kosong1)
        _tv_kosong2 = findViewById<TextView>(R.id.tv_kosong2)
        _iv_kosong = findViewById<ImageView>(R.id.iv_kosong)

        _tv_kosong1.visibility = View.INVISIBLE
        _tv_kosong2.visibility = View.INVISIBLE
        _iv_kosong.visibility = View.INVISIBLE

        ruteText = StringBuilder()
        trackRoute(dataAsal, dataTujuan, ruteText)

        _rvRute = findViewById(R.id.rvRute)
        _rvRute.layoutManager = LinearLayoutManager(this)
        val adapterP = adapterRoute(arRute)
        _rvRute.adapter = adapterP

        adapterP.setOnItemClickCallback(object : adapterRoute.OnItemClickCallback{
            //            Log.d("toast", "dependee")
            override fun onItemClicked(data: Rute) {
//                Toast.makeText(this@fClass, data.name, Toast.LENGTH_LONG). show()
                Log.d("toast", "kepencet")
//                Toast.makeText(requireContext(),"HALO", Toast.LENGTH_LONG).show()

            }

            override fun delData(pos: Int) {
                TODO("Not yet implemented")
            }

            override fun gotoDetail(data: Rute, pos: Int) {
                Log.d("opo", pos.toString())
//                val intent = Intent(this@SelectRute, Map::class.java)
//                val intent = Intent(this@SelectRute, SelectRuteDetail::class.java)
//                intent.putExtra("kirimData", data)S
                val intent = Intent(this@SelectRute, ChooseRoute::class.java)
                Log.d("uyuy", data.toString())
                intent.putExtra("kirimData", data)
                intent.putExtra(ChooseRoute.asal, dataAsal)
                intent.putExtra(ChooseRoute.tujuan, dataTujuan)
                intent.putExtra(ChooseRoute.index, pos.toString());
                intent.putExtra(ChooseRoute.filterOpt, _filterOpt)
                intent.putExtra(ChooseRoute.filterBus, bus)
                intent.putExtra(ChooseRoute.filterTrain, train)
                intent.putStringArrayListExtra(SelectRute.arrayStopp, arrayTujuan)
                intent.putExtra(ChooseRoute.tanggal, tanggalDate)
                intent.putExtra(ChooseRoute.username, username)
                startActivity(intent)
            }
        })
        adapterP.notifyDataSetChanged()
    }
    private fun showFilterDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.filter_menu)

        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)

        val _btnSort = dialog.findViewById<Button>(R.id.btnSort)
        val _btnRadio1 = dialog.findViewById<RadioButton>(R.id.btnRadio1)
        val _btnRadio2 = dialog.findViewById<RadioButton>(R.id.btnRadio2)
        val _btnRadio3 = dialog.findViewById<RadioButton>(R.id.btnRadio3)
        val _radioGroupFilter = dialog.findViewById<RadioGroup>(R.id.radioGroupFilter)

        val _idSwitch = dialog.findViewById<Switch>(R.id.idSwitch)
        val _idSwitch2 = dialog.findViewById<Switch>(R.id.idSwitch2)

        // Retrieve the last saved state from SharedPreferences
        val sharedPreferences = getSharedPreferences("FilterPrefs", Context.MODE_PRIVATE)
        val savedFilterOpt = sharedPreferences.getInt("filterOpt", 1)
        val savedBus = sharedPreferences.getBoolean("bus", true)
        val savedTrain = sharedPreferences.getBoolean("train", true)

        // Restore the switches' states
        _idSwitch.isChecked = savedBus
        _idSwitch2.isChecked = savedTrain

        var selectedRadioButtonId = -1
        when (savedFilterOpt) {
            1 -> {
                _btnRadio1.isChecked = true
                selectedRadioButtonId = _btnRadio1.id
            }
            2 -> {
                _btnRadio2.isChecked = true
                selectedRadioButtonId = _btnRadio2.id
            }
            3 -> {
                _btnRadio3.isChecked = true
                selectedRadioButtonId = _btnRadio3.id
            }
        }

        _radioGroupFilter.setOnCheckedChangeListener { group, checkedId ->
            selectedRadioButtonId = checkedId
        }

        _btnSort.setOnClickListener {
            bus = _idSwitch.isChecked
            train = _idSwitch2.isChecked

            Log.d("nbnb", "bus : $bus")
            Log.d("nbnb", "train : $train")

            if (selectedRadioButtonId != -1) {
                Log.d("nbnb", "masuk1")
                val selectedRadioButton = dialog.findViewById<RadioButton>(selectedRadioButtonId)
                if (selectedRadioButton != null) {
                    val filterText = selectedRadioButton.text.toString()
                    Log.d("nbnb", "masuk3")
                    when (selectedRadioButtonId) {
                        _btnRadio1.id -> {
                            _filterOpt = 1
//                            Toast.makeText(this, "Filter applied1: $filterText", Toast.LENGTH_SHORT).show()
                        }
                        _btnRadio2.id -> {
                            _filterOpt = 2
//                            Toast.makeText(this, "Filter applied2: $filterText", Toast.LENGTH_SHORT).show()
                        }
                        _btnRadio3.id -> {
                            _filterOpt = 3
//                            Toast.makeText(this, "Filter applied3: $filterText", Toast.LENGTH_SHORT).show()
                        }
                    }

                    Log.d("nbnb", "bus2 : $bus")
                    Log.d("nbnb", "train2 : $train")
                    // Save the current state to SharedPreferences
                    with(sharedPreferences.edit()) {
                        Log.d("nbnb", "masuk2")
                        putInt("filterOpt", _filterOpt)
                        putBoolean("bus", bus)
                        putBoolean("train", train)
                        apply()
                    }
                } else {
                    Log.d("FilterDialog", "Selected radio button is null")
                }
            }

            fetchRutes()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun trackRoute(
        tempatAwal: String,
        tempatTujuan: String,
        ruteText: StringBuilder,
        currentRoute: List<String> = listOf(),
        discoveredRoutes: MutableSet<List<String>> = mutableSetOf(),
        prevRouteDocId: String? = null,
        depth: Int = 0,
        maxDepth: Int = 5 // Set a reasonable maximum depth
    ) {
        Log.d("hbhb", "masuk0")
        Log.d("hbhb", "depth : " + depth)
        Log.d("hbhb", "maxDepth : " + maxDepth)
        if (depth > maxDepth) {
            Log.d("hbhb", "masuk1")
            // Prevents excessive recursion
            if (validDiscoveredRoutes.isEmpty()) {
                updateVisibility(false)
            }
            return
        }

        arRute.clear()

        db.collection("RuteBaru")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val idStopSource = document.getString("id_stop_source")
                    val idStopDest = document.getString("id_stop_dest")
                    val departureTimeSource = document.getLong("jam_berangkat")?.toInt() ?: 0
                    val arrivalTimeDest = document.getLong("jam_sampai")?.toInt() ?: 0
                    val currentRouteDocId = document.id

                    val ruteBaruStartDate = document.get("startDate")
                    val ruteBaruFinishDate = document.get("finishDate")

                    val ruteBaruStartDateInt = if (ruteBaruStartDate is Number) {
                        ruteBaruStartDate.toInt()
                    } else {
                        0
                    }

                    val ruteBaruFinishDateInt = if (ruteBaruFinishDate is Number) {
                        ruteBaruFinishDate.toInt()
                    } else {
                        0
                    }

                    val newRoute = currentRoute + currentRouteDocId
                    if (idStopSource == tempatAwal && idStopDest != null) {
                        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
                        tanggalDateParsed = dateFormat.parse(tanggalDate)

                        val ruteBaruStartDateParsed = formatDate(ruteBaruStartDateInt)
                        val ruteBaruFinishDateParsed = if (ruteBaruFinishDateInt != 0) {
                            formatDate(ruteBaruFinishDateInt)
                        } else {
                            ""
                        }

                        val ruteBaruStartDateDate = dateFormat.parse(ruteBaruStartDateParsed)
                        val ruteBaruFinishDateDate = if (ruteBaruFinishDateParsed != "") {
                            dateFormat.parse(ruteBaruFinishDateParsed)
                        } else {
                            null
                        }

                        Log.d("pepe", "documentId : " + document.id)
                        if (idStopDest == tempatTujuan) {
                            Log.d("pepe", "masuk1")
                            Log.d("pepe", "tanggalDateParsed : " + tanggalDateParsed)
                            Log.d("pepe", "ruteBaruStartDateDate : " + ruteBaruStartDateDate)
                            Log.d("pepe", "ruteBaruFinishDateDate : " + ruteBaruStartDateDate)
                            if (ruteBaruFinishDateDate == null) {
                                if (tanggalDateParsed.after(ruteBaruStartDateDate)) {
                                    val connectionValidity =
                                        isValidRoute(newRoute, documents, tanggalDateParsed)
                                    if (connectionValidity.all { it }) {
                                        discoveredRoutes.add(newRoute)
                                    }
                                }
                            } else {
                                if (tanggalDateParsed.after(ruteBaruStartDateDate) &&
                                    tanggalDateParsed.before(ruteBaruFinishDateDate)
                                ) {
                                    val connectionValidity =
                                        isValidRoute(newRoute, documents, tanggalDateParsed)
                                    if (connectionValidity.all { it }) {
                                        discoveredRoutes.add(newRoute)
                                    }
                                }
                            }

                        } else if (idStopDest !in currentRoute) {
                            Log.d("pepe", "masuk2")
                            Log.d("pepe", "tanggalDateParsed : " + tanggalDateParsed)
                            Log.d("pepe", "ruteBaruStartDateDate : " + ruteBaruStartDateDate)
                            Log.d("pepe", "ruteBaruFinishDateDate : " + ruteBaruStartDateDate)
                            if (tanggalDateParsed != null && ruteBaruStartDateDate != null) {
                                if (ruteBaruFinishDateDate != null) {
                                    if (tanggalDateParsed.after(ruteBaruStartDateDate) &&
                                        tanggalDateParsed.before(ruteBaruFinishDateDate)
                                    ) {
                                        val newDiscoveredRoutesLocal = mutableSetOf<List<String>>()
                                        trackRoute(
                                            idStopDest,
                                            tempatTujuan,
                                            ruteText,
                                            newRoute,
                                            newDiscoveredRoutesLocal,
                                            currentRouteDocId,
                                            depth + 1, // Increase depth
                                            maxDepth
                                        )
                                        discoveredRoutes.addAll(newDiscoveredRoutesLocal)
                                    }
                                } else {
                                    if (tanggalDateParsed.after(ruteBaruStartDateDate)) {
                                        val newDiscoveredRoutesLocal = mutableSetOf<List<String>>()
                                        trackRoute(
                                            idStopDest,
                                            tempatTujuan,
                                            ruteText,
                                            newRoute,
                                            newDiscoveredRoutesLocal,
                                            currentRouteDocId,
                                            depth + 1, // Increase depth
                                            maxDepth
                                        )
                                        discoveredRoutes.addAll(newDiscoveredRoutesLocal)
                                    }
                                }
                            }
                        }
                    }
                }
                if (discoveredRoutes.isNotEmpty()) {
                    for (i in discoveredRoutes.indices) {
                        val route = discoveredRoutes.elementAt(i)
                        val connectionValidity = isValidRoute(route, documents, tanggalDateParsed)
                        if (connectionValidity.all { it } && isRouteInOrder(
                                route,
                                arrayTujuan,
                                documents
                            )) {
                            validDiscoveredRoutes.add(route)
                        }
                    }
                    Log.d("trtr", validDiscoveredRoutes.toString())

                }
                fetchRutes()

            }
            .addOnFailureListener {
                Log.e("FirebaseError", "Error getting documents", it)
            }
    }


    fun padDateString(dateString: String): String {
        return if (dateString.length == 7) {
            "0$dateString"
        } else {
            dateString
        }
    }

    fun parseDate(dateString: String): Date? {
        val paddedDateString = padDateString(dateString)
        val dateFormat = SimpleDateFormat("ddMMyyyy", Locale("id", "ID"))
        return try {
            dateFormat.parse(paddedDateString)
        } catch (e: Exception) {
            null
        }
    }

    fun formatDate(dateInt: Int): String {
        val dateString = dateInt.toString()
        val date = parseDate(dateString)
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        return date?.let { outputFormat.format(it) } ?: "Invalid date"
    }



    private fun isValidRoute(
        currentRoute: List<String>,
        documents: QuerySnapshot,
        tanggalDateParsed: Date
    ): List<Boolean> {
        val connectionValidity = mutableListOf<Boolean>()

        if (currentRoute.isEmpty()) return connectionValidity

        // Get the current date
        val currentDate = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        // Check if tanggalDateParsed is the same as the current date
        if (tanggalDateParsed == currentDate) {
            // Get the current time in hours and minutes
            val currentTime = Calendar.getInstance()
            val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
            val currentMinute = currentTime.get(Calendar.MINUTE)
            val currentTimeInMinutes = currentHour * 60 + currentMinute

            // Check the first departure time
            val firstRouteDocId = currentRoute.first()
            val firstDepartureTime = documents.firstOrNull { it.id == firstRouteDocId }
                ?.getLong("jam_berangkat")?.toInt() ?: 0

            if (firstDepartureTime <= currentTimeInMinutes) {
                // If the first departure time is not greater than the current time, mark the entire route as invalid
                return currentRoute.map { false }
            }
        }

        for (i in 0 until currentRoute.size - 1) {
            val prevRouteDocId = currentRoute[i]
            val nextRouteDocId = currentRoute[i + 1]

            var isValid = true
            for (document in documents) {
                if (document.id == prevRouteDocId) {
                    val prevArrivalTime = document.getLong("jam_sampai")?.toInt() ?: 0
                    if (prevArrivalTime != null) {
                        for (nextDocument in documents) {
                            if (nextDocument.id == nextRouteDocId) {
                                val nextDepartureTime = nextDocument.getLong("jam_berangkat")?.toInt() ?: 0
                                if (nextDepartureTime != null && prevArrivalTime > nextDepartureTime) {
                                    isValid = false
                                    break
                                }
                                break
                            }
                        }
                    }
                    break
                }
            }
            connectionValidity.add(isValid)
        }
        return connectionValidity
    }

    fun fetchRutes() {
        Log.d("hbhb", "masuk2")
        Log.d("mvmv", validDiscoveredRoutes.toString())
        arRute.clear()
        Log.d("emem", "masuk4")
        Log.d("egh", "masuk2")
        Log.d("jjj", validDiscoveredRoutes.toString())

//        if (validDiscoveredRoutes.isEmpty()) {
//            updateVisibility(false)
//            return
//        }

        val totalRoutes = validDiscoveredRoutes.size
        var counter = 0
        var processCounter = 0

        fun processRoute(innerList: List<String>) {
            val listId = mutableListOf<String>()
            val listAsal = mutableListOf<String>()
            val listTujuan = mutableListOf<String>()
            val listHarga = mutableListOf<Int>()
            val listDurasi = mutableListOf<Int>()
            val listJamBerangkat = mutableListOf<Int>()
            val listJamSampai = mutableListOf<Int>()
            val listTranspor = mutableListOf<String>()

            var counterInnerList = 0

            for (ruteId in innerList) {
                Log.d("mhmh", "ruteID " + ruteId)
                db.collection("RuteBaru")
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val docId = document.id
                            if (docId == ruteId) {
                                val namaAsal = document.getString("id_stop_source")
                                val namaTujuan = document.getString("id_stop_dest")
                                val jamBerangkat = document.getLong("jam_berangkat")?.toInt() ?: 0
                                val jamSampai = document.getLong("jam_sampai")?.toInt() ?: 0
                                val id_transportasi = document.getString("id_transportasi")
                                val durasi = document.getLong("estimasi")?.toInt() ?: 0
                                val biaya = document.getLong("price")?.toInt() ?: 0
                                Log.d("mhmh", "masuk1 ")
                                Log.d("mhmh", "namaAsal " + namaAsal)
                                Log.d("mhmh", "namaTujuan " + namaTujuan)

                                if (namaAsal != null && namaTujuan != null && jamBerangkat != null && jamSampai != null &&id_transportasi != null) {
                                    Log.d("mhmh", "masuk2 ")
                                    listId.add(docId)
                                    listAsal.add(namaAsal)
                                    listTujuan.add(namaTujuan)
                                    listHarga.add(biaya)
                                    listDurasi.add(durasi)
                                    listJamBerangkat.add(jamBerangkat)
                                    listJamSampai.add(jamSampai)
                                    listTranspor.add(id_transportasi)

                                    counterInnerList++

                                    // Semua inner rute sudah berhasil
                                    if (counterInnerList == innerList.size) {

                                        //yang duplikat dihapus
                                        val isDuplicate = arRute.any { existingRoute ->
                                            existingRoute.doc_rute.toSet() == listId.toSet()
                                        }

                                        if (!isDuplicate) {
                                            arRute.add(
                                                Rute(
                                                    listId,
                                                    listAsal,
                                                    listTujuan,
                                                    listDurasi,
                                                    listHarga,
                                                    listJamBerangkat,
                                                    listJamSampai,
                                                    listTranspor
                                                )
                                            )
                                        }

//                                        val allListIds = mutableListOf<String>()
//                                        for (route in arRute) {
//                                            allListIds.addAll(route.doc_rute)
//                                        }
//
//                                        Log.d("nnn", "All List IDs: $allListIds")
                                        counter++

                                        if (counter == totalRoutes) {
                                            // Semua rute sudah berhasil
                                            Log.d("emem", "masuk5")
                                            Log.d("mjmj", arRute.toString())
//                                            _rvRute.adapter?.notifyDataSetChanged()
                                            val filteredRoutes: MutableList<Rute> = mutableListOf()

                                            if((bus && train) || (!bus && !train)){

                                            }
                                            else if (bus) {
                                                Log.d("mnmn", "masuk1")
                                                filteredRoutes.addAll(arRute.filter { route ->
                                                    route.id_transportasi.all { it.startsWith("B") }
                                                })
                                                arRute.clear()
                                                arRute.addAll(filteredRoutes)
                                            } else if (train) {
                                                Log.d("mnmn", "masuk2")
                                                filteredRoutes.addAll(arRute.filter { route ->
                                                    route.id_transportasi.all { it.startsWith("T") }
                                                })
                                                arRute.clear()
                                                arRute.addAll(filteredRoutes)
                                            }

                                                Log.d("mmm",arRute.toString())
                                                sortRutes(arRute)
                                                _rvRute.adapter?.notifyDataSetChanged()
                                        }
                                    }
                                }
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Handle failure
                    }.addOnCompleteListener {
//                        processCounter++
//                        if (processCounter == totalRoutes) {
//                            updateVisibility(arRute.isNotEmpty())
//                        }
                    }
            }
        }


        // Process each route
            for (innerList in validDiscoveredRoutes) {
                Log.d("lll", validDiscoveredRoutes.toString())
                Log.d("iii", innerList.toString())
                processRoute(innerList)
            }

    }


    private fun updateVisibility(hasRoutes: Boolean) {
        if (hasRoutes) {
            _tv_kosong1.visibility = View.GONE
            _tv_kosong2.visibility = View.GONE
            _iv_kosong.visibility = View.GONE
        } else {
            _tv_kosong1.visibility = View.VISIBLE
            _tv_kosong2.visibility = View.VISIBLE
            _iv_kosong.visibility = View.VISIBLE
        }
    }


    fun sortRutes(arRute: ArrayList<Rute>) {
        Log.d("emem", "masuk6")
        Log.d("egh", "masuk3")
        if (_filterOpt == 1) {
            Log.d("egh", "masuk4")
            val timeFormat = SimpleDateFormat("HHmm", Locale.getDefault())
            arRute.sortBy { rute ->
                // rute waktu sampai tercepat
                Log.d("wdwd", rute.jam_sampai.lastOrNull().toString())
                rute.jam_sampai.lastOrNull()?.let { jamSampai ->
                    jamSampai
                } ?: Int.MAX_VALUE// Default to a large value if listJamSampai is empty
            }
            Log.d("sasa", arRute.toString())
            _rvRute.adapter?.notifyDataSetChanged()
        }

        else if (_filterOpt == 2) {
            val sortedRute = arRute.sortedBy { rute ->
                rute.biaya.sum()
            }

            arRute.clear()
            arRute.addAll(sortedRute)

            sortedRute.forEach { rute ->
                Log.d("efef", "Total Biaya: ${rute.biaya.sum()}")
            }
            Log.d("sasa", arRute.toString())
            _rvRute.adapter?.notifyDataSetChanged()
        } else if(_filterOpt == 3){
            // rute minim transfer
            val sortedRute = arRute.sortedBy { rute ->
                rute.id_transportasi.distinct().size
            }
            arRute.clear()
            arRute.addAll(sortedRute)
            Log.d("sasa", arRute.toString())
            _rvRute.adapter?.notifyDataSetChanged()
        }

    }
    private fun isRouteInOrder(route: List<String>, arrayTujuan: ArrayList<String>, documents: QuerySnapshot): Boolean {
        val stopPoints = route.mapNotNull { routeId ->
            documents.firstOrNull { it.id == routeId }?.getString("id_stop_dest")
        }
        Log.d("eaea", stopPoints.toString())

        var counter = 0
        for (stop in stopPoints) {
            for (i in 1 until arrayTujuan.size) {
                Log.d("agag", "stop : "+ stop)
                Log.d("agag", "arrayTujuan : "+ arrayTujuan[i])
                if (stop == arrayTujuan[i]) {
                    counter++
                }
            }
        }
        Log.d("eaea", (counter == arrayTujuan.size-1).toString())
        return counter == arrayTujuan.size-1
    }




    private fun displayRoutes(validDiscoveredRoutes: List<List<String>>) {
        Log.d("ere", "MASUK " + validDiscoveredRoutes.toString())
        if (validDiscoveredRoutes.isNotEmpty()) {
            val routeString = validDiscoveredRoutes.joinToString("\n") {
                it.joinToString(" -> ") { route -> route }
            }
//            tv_jalan.text = validDiscoveredRoutes.toString()
        } else {
            tv_jalan.text = "Tidak ada rute yang ditemukan."
        }
    }


}
