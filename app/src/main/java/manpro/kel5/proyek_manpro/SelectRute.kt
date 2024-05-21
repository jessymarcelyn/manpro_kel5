package manpro.kel5.proyek_manpro

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
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
        const val asal = "ddd"
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
    private var bus : Boolean = false
    private var train : Boolean = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selectrute)

        _isAsal = intent.getBooleanExtra(isAsal, false)
        dataAsal = intent.getStringExtra(asal) ?: ""
        dataTujuan = intent.getStringExtra(tujuan) ?: ""

        val _btn_back = findViewById<ImageView>(R.id.btn_back_c_rute)
        _btn_back.setOnClickListener {
            val intentWithData = Intent(this@SelectRute, Home::class.java).apply {
                putExtra(Home.dataAsall, dataAsal)
                putExtra(Home.dataTujuann, dataTujuan)
                putExtra(Home.isAsall, false)
            }
            startActivity(intentWithData)
        }

        val btnFilter = findViewById<ImageButton>(R.id.btnFilter1)

        btnFilter.setOnClickListener{
            showFilterDialog()
        }

//        BottomSheetBehavior.from(sheet).ap

        Log.d("eueu", "dataAsal " + dataAsal)
        Log.d("eueu", "dataTujuan " +  dataTujuan)

        val btnSearch = findViewById<ImageButton>(R.id.btnSearch1)
        btnSearch.setOnClickListener {

        }
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
//                intent.putExtra("kirimData", data)
                val intent = Intent(this@SelectRute, ChooseRoute::class.java)
                Log.d("uyuy", data.toString())
                intent.putExtra("kirimData", data)
                intent.putExtra(ChooseRoute.asal, dataAsal)
                intent.putExtra(ChooseRoute.tujuan, dataTujuan)
                intent.putExtra(ChooseRoute.index, pos.toString());
                startActivity(intent)
            }
        })

