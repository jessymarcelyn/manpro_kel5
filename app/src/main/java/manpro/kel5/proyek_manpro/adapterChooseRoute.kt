package manpro.kel5.proyek_manpro

import android.media.browse.MediaBrowser.ItemCallback
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView


class adapterChooseRoute (
    private val listNama: MutableList<String>, private val listJam: MutableList<String>, private val listTranspor: MutableList<String>
): RecyclerView.Adapter<adapterChooseRoute.ListViewHolder>(){

    inner class ListViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        var _tv_lokasi : TextView = itemView.findViewById(R.id.tv_lokasi)
        var _tv_jamBerangkat : TextView = itemView.findViewById(R.id.tv_jamBerangkat)
        var _tv_trans : TextView = itemView.findViewById(R.id.tv_bus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.list_choose_route, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listNama.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var nama = listNama[position]
        var jam = listJam[position]
        var trans = listTranspor[position]

        val jamStr = jam.toString().padStart(4, '0')
        val formattedJam = "${jamStr.substring(0, 2)}:${jamStr.substring(2, 4)}"

        holder._tv_lokasi.text = nama
        holder._tv_jamBerangkat.text = formattedJam
        holder._tv_trans.text = trans
//        holder._tv_jamBerangkat.text = rutee.jam_berangkat


    }
}
