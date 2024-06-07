package manpro.kel5.proyek_manpro.profile

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import manpro.kel5.proyek_manpro.R
import manpro.kel5.proyek_manpro.RutePerjalanan
import manpro.kel5.proyek_manpro.Stop
import manpro.kel5.proyek_manpro.profile.DistanceMatrixResponse
import manpro.kel5.proyek_manpro.profile.DistanceMatrixService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class AddRute : AppCompatActivity() {
    private lateinit var sourceSpinner: Spinner
    private lateinit var destSpinner: Spinner
    private lateinit var transSpinner: Spinner
    //    private lateinit var distanceEditText: EditText
    private lateinit var departureTimeEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var db: FirebaseFirestore
    private lateinit var textView: TextView
    private lateinit var distanceMatrixService: DistanceMatrixService

    private lateinit var coordinate: String
    private lateinit var origins : String
    private lateinit var destinations : String
    private lateinit var tv_origins : TextView
    private lateinit var tv_dest : TextView
    private lateinit var cek_koordinat_button : Button
    private lateinit var etStartDate: EditText
    private lateinit var etFinishDate: EditText

    private lateinit var newRoute : RutePerjalanan


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_rute)

        db = FirebaseFirestore.getInstance()

        textView = findViewById(R.id.textView)
        sourceSpinner = findViewById(R.id.sn_source)
        destSpinner = findViewById(R.id.sn_dest)
        transSpinner = findViewById(R.id.sn_trans)