//        val recyclerView: RecyclerView = findViewById(R.id.routeSelect)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//
//        val tvRuteJalan: TextView = findViewById(R.id.tv_rutejalan)
//        val routeIds: ArrayList<String>? = intent.getStringArrayListExtra("routeIds")
//        println("Isi intent: $routeIds")
//        if (routeIds != null) {
//            val routeStringBuilder = StringBuilder()
//            routeStringBuilder.append("Rute:\n")
//            for ((index, routeId) in routeIds.withIndex()) {
//                routeStringBuilder.append("Rute ${index + 1}: $routeId -> $routeId\n")
//            }
//
//            tvRuteJalan.text = routeStringBuilder.toString()
//
//            val routeIdsList: List<List<String>> = listOf(routeIds)
//            val adapter = RouteAdapter(routeIdsList)
//            recyclerView.adapter = adapter
//        }



    }
    private fun showFilterDialog() {
        // Create a new dialog
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.filter_menu)

        // Set the dialog properties
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

        var selectedRadioButtonId = -1

        // Set a checked change listener for the radio group
        _radioGroupFilter.setOnCheckedChangeListener { group, checkedId ->
            selectedRadioButtonId = checkedId
        }

        _btnSort.setOnClickListener {
            bus = _idSwitch.isChecked
            train = _idSwitch2.isChecked

            //blm bisa mempertahanin kalau diclick

            Log.d("nbnb", "bus : " + bus)
            Log.d("nbnb", "train : " + train)
            if (selectedRadioButtonId != -1) {
                val selectedRadioButton = dialog.findViewById<RadioButton>(selectedRadioButtonId)
                val filterText = selectedRadioButton.text.toString()
                if(selectedRadioButtonId == _btnRadio1.id){
                    _filterOpt = 1
                    Toast.makeText(this, "Filter applied1: $filterText", Toast.LENGTH_SHORT).show()
                }
                else if(selectedRadioButtonId == _btnRadio2.id){
                    _filterOpt = 2
                    Toast.makeText(this, "Filter applied2: $filterText", Toast.LENGTH_SHORT).show()
                }
                else if(selectedRadioButtonId == _btnRadio3.id){
                    _filterOpt = 3
                    Toast.makeText(this, "Filter applied3: $filterText", Toast.LENGTH_SHORT).show()
                }
            }
            fetchRutes()
            dialog.dismiss()
        }


        // Show the dialog
        dialog.show()
    }

    private fun trackRoute(
        tempatAwal: String,
        tempatTujuan: String,
        ruteText: StringBuilder,
        currentRoute: List<String> = listOf(),
        discoveredRoutes: MutableSet<List<String>> = mutableSetOf(),
        prevRouteDocId: String? = null
    ) {
        arRute.clear()

        db.collection("Rute")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val idStopSource = document.getString("id_stop_source")
                    val idStopDest = document.getString("id_stop_dest")
                    val departureTimeSource = document.getString("jam_berangkat")?.toInt()
                    val arrivalTimeDest = document.getString("jam_sampai")?.toInt()
                    val currentRouteDocId = document.id

                    newRoute = currentRoute + currentRouteDocId
                    if (idStopSource == tempatAwal && idStopDest != null) {
                        if (idStopDest == tempatTujuan && departureTimeSource != null && arrivalTimeDest != null) {
                            // Cek antar route apakah sudah benar
                            val connectionValidity = isValidRoute(newRoute, documents)
                            if (connectionValidity.all { it }) { // Kalau bener semua
                                discoveredRoutes.add(newRoute)
                            }
                        } else if (idStopDest !in currentRoute) {
                            val newDiscoveredRoutesLocal = mutableSetOf<List<String>>()
                            trackRoute(
                                idStopDest,
                                tempatTujuan,
                                ruteText,
                                newRoute,
                                newDiscoveredRoutesLocal,
                                currentRouteDocId
                            )
                            discoveredRoutes.addAll(newDiscoveredRoutesLocal)
//                            Log.d("dfdf", "newDiscoveredRoutesLocal " + newDiscoveredRoutesLocal.toString() )
                        }
                    }
                }
                // Pastiin lagi discovered routenya udah bener atau gk
                if (discoveredRoutes.isNotEmpty()) {
                    for (i in discoveredRoutes.indices) {
                        val route = discoveredRoutes.elementAt(i)
                        val connectionValidity = isValidRoute(route, documents)
                        if (connectionValidity.all { it }) { // Kalau bener semua
                            validDiscoveredRoutes.add(route)
                        }
                    }
                    Log.d("trtr", validDiscoveredRoutes.toString())
//                    displayRoutes(validDiscoveredRoutes)
//                        displayRoutes(validDiscoveredRoutes)
                    fetchRutes()
                }
            }
            .addOnFailureListener {
            }
    }

    private fun isValidRoute(
        currentRoute: List<String>,
        documents: QuerySnapshot
    ): List<Boolean> {
        val connectionValidity = mutableListOf<Boolean>()
        for (i in 0 until currentRoute.size - 1) {
            val prevRouteDocId = currentRoute[i]
            val nextRouteDocId = currentRoute[i + 1]

            var isValid = true;
            for (document in documents) {
                if (document.id == prevRouteDocId) {
                    val prevArrivalTime = document.getString("jam_sampai")?.toInt()
                    if (prevArrivalTime != null) {
                        for (nextDocument in documents) {
                            if (nextDocument.id == nextRouteDocId) {
                                val nextDepartureTime =
                                    nextDocument.getString("jam_berangkat")?.toInt()
                                if (nextDepartureTime != null && prevArrivalTime > nextDepartureTime) {
                                    isValid = false;
                                    break;
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
            }
            connectionValidity.add(isValid);
        }
        return connectionValidity;
    }

    fun fetchRutes() {
        val totalRoutes = validDiscoveredRoutes.size
        var counter = 0

        fun processRoute(innerList: List<String>) {
            val listId = mutableListOf<String>()
            val listAsal = mutableListOf<String>()
            val listTujuan = mutableListOf<String>()
            val listHarga = mutableListOf<Int>()
            val listDurasi = mutableListOf<Int>()
            val listJamBerangkat = mutableListOf<String>()
            val listJamSampai = mutableListOf<String>()
            val listTranspor = mutableListOf<String>()

            var counterInnerList = 0

            for (ruteId in innerList) {
                Log.d("mhmh", "ruteID " + ruteId)
                db.collection("Rute")
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val docId = document.id
                            if (docId == ruteId) {
                                val namaAsal = document.getString("id_stop_source")
                                val namaTujuan = document.getString("id_stop_dest")
                                val jamBerangkat = document.getString("jam_berangkat")
                                val jamSampai = document.getString("jam_sampai")
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
                                    listHarga.add(durasi)
                                    listDurasi.add(biaya)
                                    listJamBerangkat.add(jamBerangkat)
                                    listJamSampai.add(jamSampai)
                                    listTranspor.add(id_transportasi)

                                    counterInnerList++

                                    if (counterInnerList == innerList.size) {
                                        // Semua inner rute sudah berhasil
                                        arRute.add(
                                            Rute(
                                                listId,
                                                listAsal,
                                                listTujuan,
                                                listHarga,
                                                listDurasi,
                                                listJamBerangkat,
                                                listJamSampai,
                                                listTranspor
                                            )
                                        )
                                        counter++

                                        if (counter == totalRoutes) {
                                            // Semua rute sudah berhasil
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
                    }
            }
        }

        // Process each route
        for (innerList in validDiscoveredRoutes) {
            processRoute(innerList)
            arRute.clear()
        }
    }

    fun sortRutes(arRute: ArrayList<Rute>) {
        if (_filterOpt == 1) {
            val timeFormat = SimpleDateFormat("HHmm", Locale.getDefault())
            arRute.sortBy { rute ->
                // rute waktu sampai tercepat
                Log.d("wdwd", rute.jam_sampai.lastOrNull().toString())
                rute.jam_sampai.lastOrNull()?.let { jamSampai ->
                    val date: Date = timeFormat.parse(jamSampai)!!
                    val calendar = Calendar.getInstance()
                    calendar.time = date
                    val hours = calendar.get(Calendar.HOUR_OF_DAY)
                    val minutes = calendar.get(Calendar.MINUTE)
                    hours * 60 + minutes
                } ?: Int.MAX_VALUE  // Default to a large value if listJamSampai is empty
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
