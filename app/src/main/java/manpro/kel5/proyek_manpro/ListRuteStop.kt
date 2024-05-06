package manpro.kel5.proyek_manpro

import androidx.recyclerview.widget.RecyclerView

//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ArrayAdapter
//import android.widget.TextView
//
//class ListRuteStop(context: Context, private val stops: List<String>) :
//    ArrayAdapter<String>(context, R.layout.list_selectroute__list_rute, stops) {
//
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_selectroute__list_rute, parent, false)
//        val _tv_lokasi = view.findViewById<TextView>(R.id.tv_stop2)
//        _tv_lokasi.text = stops[position]
//        return view
//    }
//}

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView

class ListRuteStop(
    private val listRute: MutableList<String>
): RecyclerView.Adapter<ListRuteStop.ListViewHolder>(){

    inner class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var _listrutee: TextView = itemView.findViewById(R.id.tv_stop2)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.list_selectroute__list_rute,parent,false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listRute.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var rute = listRute[position]

        holder._listrutee.text = rute
    }

}

