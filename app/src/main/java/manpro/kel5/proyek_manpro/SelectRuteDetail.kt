package manpro.kel5.proyek_manpro

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.blue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import manpro.kel5.proyek_manpro.R
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import com.android.volley.Request
import com.google.android.gms.maps.model.Marker
import com.google.firebase.firestore.FirebaseFirestore


class SelectRuteDetail : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    companion object{
        const val tujuan = "gf"
        const val asal = "df"
        const val index = "asghas"
        const val filterOpt = "ghgh"
        const val filterBus = "rer"
        const val filterTrain = "wqwe"
        const val tanggal = "e"
        const val username = "kkk"
    }

    private var myMap: GoogleMap? = null
    private var arStop = arrayListOf<Stop>()
    private var arRute = arrayListOf<Rutee>()
    private lateinit var dataAsal: String
    private lateinit var dataTujuan: String
    private lateinit var indexx: String
    private  var dataFilterOpt: Int = 0
    private  var dataFilterBus: Boolean = false
    private  var dataFilterTrain: Boolean = false
    private var arrayTujuan: ArrayList<String> = ArrayList()
    private lateinit var tanggalDate:String
    private lateinit var url:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_map)

        val db = Firebase.firestore
        val dataIntent = intent.getParcelableExtra<Rute>("kirimData")
        val dataIndex = intent.getStringExtra("index") ?: ""

        dataAsal = intent.getStringExtra(ChooseRoute.asal) ?: ""
        dataTujuan = intent.getStringExtra(ChooseRoute.tujuan) ?: ""
        dataFilterOpt = intent.getIntExtra(ChooseRoute.filterOpt, 1)
        dataFilterBus = intent.getBooleanExtra(ChooseRoute.filterBus, true)
        dataFilterTrain = intent.getBooleanExtra(ChooseRoute.filterTrain, true)
        arrayTujuan = intent.getStringArrayListExtra(SelectRute.arrayStopp) ?: ArrayList()
        tanggalDate = intent.getStringExtra(ChooseRoute.tanggal) ?: ""
        val username = intent.getStringExtra(ChooseRoute.username) ?: ""

        Log.d("mxmx", "username SelectRuteDetail " + username)
        val _tv_title = findViewById<TextView>(R.id.tv_title)
        val _btn_back_c_rute = findViewById<ImageView>(R.id.btn_back_c_rute)

        _tv_title.text = "Rute " + dataIndex.toString()
        _btn_back_c_rute.setOnClickListener{
            val intentWithData = Intent(this@SelectRuteDetail, ChooseRoute::class.java).apply {
                putExtra("kirimData", dataIntent)
                putExtra(ChooseRoute.asal, dataAsal)
                putExtra(ChooseRoute.tujuan, dataTujuan)
                putExtra(ChooseRoute.filterOpt, dataFilterOpt)
                putExtra(ChooseRoute.index, dataIndex);
                putExtra(ChooseRoute.filterBus, dataFilterBus)
                putExtra(ChooseRoute.filterTrain, dataFilterTrain)
                putStringArrayListExtra(SelectRute.arrayStopp, arrayTujuan)
                putExtra(ChooseRoute.tanggal, tanggalDate)

            }
            startActivity(intentWithData)

        }
        // Fetch all stops
        db.collection("StopBaru").get().addOnSuccessListener { stopResult ->
            val stopsMap = mutableMapOf<String, Stop>()

            for (stopDocument in stopResult) {
                val docId = stopDocument.id
                val idStop = stopDocument.getString("id_stop") ?: ""
                val namaStop = stopDocument.getString("nama") ?: ""
                val latitudeStop = stopDocument.getDouble("latitude") ?: 0.0
                val longitudeStop = stopDocument.getDouble("longitude") ?: 0.0
                val jenisStop = stopDocument.getString("jenis") ?: ""

                val stop = Stop(docId, idStop, namaStop, latitudeStop, longitudeStop, jenisStop)
                stopsMap[namaStop] = stop
            }

            // Fetch routes
            // uji coba B010 rute PCM - GM3 - GM2
            arRute.clear()
            if (dataIntent != null) {
                for (rute in dataIntent.doc_rute) {
                    db.collection("RuteBaru").get().addOnSuccessListener { routeResult ->
                        for (routeDocument in routeResult) {
                            val docId = routeDocument.id
                            if (docId == rute)
                            {
                                val idRute = routeDocument.getString("id_rute") ?: ""
                                val idSource = routeDocument.getString("id_stop_source") ?: ""
                                val idDest = routeDocument.getString("id_stop_dest") ?: ""
                                val idTransport = routeDocument.getString("id_transportasi") ?: ""
                                val jarak = routeDocument.getLong("jarak")?.toInt() ?: 0
                                val durasi = routeDocument.getLong("estimasi")?.toInt() ?: 0
                                val biaya = routeDocument.getLong("price")?.toInt() ?: 0
                                val jamBerangkat = routeDocument.getLong("jam_berangkat")?.toInt() ?: 0
                                val jamSampai = routeDocument.getLong("jam_sampai")?.toInt() ?: 0

                                // Ambil detail dari stop
                                val sourceStop = stopsMap[idSource]
                                val destStop = stopsMap[idDest]
                                Log.d("pop", "idSource " + idSource)
                                Log.d("pop", "source stop " + sourceStop)

                                // Create Rute object
                                val route = Rutee(
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
                                Log.d("owow", "counter : $rute")
                                Log.d("owow", "rute id : $docId")
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
        }
    }

    private var jenis : String =""
    //buat google map


//    override fun onMapReady(googleMap: GoogleMap?) {
//        myMap = googleMap
//
//        myMap?.setOnMarkerClickListener(this)
//        myMap?.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
//            override fun getInfoWindow(marker: Marker): View? {
//                return null // Use default frame
//            }
//
//            @SuppressLint("MissingInflatedId")
//            override fun getInfoContents(marker: Marker): View {
//                // Inflate custom layout
//                val view = layoutInflater.inflate(R.layout.custom_map_info, null)
//
//                // Set information in custom layout
//                val title = view.findViewById<TextView>(R.id.info_title)
//                val info_snippet = view.findViewById<TextView>(R.id.info_snippet)
//
//                val db = FirebaseFirestore.getInstance()
//                db.collection("StopBaru")
//                    .get()
//                    .addOnSuccessListener { documents ->
//                        for (document in documents) {
//                            val namaStop = document.getString("nama")
//                            if (namaStop == marker.snippet ) {
//                                jenis = document.getString("jenis").toString()
//                            }
//                        }
//                    }
//                    .addOnFailureListener { exception ->
//                    }
//
//                title.text = jenis + " " + marker.title
//                info_snippet.text = formatTime(marker.snippet)
////                if (marker.snippet != null && marker.snippet.startsWith("B")) {
////                    title.text = jenis + " " + marker.title
////                    view.setBackgroundColor(Color.YELLOW)
////                } else {
////                    title.text = "Stasiun " + marker.title
////                }
//
////                snippet.text = marker.snippet
//
//                return view
//            }
//        })
//
//        // Buat marker dan direction berdasarkan yang ada di dalam array rute
//        for ((index, rute) in arRute.withIndex()) {
//            if (index == 0) {
//                val positionSource = LatLng(rute.latitude_source, rute.longitude_source)
//                myMap?.addMarker(MarkerOptions().position(positionSource).title(rute.nama_source).snippet(rute.jam_berangkat.toString()))
//            }
//            val positionDest = LatLng(rute.latitude_dest, rute.longitude_dest)
//            myMap?.addMarker(MarkerOptions().position(positionDest).title(rute.nama_dest).snippet(rute.jam_sampai.toString()))
//            if (index != arRute.size ) {
//                val url = getUrl(
//                    LatLng(rute.latitude_source, rute.longitude_source),
//                    LatLng(rute.latitude_dest, rute.longitude_dest),
//                    "driving"
//                )
//                direction(url)
//            }
//        }
//
//        // Pindah fokus kamera ke titik awal
//        val sby = LatLng(arRute.get(0).latitude_source, arRute.get(0).longitude_source)
//        myMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(sby, 16f))
//
//    }
override fun onMapReady(googleMap: GoogleMap?) {
    myMap = googleMap

    myMap?.setOnMarkerClickListener(this)
    myMap?.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
        override fun getInfoWindow(marker: Marker): View? {
            return null // Use default frame
        }

        @SuppressLint("MissingInflatedId")
        override fun getInfoContents(marker: Marker): View {
            // Inflate custom layout
            val view = layoutInflater.inflate(R.layout.custom_map_info, null)

            // Set information in custom layout
            val title = view.findViewById<TextView>(R.id.info_title)
            val info_snippet = view.findViewById<TextView>(R.id.info_snippet)

            val db = FirebaseFirestore.getInstance()
            db.collection("StopBaru")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val namaStop = document.getString("nama")
                        if (namaStop == marker.snippet) {
                            jenis = document.getString("jenis").toString()
                        }
                    }
                }
                .addOnFailureListener { exception ->
                }

            title.text = jenis + " " + marker.title
            info_snippet.text = formatTime(marker.snippet)

            return view
        }
    })

    // Create a map of id_transportasi to colors
    val colorMap = mutableMapOf<String, Int>()
    var currentColorIndex = 0
    val colors = listOf(
        Color.RED,
        Color.GREEN,
        Color.BLUE,
        Color.YELLOW,
        Color.CYAN,
        Color.MAGENTA,
        Color.DKGRAY,
        Color.LTGRAY,
        Color.BLACK
    )

    // Create markers and directions based on the arRute array
    for ((index, rute) in arRute.withIndex()) {
        val idTransportasi = rute.id_transportasi
        val color = colorMap.getOrPut(idTransportasi) {
            colors[currentColorIndex++ % colors.size]
        }

        if (index == 0) {
            val positionSource = LatLng(rute.latitude_source, rute.longitude_source)
            myMap?.addMarker(
                MarkerOptions()
                    .position(positionSource)
                    .title(rute.nama_source)
                    .snippet(rute.jam_berangkat.toString())
            )
        }

        val positionDest = LatLng(rute.latitude_dest, rute.longitude_dest)
        myMap?.addMarker(
            MarkerOptions()
                .position(positionDest)
                .title(rute.nama_dest)
                .snippet(rute.jam_sampai.toString())
        )

        if (index != arRute.size) {
            if(arRute[index].id_transportasi.startsWith("K")){
                 url = getUrl(
                    LatLng(rute.latitude_source, rute.longitude_source),
                    LatLng(rute.latitude_dest, rute.longitude_dest),
                    "transit","train"
                )
            }else{
                 url = getUrl(
                    LatLng(rute.latitude_source, rute.longitude_source),
                    LatLng(rute.latitude_dest, rute.longitude_dest),
                    "driving", "bus")
            }

            Log.d("lili", "url : " + url)
            direction(url, color) // Pass the color to the direction function
        }
    }

    // Move camera focus to the starting point
    val sby = LatLng(arRute[0].latitude_source, arRute[0].longitude_source)
    myMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(sby, 16f))
}


    fun formatTime(time: String): String {
        // Insert colon at the correct position
        return time.substring(0, 2) + ":" + time.substring(2, 4)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        marker.showInfoWindow()
        return true // Return true to indicate we've handled the event
    }

    private fun direction(url: String, color: Int) {
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    val status = response.getString("status")
                    if (status.equals("OK", ignoreCase = true)) {
                        val routes = response.getJSONArray("routes")
                        val points = ArrayList<LatLng>()
                        var polylineOptions: PolylineOptions? = null

                        for (i in 0 until routes.length()) {
                            val legs = routes.getJSONObject(i).getJSONArray("legs")
                            for (j in 0 until legs.length()) {
                                val steps = legs.getJSONObject(i).getJSONArray("steps")
                                for (k in 0 until steps.length()) {
                                    val polyline = steps.getJSONObject(k).getJSONObject("polyline")
                                        .getString("points")
                                    val list = decodePoly(polyline)
                                    for (l in list) {
                                        val position = LatLng(l.latitude, l.longitude)
                                        points.add(position)
                                    }
                                }
                            }
                        }
                        polylineOptions = PolylineOptions()
                        polylineOptions.addAll(points)
                        polylineOptions.width(10f)
                        polylineOptions.color(color) // Use the passed color
                        polylineOptions.geodesic(true)

                        myMap?.addPolyline(polylineOptions)
                    }
                } catch (e: JSONException) {
                    Log.e("Map", "Error parsing JSON: $e")
                }
            },
            { error ->
                Log.e("Map", "Error fetching JSON response: $error")
            })

        // Add request to the RequestQueue
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

