package manpro.kel5.proyek_manpro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore

class AddStop : AppCompatActivity() {
    private lateinit var id_stop : EditText
    private lateinit var nama_stop : EditText
    private lateinit var jenis_stop : EditText
    private lateinit var latitude_stop : EditText
    private lateinit var longitude_stop : EditText
    private lateinit var save_button : Button
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_stop)

        id_stop = findViewById(R.id.edt_id_stop)
        nama_stop = findViewById(R.id.edt_nama_stop)
        jenis_stop = findViewById(R.id.edt_jenis_stop)
        latitude_stop = findViewById(R.id.edt_latitude_stop)
        longitude_stop = findViewById(R.id.edt_longitude_stop)
        save_button = findViewById(R.id.btn_save)

        db = FirebaseFirestore.getInstance()

        save_button.setOnClickListener {
            saveStop()
            setEmpty()
        }
    }

    private fun saveStop() {
        // Implementation for saving the stop data to Firestore
        val id = id_stop.text.toString().trim()
        val nama = nama_stop.text.toString().trim()
        val jenis = jenis_stop.text.toString().trim()
        val latitude = latitude_stop.text.toString().trim().toDouble()
        val longitude = longitude_stop.text.toString().trim().toDouble()

        if (id.isEmpty() || nama.isEmpty() || jenis.isEmpty() || latitude == null || longitude == null) {
            Toast.makeText(this, "Some field Empty", Toast.LENGTH_SHORT).show()
            return
        }

        val newStop = Stop(
            id_stop = id,
            nama = nama,
            jenis = jenis,
            latitude = latitude,
            longitude = longitude
        )

        db.collection("StopBaru")
            .add(newStop)
            .addOnSuccessListener { documentReference ->
                // Get the generated document ID
                val docId = documentReference.id

                // Update the newStop object with the generated docId
                newStop.doc_id = docId

                // Add the updated newStop object to Firestore again
                db.collection("StopBaru")
                    .document(docId)
                    .set(newStop)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Save Failed", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Save Failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setEmpty(){
        id_stop.setText("")
        nama_stop.setText("")
        jenis_stop.setText("")
        latitude_stop.setText("")
        longitude_stop.setText("")
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