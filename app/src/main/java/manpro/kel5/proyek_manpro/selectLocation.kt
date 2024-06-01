package manpro.kel5.proyek_manpro

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore


class SelectLocation : AppCompatActivity() {
    companion object{
        const val isAsal = "true"
        const val tujuan = "aaa"
        const val asal = "bbb"
        const val isAdapter = "fdgd"
        const val adap = "kbj"
        const val index = "fgs"
    }

    private  var _isAsal: Boolean = false
    private lateinit var dataAsal: String
    private lateinit var dataTujuan: String
    private lateinit var dataAdap: String
    private  var _isAdapter: Boolean = false
    private var _index: Int = 0
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_select_location)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val _searchText = findViewById<TextInputEditText>(R.id.searchText)

//        val _imageSearch = findViewById<ImageButton>(R.id.btnSearch1)
//
//        _imageSearch.setOnClickListener{
//            val userInput = _searchText.text.toString()
//            Log.d("ukuk", "User input: $userInput")
//            retrieveStopDataFilter(userInput)
//        }

        _searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val userInput = s.toString()
                Log.d("ukuk", "User input: $userInput")
                retrieveStopDataFilter(userInput)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })


        _isAsal = intent.getBooleanExtra(isAsal, false)
        dataAsal = intent.getStringExtra(asal) ?: ""
        dataTujuan = intent.getStringExtra(tujuan) ?: ""
        dataAdap = intent.getStringExtra(adap) ?: ""
        _isAdapter = intent.getBooleanExtra(isAdapter, false)
        _index = intent.getIntExtra(index, 0)
        Log.d("pipi", "dataAdap " + dataAdap.toString())
        Log.d("pipi", "_isAdapter " + _isAdapter.toString())
//        Log.d("pipi", _isAsal.toString())
//        Log.d("pipi", "dataAsal" + dataAsal.toString())
//        Log.d("pipi", "dataTujuan : " + dataTujuan.toString())


        val _btn_back = findViewById<ImageView>(R.id.btn_back)
        _btn_back.setOnClickListener {
            val intentWithData = Intent(this@SelectLocation, Home::class.java).apply {
                putExtra(Home.dataAsall, dataAsal)
                putExtra(Home.dataTujuann, dataTujuan)
                putExtra(Home.isAsall, false)
            }
            startActivity(intentWithData)
        }

        retrieveStopData()
    }

    private fun retrieveStopDataFilter(input: String) {
        Log.d("ukuk", "input" + input.toString())

        val stopList = mutableListOf<String>() // List to store stop names
        val db = FirebaseFirestore.getInstance()
        db.collection("Stop")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val namaStop = document.getString("nama_stop")
                    if (namaStop != null && namaStop.startsWith(input, ignoreCase = true)) {
                        // Check if the stop name starts with the specified string (case-insensitive)
                        stopList.add(namaStop)
                    }
                }
                Log.d("ukuk", stopList.toString())
                // Now you have the filtered list of stop names, you can populate the ListView
                populateListView(stopList)
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }
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

                val intentWithData = Intent(this@SelectLocation, Home::class.java).apply {
                    putExtra(Home.dataAsall, selectedItem)
                    putExtra(Home.dataTujuann, dataTujuan)
                    Log.d("pipi", "selectedItem " + selectedItem)
                    Log.d("pipi", "dataAsal1" + dataAsal.toString())
                    Log.d("pipi", "dataTujuan1 : " + dataTujuan.toString())
                    putExtra(Home.isAsall, true)
                    putExtra(Home.isAdapter, false)
                }
                startActivity(intentWithData)
            }else if(_isAdapter){
                val intentWithData = Intent(this@SelectLocation, Home::class.java).apply {
                    putExtra(Home.dataAsall, dataAsal)
                    putExtra(Home.dataTujuann, dataTujuan)
                    putExtra(Home.isAsall, false)
                    putExtra(Home.isAdapter, true)
                    putExtra(Home.index, _index)
                    putExtra(Home.dataAdap, selectedItem)

                }
                startActivity(intentWithData)
            }
            else{
                Log.d("pipi", "masuk2")
                val intentWithData = Intent(this@SelectLocation, Home::class.java).apply {
                    putExtra(Home.dataAsall, dataAsal)
                    putExtra(Home.dataTujuann, selectedItem)
                    putExtra(Home.isAsall, false)
                    putExtra(Home.isAdapter, false)
                }
                startActivity(intentWithData)
            }
//            Toast.makeText(this, "Clicked item: $selectedItem", Toast.LENGTH_SHORT).show()
        }
    }

}