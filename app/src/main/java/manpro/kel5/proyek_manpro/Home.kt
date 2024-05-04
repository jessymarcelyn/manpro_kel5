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
    private  var routeList: List<String> = emptyList()
    private var counter: Int = 0;
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
                    val departureTimeSource = document.getString("jam_berangkat")
                    val arrivalTimeDest = document.getString("jam_sampai")
                    val currentRouteDocId = document.id

                    Log.d("rtrt", "idStopDest" + idStopDest)
                    Log.d("erer", "new route sebelum " + newRoute)
                    newRoute = currentRoute + currentRouteDocId
                    Log.d("erer", "new route sesudah " + newRoute)
                    Log.d("erer", "currentRoute " + currentRoute)
                    Log.d("erer", "currentRouteDocId " + currentRouteDocId)

                    if (idStopSource == tempatAwal && idStopDest != null) {
                        if (idStopDest == tempatTujuan && departureTimeSource != null && arrivalTimeDest != null) {
                            Log.d("mimi", "masuk" + prevRouteDocId)
                            val isValidRoute = isRouteValid(prevRouteDocId, departureTimeSource.toInt(), documents)
                            if (isValidRoute) {
//                                 newRoute = currentRoute + currentRouteDocId
                                Log.d("erer", "new route MASUK " + newRoute)
                                if (!discoveredRoutes.contains(newRoute)) {
                                    discoveredRoutes.add(newRoute)
                                }
                            }
                        } else if (idStopDest !in currentRoute) {
                            val newDiscoveredRoutesLocal = mutableSetOf<List<String>>()
                            trackRoute(idStopDest, tempatTujuan, ruteText, newRoute, newDiscoveredRoutesLocal, currentRouteDocId)
                            discoveredRoutes.addAll(newDiscoveredRoutesLocal)
                        }
                    }
                }
//                ruteText.append("Kemungkinan:\n")
                if (discoveredRoutes.isNotEmpty()) {
                    displayRoutes(discoveredRoutes.toList(), ruteText)
                }
            }
            .addOnFailureListener {
            }
    }

    private fun displayRoutes(routes: List<List<String>>, ruteText: StringBuilder) {
        Log.d("awaw", routes.toString())
        routes.forEachIndexed { index, routeList ->
            val routeString = routeList.joinToString(" -> ")
            {
                it }

            ruteText.append("Kemungkinan : $routeString\n")
        }
//        Log.d("rere", listId.toString())
        Log.d("eeeq", "rute : $ruteText")
        tv_jalan.text = ruteText.toString()
    }

    private fun isRouteValid(prevRouteDocId: String?, departureTime: Int, documents: QuerySnapshot): Boolean {
        if (prevRouteDocId == null) return true

        for (document in documents) {
            if (document.id == prevRouteDocId) {
                val arrivalTime = document.getString("jam_sampai")?.toInt()
                if (arrivalTime != null && arrivalTime <= departureTime) {
                    Log.d("rere", "documentid : " + prevRouteDocId)
                    Log.d("rere", "arroval time : " + arrivalTime + " dept time : " + departureTime)
                    return true
                }
                break
            }
        }
        return false
    }
}

