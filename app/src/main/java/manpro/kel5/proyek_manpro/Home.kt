package manpro.kel5.proyek_manpro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import manpro.kel5.proyek_manpro.BottomNav.Companion.setupBottomNavigationView

class Home : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var btnRute: Button
    private lateinit var tv_jalan: TextView
    private  var newRoute: List<String> = emptyList()
    private val validDiscoveredRoutes = mutableListOf<List<String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBottomNavigationView(this)
        FirebaseApp.initializeApp(this)

        btnRute = findViewById(R.id.btn_rute)
        tv_jalan = findViewById(R.id.tv_jalan)
        btnRute.setOnClickListener {
//            startActivity(Intent(this, SelectRute::class.java))
            val ruteText = StringBuilder()
            trackRoute("Galaxy Mall 3", "Galaxy Mall 2", ruteText)

        }


    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            else -> return super.onOptionsItemSelected(item)
        }
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
                    displayRoutes(validDiscoveredRoutes)
//                        displayRoutes(validDiscoveredRoutes)
                }
            }
            .addOnFailureListener {
            }
    }

    private fun displayRoutes(validDiscoveredRoutes: List<List<String>>) {
        Log.d("ere", validDiscoveredRoutes.toString())
        if (validDiscoveredRoutes.isNotEmpty()) {
            val routeString = validDiscoveredRoutes.joinToString("\n") {
                it.joinToString(" -> ") { route -> route }
            }

            tv_jalan.text = validDiscoveredRoutes.toString() // Update UI with routeString
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


