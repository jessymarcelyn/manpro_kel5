package manpro.kel5.proyek_manpro

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddRute : AppCompatActivity() {
    private lateinit var sourceSpinner: Spinner
    private lateinit var destSpinner: Spinner
    private lateinit var transSpinner: Spinner
    private lateinit var distanceEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var departureTimeEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_rute)

        db = FirebaseFirestore.getInstance()

        sourceSpinner = findViewById(R.id.sn_source)
        destSpinner = findViewById(R.id.sn_dest)
        transSpinner = findViewById(R.id.sn_trans)
        distanceEditText = findViewById(R.id.et_distance)
        priceEditText = findViewById(R.id.et_price)
        departureTimeEditText = findViewById(R.id.et_departure_time)
        saveButton = findViewById(R.id.button_save)

        initSpinner(sourceSpinner)
        initSpinner(destSpinner)
        inittransSpinner(transSpinner)

        saveButton.setOnClickListener {
            val selectedSourceStopId = sourceSpinner.selectedItem.toString()
            val selectedDestinationStopId = destSpinner.selectedItem.toString()
            val selectedTransportationId = transSpinner.selectedItem.toString()
            val currentDate = SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(Date())

            db.collection("Transportasi")
                .whereEqualTo("id_transportasi", selectedTransportationId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        val speed = document.getDouble("kecepatan") ?: 0.0
                        val distance = distanceEditText.text.toString().toDouble()
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
                        if (plusHour > 23){
                            plusHour %= 24
                        }
                        val plusHourStr = if (plusHour < 10) "0$plusHour" else "$plusHour"
                        val plusMinuteStr = if (plusMinute < 10) "0$plusMinute" else "$plusMinute"
                        val arrivalTime = "$plusHourStr$plusMinuteStr"

                        val newRoute = RutePerjalanan(
                            id_rute = "$selectedTransportationId$estimation$currentDate",
                            id_stop_source = selectedSourceStopId,
                            id_stop_dest = selectedDestinationStopId,
                            id_transportasi = selectedTransportationId,
                            jarak = distance,
                            price = priceEditText.text.toString().toDouble(),
                            estimasi = estimation,
                            jam_berangkat = departureTimeEditText.text.toString(),
                            jam_sampai = arrivalTime
                        )

                        db.collection("Rute")
                            .add(newRoute)
                            .addOnSuccessListener { documentReference ->
                                showAlert("Data berhasil disimpan!")
                                distanceEditText.setText("")
                                priceEditText.setText("")
                                departureTimeEditText.setText("")
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
    }

    private fun initSpinner(spinner: Spinner) {
        db.collection("Stop")
            .get()
            .addOnSuccessListener { documents ->
                val items = mutableListOf<String>()
                for (document in documents) {
                    val namaStop = document.getString("nama_stop")
                    if (namaStop != null) {
                        items.add(namaStop)
                    }
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
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
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
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
}

