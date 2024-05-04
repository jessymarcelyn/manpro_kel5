package manpro.kel5.proyek_manpro

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ListLocation(context: Context, private val stops: List<String>) :
    ArrayAdapter<String>(context, R.layout.list_location, stops) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_location, parent, false)
        val _tv_lokasi = view.findViewById<TextView>(R.id.tv_lokasi)
        _tv_lokasi.text = stops[position]
        return view
    }
}
