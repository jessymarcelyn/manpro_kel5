package manpro.kel5.proyek_manpro
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class adapterRoute (
    private val listRute: ArrayList<Rute>,

//    ,
//    private val context: Context
): RecyclerView.Adapter<adapterRoute.ListViewHolder>(){

    inner class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var _tv_label: TextView = itemView.findViewById(R.id.tv_label)
        var _tv_durasi: TextView = itemView.findViewById(R.id.tv_durasi)
        var _tv_harga : TextView = itemView.findViewById(R.id.tv_harga)
        var _btn_choose : Button = itemView.findViewById(R.id.btn_choose)
        val recyclerView: RecyclerView = itemView.findViewById(R.id.listRute)

        init {
            Log.d("adapterRoute", "ViewHolder initialized")
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.list_selectroute,parent,false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listRute.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var rute = listRute[position]
        var totalBiaya = 0
        var totalDetik = 0
        for(listBiaya in rute.biaya){
            totalBiaya += listBiaya
        }
        for(listDetik in rute.durasi){
            totalDetik += listDetik
        }
        val menit = totalDetik / 60


        holder._tv_label.text = "Rute ${position+1}"
        holder._tv_harga.text = totalBiaya.toString()
        holder._tv_durasi.text = menit.toString()

        var titikRute: MutableList<String> = mutableListOf()
        for ((index, listTitik) in rute.nama_source.withIndex()) {
            titikRute.add(listTitik)
            if (index == rute.nama_source.lastIndex) {
                titikRute.add(rute.nama_dest[index])
            }
        }

        val adapter = ListRuteStop(titikRute)
        holder.recyclerView.adapter = adapter
        holder.recyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)




    }

}