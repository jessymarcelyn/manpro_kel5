package manpro.kel5.proyek_manpro

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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


class Map : AppCompatActivity(), OnMapReadyCallback {

    private var myMap: GoogleMap? = null
    private var arStop = arrayListOf<Stop>()
    private var arRute = arrayListOf<Rutee>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_map)

        val db = Firebase.firestore

        // Fetch all stops
        db.collection("Stop").get().addOnSuccessListener { stopResult ->
            val stopsMap = mutableMapOf<String, Stop>()

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
            // uji coba B010 rute PCM - GM3 - GM2
            arRute.clear()
            for (i in 0..5) {
                db.collection("Rute").get().addOnSuccessListener { routeResult ->
                    for (routeDocument in routeResult) {
                        val docId = routeDocument.id
                        if ((i == 1 && docId == "kahEaXw31pfovUTMHorr") ||
                            (i == 2 && docId == "t1dPWQ3fN39f8lXoBkBw") ||
                            (i == 3 && docId == "l6a3pNmDlt692II4AF9b")
                        ) {
                            val idRute = routeDocument.getString("id_rute") ?: ""
                            val idSource = routeDocument.getString("id_stop_source") ?: ""
                            val idDest = routeDocument.getString("id_stop_dest") ?: ""
                            val idTransport = routeDocument.getString("id_transportasi") ?: ""
                            val jarak = routeDocument.getLong("jarak")?.toInt() ?: 0
                            val durasi = routeDocument.getLong("durasi")?.toInt() ?: 0
                            val biaya = routeDocument.getLong("biaya")?.toInt() ?: 0
                            val jamBerangkat =routeDocument.getLong("jam_berangkat")?.toInt() ?: 0
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
                            Log.d("owow", "counter : $i")
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

    //buat google map
    override fun onMapReady(googleMap: GoogleMap?) {
        myMap = googleMap

        // Buat marker dan direction berdasarkan yang ada di dalam array rute
        for ((index, rute) in arRute.withIndex()) {
            if (index == 0) {
                val positionSource = LatLng(rute.latitude_source, rute.longitude_source)
                myMap?.addMarker(MarkerOptions().position(positionSource).title(rute.nama_source))
            }
            val positionDest = LatLng(rute.latitude_dest, rute.longitude_dest)
            myMap?.addMarker(MarkerOptions().position(positionDest).title(rute.nama_dest))
            if (index != arRute.size - 1) {
                val url = getUrl(
                    LatLng(rute.latitude_source, rute.longitude_source),
                    LatLng(rute.latitude_dest, rute.longitude_dest),
                    "driving"
                )
                direction(url)
            }
        }

        // Pindah fokus kamera ke titik awal
        val sby = LatLng(arRute.get(0).latitude_source, arRute.get(0).longitude_source)
        myMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(sby, 16f))

    }

    private fun direction(url: String) {
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    val status = response.getString("status")
                    if (status.equals("OK", ignoreCase = true)) {
                        Log.d("kiki", "masuk")
                        val routes = response.getJSONArray("routes")
                        Log.d("kiki", "routes $routes")
                        val points = ArrayList<LatLng>()
                        var polylineOptions: PolylineOptions? = null

                        for (i in 0 until routes.length()) {
                            val legs = routes.getJSONObject(i).getJSONArray("legs")
                            for (j in 0 until legs.length()) {
                                val steps = legs.getJSONObject(i).getJSONArray("steps")
                                Log.d("kiki", "steps $steps")
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
                        polylineOptions.color(ContextCompat.getColor(this, R.color.red))
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

        // Tambahkan request ke RequestQueue.
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    // untuk nyusun url api google map direction
    private fun getUrl(
        origin: LatLng,
        dest: LatLng,
        directionMode: String
    ): String {
        val str_origin = "origin=${origin.latitude},${origin.longitude}"
        val str_dest = "destination=${dest.latitude},${dest.longitude}"
        val mode = "mode=$directionMode"
        val apiKey =
            getString(R.string.my_map_api_key)
        val parameters = "$str_origin&$str_dest&$mode&key=$apiKey"
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


