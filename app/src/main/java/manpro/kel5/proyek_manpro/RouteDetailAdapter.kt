package manpro.kel5.proyek_manpro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RouteDetailAdapter(private val routeDetail: List<String>) : RecyclerView.Adapter<RouteDetailAdapter.RouteDetailViewHolder>() {

    inner class RouteDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewRouteDetail: TextView = itemView.findViewById(R.id.tv_ruteDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.route_detail, parent, false)
        return RouteDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: RouteDetailViewHolder, position: Int) {
        val route = routeDetail[position]
        holder.textViewRouteDetail.text = route
    }

    override fun getItemCount(): Int {
        return routeDetail.size
    }
}