//    private fun direction(url: String) {
//        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
//            { response ->
//                try {
//                    val status = response.getString("status")
//                    if (status.equals("OK", ignoreCase = true)) {
//                        Log.d("kiki", "masuk")
//                        val routes = response.getJSONArray("routes")
//                        Log.d("kiki", "routes $routes")
//                        val points = ArrayList<LatLng>()
//                        var polylineOptions: PolylineOptions? = null
//
//                        for (i in 0 until routes.length()) {
//                            val legs = routes.getJSONObject(i).getJSONArray("legs")
//                            for (j in 0 until legs.length()) {
//                                val steps = legs.getJSONObject(i).getJSONArray("steps")
//                                Log.d("kiki", "steps $steps")
//                                for (k in 0 until steps.length()) {
//                                    val polyline = steps.getJSONObject(k).getJSONObject("polyline")
//                                        .getString("points")
//                                    val list = decodePoly(polyline)
//                                    for (l in list) {
//                                        val position = LatLng(l.latitude, l.longitude)
//                                        points.add(position)
//                                    }
//                                }
//                            }
//                        }
//                        polylineOptions = PolylineOptions()
//                        polylineOptions.addAll(points)
//                        polylineOptions.width(10f)
//                        polylineOptions.color(ContextCompat.getColor(this, R.color.red))
//                        polylineOptions.geodesic(true)
//
//                        myMap?.addPolyline(polylineOptions)
//                    }
//                } catch (e: JSONException) {
//                    Log.e("Map", "Error parsing JSON: $e")
//                }
//            },
//            { error ->
//                Log.e("Map", "Error fetching JSON response: $error")
//            })
//
//        // Tambahkan request ke RequestQueue.
//        Volley.newRequestQueue(this).add(jsonObjectRequest)
//    }
//private fun direction(url: String, color: Int) {
//    val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
//        { response ->
//            try {
//                val status = response.getString("status")
//                if (status.equals("OK", ignoreCase = true)) {
//                    val routes = response.getJSONArray("routes")
//                    val points = ArrayList<LatLng>()
//                    var polylineOptions: PolylineOptions? = null
//
//                    for (i in 0 until routes.length()) {
//                        val legs = routes.getJSONObject(i).getJSONArray("legs")
//                        for (j in 0 until legs.length()) {
//                            val steps = legs.getJSONObject(i).getJSONArray("steps")
//                            for (k in 0 until steps.length()) {
//                                val polyline = steps.getJSONObject(k).getJSONObject("polyline")
//                                    .getString("points")
//                                val list = decodePoly(polyline)
//                                for (l in list) {
//                                    val position = LatLng(l.latitude, l.longitude)
//                                    points.add(position)
//                                }
//                            }
//                        }
//                    }
//                    polylineOptions = PolylineOptions()
//                    polylineOptions.addAll(points)
//                    polylineOptions.width(10f)
//                    polylineOptions.color(ContextCompat.getColor(this, R.color.red)) // Use the passed color
//                    polylineOptions.geodesic(true)
//
//                    myMap?.addPolyline(polylineOptions)
//                }
//            } catch (e: JSONException) {
//                Log.e("Map", "Error parsing JSON: $e")
//            }
//        },
//        { error ->
//            Log.e("Map", "Error fetching JSON response: $error")
//        })
//
//    // Tambahkan request ke RequestQueue.
//    Volley.newRequestQueue(this).add(jsonObjectRequest)
//}


    // untuk nyusun url api google map direction
    private fun getUrl(
        origin: LatLng,
        dest: LatLng,
        directionMode: String,
        transitMode: String
    ): String {
        val str_origin = "origin=${origin.latitude},${origin.longitude}"
        val str_dest = "destination=${dest.latitude},${dest.longitude}"
        val mode = "mode=$directionMode"
        val transit_mode = "transit_mode=$transitMode"
        val apiKey =
            getString(R.string.my_map_api_key)
        val parameters = "$str_origin&$str_dest&$mode&$transit_mode&key=$apiKey"
        val output = "json"
        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters"
    }


    //decode polyline dari api jadi list latitude dan longitude
    fun decodePoly(encoded: String): List<LatLng> {
        val poly = mutableListOf<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val p = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            poly.add(p)
        }
        return poly
    }

}


