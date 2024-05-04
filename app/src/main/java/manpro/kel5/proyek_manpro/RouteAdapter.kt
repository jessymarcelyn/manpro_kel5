package manpro.kel5.proyek_manpro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class RouteAdapter(private val routes: List<List<String>>) : RecyclerView.Adapter<RouteAdapter.RouteViewHolder>() {

    inner class RouteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTAwal: TextView = itemView.findViewById(R.id.tv_tAwal)
        val tvTAkhir: TextView = itemView.findViewById(R.id.tv_tAkhir)
        val tvEstimasiWaktu: TextView = itemView.findViewById(R.id.tv_EstimasiWaktu)
        val tvEstimasiBiaya: TextView = itemView.findViewById(R.id.tv_EstimasiBiaya)
        val recyclerViewRouteDetail: RecyclerView = itemView.findViewById(R.id.recyclerViewRouteDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.route_main, parent, false)
        return RouteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        val currentRoute = routes[position]

        val firstDocumentId = currentRoute.firstOrNull()
        if (firstDocumentId != null) {
            FirebaseFirestore.getInstance().collection("Rute").document(firstDocumentId)
                .get()
                .addOnSuccessListener { document ->
                    val idStopSource = document.getString("id_stop_source")
                    holder.tvTAwal.text = idStopSource
                }
        }

        val lastDocumentId = currentRoute.lastOrNull()
        if (lastDocumentId != null) {
            FirebaseFirestore.getInstance().collection("Rute").document(lastDocumentId)
                .get()
                .addOnSuccessListener { document ->
                    val idStopDest = document.getString("id_stop_dest")
                    holder.tvTAkhir.text = idStopDest
                }
        }

        var totalEstimasi = 0
        var totalBiaya = 0
        currentRoute.forEach { routeId ->
            FirebaseFirestore.getInstance().collection("Rute").document(routeId)
                .get()
                .addOnSuccessListener { document ->
                    val estimasi = document.getLong("estimasi")
                    val price = document.getLong("price")
                    if (estimasi != null) {
                        totalEstimasi += estimasi.toInt()
                        val jam = totalEstimasi / 60
                        val menit = totalEstimasi % 60
                        holder.tvEstimasiWaktu.text = "Estimasi Waktu: $jam jam $menit menit"
                    }
                    if (price != null) {
                        totalBiaya += price.toInt()
                        val formattedBiaya = "Rp. " + String.format("%,d", totalBiaya)
                        holder.tvEstimasiBiaya.text = "Estimasi Biaya: $formattedBiaya"
                    }
                }
        }

        val routeDetailAdapter = RouteDetailAdapter(currentRoute)
        holder.recyclerViewRouteDetail.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = routeDetailAdapter
        }
    }

    override fun getItemCount(): Int {
        return routes.size
    }
}

