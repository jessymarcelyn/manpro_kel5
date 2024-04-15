package manpro.kel5.proyek_manpro

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore

class Map : AppCompatActivity(), OnMapReadyCallback {

    private var myMap: GoogleMap? = null
    private var arStop = arrayListOf<Stop>()
    private var arRute = arrayListOf<Rute>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_map)

//        val db = Firebase.firestore
//        db.collection("Stop").get().addOnSuccessListener { result ->
//            arStop.clear()
//            for (document in result) {
//                val docId = document.id
//                val idStop = document.getString("id_stop") ?: ""
//                val namaStop = document.getString("nama_stop") ?: ""
//                val latitudeStop = document.getDouble("latitude_stop") ?: 0.0
//                val longitudeStop = document.getDouble("longitude_stop") ?: 0.0
//                val jenisStop = document.getString("jenis_stop") ?: ""
//
//                val stop = Stop(docId, idStop, namaStop, latitudeStop, longitudeStop, jenisStop)
//                arStop.add(stop)
//
//            }
//            val mapFragment =
//                supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
//            mapFragment?.getMapAsync(this)
//        }

        val db = Firebase.firestore

        // Fetch all stops
        db.collection("Stop").get().addOnSuccessListener { stopResult ->
            val stopsMap = mutableMapOf<String, Stop>()

            // Iterate through stop documents to create a map of stop IDs to Stop objects
            for (stopDocument in stopResult) {
                val docId = stopDocument.id
                val idStop = stopDocument.getString("id_stop") ?: ""
                val namaStop = stopDocument.getString("nama_stop") ?: ""
                val latitudeStop = stopDocument.getDouble("latitude_stop") ?: 0.0
                val longitudeStop = stopDocument.getDouble("longitude_stop") ?: 0.0
                val jenisStop = stopDocument.getString("jenis_stop") ?: ""

                val stop = Stop(docId, idStop, namaStop, latitudeStop, longitudeStop, jenisStop)
                stopsMap[namaStop] = stop
            }

// Fetch routes
            db.collection("Rute").get().addOnSuccessListener { routeResult ->
                arRute.clear()
                for (routeDocument in routeResult) {
                    val docId = routeDocument.id
                    if (docId in listOf(
                            "kahEaXw31pfovUTMHorr",
                            "t1dPWQ3fN39f8lXoBkBw",
                            "l6a3pNmDlt692II4AF9b"
                        )
                    ) {
                        val idRute = routeDocument.getString("id_rute") ?: ""
                        val idSource = routeDocument.getString("id_stop_source") ?: ""
                        val idDest = routeDocument.getString("id_stop_dest") ?: ""
                        val idTransport = routeDocument.getString("id_transportasi") ?: ""
                        val jarak = routeDocument.getLong("jarak")?.toInt() ?: 0
                        val durasi = routeDocument.getLong("durasi")?.toInt() ?: 0
                        val biaya = routeDocument.getLong("biaya")?.toInt() ?: 0
                        val jamBerangkat = routeDocument.getString("jam_berangkat") ?: ""
                        val jamSampai = routeDocument.getString("jam_sampai") ?: ""

                        // Retrieve source and destination stops from the stopsMap
                        val sourceStop = stopsMap[idSource]
                        val destStop = stopsMap[idDest]
                        Log.d("pop", "idSource " + idSource)
                        Log.d("pop", "source stop " + sourceStop)

                        // Create Rute object
                        val route = Rute(
                            docId,
                            idRute,
                            idSource,
                            idDest,
                            idTransport,
                            jarak,
                            durasi,
                            biaya,
                            jamBerangkat,
                            jamSampai,
                            sourceStop?.nama ?: "",
                            sourceStop?.latitude ?: 0.0,
                            sourceStop?.longitude ?: 0.0,
                            destStop?.nama ?: "",
                            destStop?.latitude ?: 0.0,
                            destStop?.longitude ?: 0.0
                        )

                        arRute.add(route)
                    }
                }

                Log.d("asas", arRute.toString())
                // After processing all routes, initialize the map
                val mapFragment =
                    supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
                mapFragment?.getMapAsync(this)
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap?) {
        // Now you can use the 'googleMap' object to customize your map.
        // For example, adding markers, setting map types, enabling UI controls, etc.
        myMap = googleMap

        val icons = BitmapDescriptorFactory.fromResource(R.drawable.baseline_directions_bus_24)


        // Iterate over the list of stops
        for ((index, rute) in arRute.withIndex()) {
            if (index == 0) {
                val positionSource = LatLng(rute.latitude_source, rute.longitude_source)
                myMap?.addMarker(MarkerOptions().position(positionSource).title(rute.nama_source))
            }
            val positionDest = LatLng(rute.latitude_dest, rute.longitude_dest)
            myMap?.addMarker(MarkerOptions().position(positionDest).title(rute.nama_dest))
        }

//        Log.d("yay", arStop[0].toString())
//        val position = LatLng(arStop[0].latitude, arStop[0].longitude)
//        myMap?.addMarker(MarkerOptions().position(position).title(arStop[0].nama))

        // Move camera to focus on Surabaya
        val sby = LatLng(-7.2575, 112.7521)
        myMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(sby, 12f))
    }
}
