//package manpro.kel5.proyek_manpro
//
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import android.widget.TextView
//
//class SelectRute : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_selectrute)
//
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
//    }
//}
