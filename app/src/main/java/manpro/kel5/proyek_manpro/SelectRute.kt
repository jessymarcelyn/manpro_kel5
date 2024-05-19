package manpro.kel5.proyek_manpro

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.DocumentSnapshot


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

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selectrute)

        _isAsal = intent.getBooleanExtra(isAsal, false)
        dataAsal = intent.getStringExtra(asal) ?: ""
        dataTujuan = intent.getStringExtra(tujuan) ?: ""

        val _btn_back = findViewById<ImageView>(R.id.btn_back1)
        _btn_back.setOnClickListener {
            val intentWithData = Intent(this@SelectRute, Home::class.java).apply {
                putExtra(Home.dataAsall, dataAsal)
                putExtra(Home.dataTujuann, dataTujuan)
                putExtra(Home.isAsall, false)
            }
            startActivity(intentWithData)
        }

        Log.d("eueu", "dataAsal " + dataAsal)
        Log.d("eueu", "dataTujuan " +  dataTujuan)

        val btnSearch = findViewById<ImageButton>(R.id.btnSearch1)
        btnSearch.setOnClickListener {

        }
        val ruteText = StringBuilder()
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

            override fun gotoDetail(data: Rute) {
//                val intent = Intent(this@SelectRute, Map::class.java)
                val intent = Intent(this@SelectRute, SelectRuteDetail::class.java)
                Log.d("uyuy", data.toString())
                intent.putExtra("kirimData", data)
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
    private fun trackRoute(
        tempatAwal: String,
        tempatTujuan: String,
        ruteText: StringBuilder,
        currentRoute: List<String> = listOf(),
        discoveredRoutes: MutableSet<List<String>> = mutableSetOf(),
        prevRouteDocId: String? = null
    ) {
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
                                val durasi = document.getLong("estimasi")?.toInt() ?: 0
                                val biaya = document.getLong("price")?.toInt() ?: 0
                                Log.d("mhmh", "masuk1 ")
                                Log.d("mhmh", "namaAsal " + namaAsal)
                                Log.d("mhmh", "namaTujuan " + namaTujuan)

                                if (namaAsal != null && namaTujuan != null && jamBerangkat != null && jamSampai != null) {
                                    Log.d("mhmh", "masuk2 ")
                                    listId.add(docId)
                                    listAsal.add(namaAsal)
                                    listTujuan.add(namaTujuan)
                                    listHarga.add(durasi)
                                    listDurasi.add(biaya)
                                    listJamBerangkat.add(jamBerangkat)
                                    listJamSampai.add(jamSampai)

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
                                                listJamSampai
                                            )
                                        )
                                        counter++

                                        if (counter == totalRoutes) {
                                            // Semua rute sudah berhasil
                                            Log.d("mjmj", arRute.toString())
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
}
