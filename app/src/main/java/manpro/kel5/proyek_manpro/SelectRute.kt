package manpro.kel5.proyek_manpro

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView

class SelectRute : AppCompatActivity() {
    companion object{
        const val isAsal = "true"
        const val tujuan = "ccc"
        const val asal = "ddd"
    }
    private  var _isAsal: Boolean = false
    private lateinit var dataAsal: String
    private lateinit var dataTujuan: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selectrute)

        _isAsal = intent.getBooleanExtra(isAsal, false)
        dataAsal = intent.getStringExtra(asal) ?: ""
        dataTujuan = intent.getStringExtra(tujuan) ?: ""

        val _btn_back = findViewById<ImageView>(R.id.btn_back1)
        _btn_back.setOnClickListener {
            val intentWithData = Intent(this@SelectRute, Home::class.java).apply {
                putExtra(Home.dataAsall, dataAsal)
                putExtra(Home.dataTujuann, dataTujuan)
                putExtra(Home.isAsall, false)
            }
            startActivity(intentWithData)
        }

//        val recyclerView: RecyclerView = findViewById(R.id.routeSelect)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//
//        val tvRuteJalan: TextView = findViewById(R.id.tv_rutejalan)
//        val routeIds: ArrayList<String>? = intent.getStringArrayListExtra("routeIds")
//        println("Isi intent: $routeIds")
//        if (routeIds != null) {
//            val routeStringBuilder = StringBuilder()
//            routeStringBuilder.append("Rute:\n")
//            for ((index, routeId) in routeIds.withIndex()) {
//                routeStringBuilder.append("Rute ${index + 1}: $routeId -> $routeId\n")
//            }
//
//            tvRuteJalan.text = routeStringBuilder.toString()
//
//            val routeIdsList: List<List<String>> = listOf(routeIds)
//            val adapter = RouteAdapter(routeIdsList)
//            recyclerView.adapter = adapter
//        }
    }
}
