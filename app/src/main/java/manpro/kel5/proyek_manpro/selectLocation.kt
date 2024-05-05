package manpro.kel5.proyek_manpro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore


class selectLocation : AppCompatActivity() {
    companion object{
        const val isAsal = "true"
        const val tujuan = "aaa"
        const val asal = "bbb"
    }

    private  var _isAsal: Boolean = false
    private lateinit var dataAsal: String
    private lateinit var dataTujuan: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        val _btn_back = findViewById<ImageView>(R.id.btn_back)

        _isAsal = intent.getBooleanExtra(isAsal, false)
        dataAsal = intent.getStringExtra(asal) ?: ""
        dataTujuan = intent.getStringExtra(tujuan) ?: ""
        Log.d("pipi", _isAsal.toString())
        Log.d("pipi", "dataAsal" + dataAsal.toString())
        Log.d("pipi", "dataTujuan : " + dataTujuan.toString())

//        _btn_back.setOnClickListener {
//            val intent = Intent(this@selectLocation, Home::class.java)
//            startActivity(intent)
//        }

        setContentView(R.layout.activity_select_location)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        retrieveStopData()



    }
    private fun retrieveStopData() {
        val stopList = mutableListOf<String>() // List to store stop names
        val db = FirebaseFirestore.getInstance()
        db.collection("Stop")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val namaStop = document.getString("nama_stop")
                    if (namaStop != null) {
                        stopList.add(namaStop)
                    }
                }
                // Now you have the list of stop names, you can populate the ListView
                populateListView(stopList)
            }
            .addOnFailureListener { exception ->
            }

    }

    private fun populateListView(stopList: List<String>) {
        Log.d("klkl", stopList.toString())
        val _list_loc = findViewById<ListView>(R.id.listLoc)
        val adapter = ListLocation(this, stopList.sorted())
        _list_loc.adapter = adapter


        _list_loc.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as String
            if(_isAsal){
                Log.d("pipi", "masuk1")

                val intentWithData = Intent(this@selectLocation, Home::class.java).apply {
                    putExtra(Home.dataAsall, selectedItem)
                    putExtra(Home.dataTujuann, dataTujuan)
                    Log.d("pipi", "selectedItem " + selectedItem)
                    Log.d("pipi", "dataAsal1" + dataAsal.toString())
                    Log.d("pipi", "dataTujuan1 : " + dataTujuan.toString())
                    putExtra(Home.isAsall, true)
                }
                startActivity(intentWithData)
            }else{
                Log.d("pipi", "masuk2")
                val intentWithData = Intent(this@selectLocation, Home::class.java).apply {
                    putExtra(Home.dataAsall, dataAsal)
                    putExtra(Home.dataTujuann, selectedItem)
                    putExtra(Home.isAsall, false)
                }
                startActivity(intentWithData)
            }
//            Toast.makeText(this, "Clicked item: $selectedItem", Toast.LENGTH_SHORT).show()
        }
    }

}