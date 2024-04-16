package manpro.kel5.proyek_manpro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
            trackRoute("Graha Famili", "PTC", ruteText)
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
        visited: MutableSet<String> = mutableSetOf(),
        route: String = "",
        discoveredRoutes: MutableSet<String> = mutableSetOf(),
        prevRouteDocId: String? = null
    ) {
        visited.add(tempatAwal)
        val newRoute = if (route.isNotEmpty()) "$route -> $tempatAwal" else tempatAwal

        db.collection("Rute")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val idStopSource = document.getString("id_stop_source")
                    val idStopDest = document.getString("id_stop_dest")
                    val departureTimeSource = document.getString("jam_berangkat")
                    val arrivalTimeDest = document.getString("jam_sampai")
                    val currentRouteDocId = document.id

                    if (idStopSource == tempatAwal) {
                        if (idStopDest == tempatTujuan && departureTimeSource != null && arrivalTimeDest != null) {
                            val isValidRoute = isRouteValid(prevRouteDocId, departureTimeSource.toInt(), documents)
                            if (isValidRoute) {
                                val routeString = "$newRoute -> $tempatTujuan"
                                val currentRoute = "$newRoute -> $idStopDest"
                                println("Current Route: $currentRoute")
                                if (!discoveredRoutes.contains(currentRoute)) {
                                    println("Adding Route: $currentRoute")
                                    ruteText.append("Kemungkinan: $routeString $tempatAwal -> $tempatTujuan\n")
                                    discoveredRoutes.add(currentRoute)
                                }
                            }


                        } else if (idStopDest !in visited) {
                            val newVisited = visited.toMutableSet()
                            trackRoute(idStopDest!!, tempatTujuan, ruteText, newVisited, newRoute, discoveredRoutes, currentRouteDocId)
                        }
                    }
                }
                tv_jalan.text = ruteText.toString()
            }
            .addOnFailureListener {
                // Tangani kesalahan jika terjadi
            }
    }

    private fun isRouteValid(prevRouteDocId: String?, departureTime: Int, documents: QuerySnapshot): Boolean {
        if (prevRouteDocId == null) return true

        for (document in documents) {
            if (document.id == prevRouteDocId) {
                val arrivalTime = document.getString("jam_sampai")?.toInt()
                if (arrivalTime != null && arrivalTime <= departureTime) {
                    return true
                }
                break
            }
        }
        return false
    }


//    private fun trackRoute(
//        tempatAwal: String,
//        tempatTujuan: String,
//        ruteText: StringBuilder,
//        visited: MutableSet<String> = mutableSetOf(),
//        route: String = "",
//        counter: Int = 1,
//        discoveredRoutes: MutableSet<String> = mutableSetOf()
//    ) {
//        visited.add(tempatAwal)
//        val newRoute = if (route.isNotEmpty()) "$route -> $tempatAwal" else tempatAwal
//
//        db.collection("Rute")
//            .get()
//            .addOnSuccessListener { documents ->
//                var routeCounter = counter
//
//                for (document in documents) {
//                    val idStopSource = document.getString("id_stop_source")
//                    val idStopDest = document.getString("id_stop_dest")
//
//                    if (idStopSource == tempatAwal) {
//                        if (idStopDest == tempatTujuan) {
//                            val routeString = "$newRoute -> $tempatTujuan"
//                            if (routeString !in discoveredRoutes) {
//                                ruteText.append("Kemungkinan ($routeCounter): $routeString\n")
//                                discoveredRoutes.add(routeString)
//                                routeCounter++
//                            }
//                        } else if (idStopDest !in visited) {
//                            val newVisited = visited.toMutableSet()
//                            trackRoute(idStopDest!!, tempatTujuan, ruteText, newVisited, newRoute, routeCounter, discoveredRoutes)
//                        }
//                    }
//                }
//                tv_jalan.text = ruteText.toString()
//            }
//            .addOnFailureListener {
//                // Tangani kesalahan jika terjadi
//            }
//    }









}