//        distanceEditText = findViewById(R.id.et_distance)
        departureTimeEditText = findViewById(R.id.et_departure_time)
        saveButton = findViewById(R.id.button_save)

        initSpinner(sourceSpinner)
        initSpinner(destSpinner)
        inittransSpinner(transSpinner)

        tv_origins = findViewById(R.id.tv_coordinate_origin)
        tv_dest = findViewById(R.id.tv_coordinate_dest)
        cek_koordinat_button = findViewById(R.id.btn_cek_koordinat)

        etStartDate = findViewById(R.id.et_start_date)
        etFinishDate = findViewById(R.id.et_finish_date)

        etStartDate.setOnClickListener {
            showDatePickerDialog(etStartDate)
        }

        etFinishDate.setOnClickListener {
            showDatePickerDialog(etFinishDate)
        }

        val apiKey = getString(R.string.my_map_api_key)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/distancematrix/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        distanceMatrixService = retrofit.create(DistanceMatrixService::class.java)

        cek_koordinat_button.setOnClickListener {
            val selectedSourceStopId = sourceSpinner.selectedItem.toString()
            val selectedDestinationStopId = destSpinner.selectedItem.toString()
            getCoordinateOrigin(selectedSourceStopId)
            getCoordinateDest(selectedDestinationStopId)
        }

        saveButton.setOnClickListener {
            val selectedSourceStopId = sourceSpinner.selectedItem.toString()
            val selectedDestinationStopId = destSpinner.selectedItem.toString()
            val selectedTransportationId = transSpinner.selectedItem.toString()
            val currentDate = SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(Date())

            // Provided coordinates

            origins = tv_origins.text.toString()
            destinations = tv_dest.text.toString()
            print(origins)
            print(destinations)

//            val origins ="-7.33747,112.72949"  // PERLU DIUBAH pake fun get coordinates di bawah
//            val destinations = "-7.35097,112.73641"

            val call = distanceMatrixService.getDistance(origins, destinations, apiKey)
            call.enqueue(object : Callback<DistanceMatrixResponse> {
                override fun onResponse(call: Call<DistanceMatrixResponse>, response: Response<DistanceMatrixResponse>) {
                    if (response.isSuccessful) {
                        val distanceValue = response.body()?.rows?.get(0)?.elements?.get(0)?.distance?.value
                        if (distanceValue != null) {
                            calculateAndSaveRoute(distanceValue.toDouble() / 1000, selectedSourceStopId, selectedDestinationStopId, selectedTransportationId, currentDate)
                        } else {
                            showAlert("Failed to get distance")
                        }
                    } else {
                        showAlert("API response error: ${response.errorBody()}")
                        Log.e("AddRute", "API response error: ${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<DistanceMatrixResponse>, t: Throwable) {
                    showAlert("API call failed: ${t.message}")
                    Log.e("AddRute", "API call failed", t)
                }
            })
        }
    }

    private fun calculateAndSaveRoute(distance: Double, selectedSourceStopId: String, selectedDestinationStopId: String, selectedTransportationId: String, currentDate: String) {
        db.collection("Transportasi")
            .whereEqualTo("id_transportasi", selectedTransportationId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val speed = document.getDouble("kecepatan") ?: 0.0
                    val estimation = ((distance / speed) * 3600).toInt()
                    val departureTime = departureTimeEditText.text.toString()
                    val hour = departureTime.substring(0, 2).toInt()
                    val minute = departureTime.substring(2).toInt()
                    var plusHour = hour + (estimation / 3600)
                    var plusMinute = minute + ((estimation % 3600) / 60)
                    if (plusMinute > 59) {
                        plusHour += plusMinute / 60
                        plusMinute %= 60
                    }
                    if (plusHour > 23) {
                        plusHour %= 24
                    }
                    val plusHourStr = if (plusHour < 10) "0$plusHour" else "$plusHour"
                    val plusMinuteStr = if (plusMinute < 10) "0$plusMinute" else "$plusMinute"
                    val arrivalTime = "$plusHourStr$plusMinuteStr".toInt()

                    var finalDistance = BigDecimal(distance).setScale(2, RoundingMode.HALF_EVEN).toDouble()

                    val hari_berangkat_raw = etStartDate.text.toString()
                    val hari_sampai_raw = etFinishDate.text.toString()
                    val hari_berangkat_final = convertDateFormat(hari_berangkat_raw).toInt()
                    var hari_sampai_final = 0   // default null
                    if(hari_sampai_raw != ""){
                        hari_sampai_final = convertDateFormat(hari_sampai_raw).toInt()
                    } else {
                        hari_sampai_final = 0
                    }

                    // Jenis kendaraan = kereta
                    if(document.getString("jenis") == "kereta") {
                        val price = (finalDistance * 4000).roundToInt()
                        val roundedPrice = (Math.round(price / 500.0) * 500).toInt()
                        val finalPrice = roundedPrice.toDouble()

                        newRoute = RutePerjalanan(
                            id_rute = "$selectedTransportationId$estimation$currentDate",
                            id_stop_source = selectedSourceStopId,
                            id_stop_dest = selectedDestinationStopId,
                            id_transportasi = selectedTransportationId,
                            jarak = finalDistance,
                            price = finalPrice,
                            estimasi = estimation,
                            jam_berangkat = departureTimeEditText.text.toString().toInt(),
                            jam_sampai = arrivalTime,
                            startDate = hari_berangkat_final,
                            finishDate = hari_sampai_final
                        )
                    }

                    // Jenis kendaraan = bus
                    else {
                        val price = (finalDistance * 2500).roundToInt()
                        val roundedPrice = (Math.round(price / 500.0) * 500).toInt()
                        val finalPrice = roundedPrice.toDouble()
                        newRoute = RutePerjalanan(
                            id_rute = "$selectedTransportationId$estimation$currentDate",
                            id_stop_source = selectedSourceStopId,
                            id_stop_dest = selectedDestinationStopId,
                            id_transportasi = selectedTransportationId,
                            jarak = finalDistance,
                            price = finalPrice,
                            estimasi = estimation,
                            jam_berangkat = departureTimeEditText.text.toString().toInt(),
                            jam_sampai = arrivalTime,
                            startDate = hari_berangkat_final,
                            finishDate = hari_sampai_final

                        )
                    }

                    db.collection("RuteBaru")
                        .add(newRoute)
                        .addOnSuccessListener { documentReference ->
                            showAlert("Data berhasil disimpan! arrival time = $arrivalTime \n Document id : ${documentReference.id}")
//                            distanceEditText.setText("")
                            departureTimeEditText.setText("")
                            tv_origins.setText("Origin")
                            tv_dest.setText("Dest")
                            textView.text = documentReference.id
                        }
                        .addOnFailureListener { e ->
                            showAlert("Gagal menyimpan data: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                showAlert("Gagal mengambil data kecepatan: ${e.message}")
            }
    }

    private fun initSpinner(spinner: Spinner) {
        db.collection("StopBaru")
            .get()
            .addOnSuccessListener { documents ->
                val items = mutableListOf<String>()
                for (document in documents) {
                    val namaStop = document.getString("nama")
                    if (namaStop != null) {
                        items.add(namaStop)
                    }
                }
                val sortedItems = items.sorted()

                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortedItems)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }
            .addOnFailureListener { exception ->

            }
    }

    private fun inittransSpinner(spinner: Spinner) {
        db.collection("Transportasi")
            .get()
            .addOnSuccessListener { documents ->
                val items = mutableListOf<String>()
                for (document in documents) {
                    val namaTrans = document.getString("id_transportasi")
                    if (namaTrans != null) {
                        items.add(namaTrans)
                    }
                }
                val sortedItems = items.sorted()

                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortedItems)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }
            .addOnFailureListener { exception ->

            }
    }

    private fun showAlert(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Info")
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    // ERROR FETCH DATABASE
    private fun getCoordinateOrigin(stopName: String) {
        coordinate = "Empty"
        db.collection("StopBaru")
            .whereEqualTo("nama", stopName)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    val latitude = document.getDouble("latitude")
                    val longitude = document.getDouble("longitude")
                    latitude.toString()
                    longitude.toString()
                    tv_origins.text = "$latitude,$longitude"
//                    tv_usernameDisplay.text = text
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure, e.g., log the error
                Log.e("getCoordinates", "Error getting documents: ", exception)
            }
    }

    private fun getCoordinateDest(stopName: String) {
        coordinate = "Empty"
        db.collection("StopBaru")
            .whereEqualTo("nama", stopName)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    val latitude = document.getDouble("latitude")
                    val longitude = document.getDouble("longitude")
                    latitude.toString()
                    longitude.toString()
                    tv_dest.text = "$latitude,$longitude"
//                    tv_usernameDisplay.text = text
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure, e.g., log the error
                Log.e("getCoordinates", "Error getting documents: ", exception)
            }
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Month is 0-based, so you have to add 1 to the month
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                editText.setText(selectedDate)
            }, year, month, day)

        datePickerDialog.show()
    }

    private fun convertDateFormat(inputDate: String, inputFormat: String = "dd/MM/yyyy", outputFormat: String = "ddMMyyyy"): String {
        val inputDateFormat = SimpleDateFormat(inputFormat, Locale.getDefault())
        val outputDateFormat = SimpleDateFormat(outputFormat, Locale.getDefault())
        val date = inputDateFormat.parse(inputDate)
        return outputDateFormat.format(date)
    }
}