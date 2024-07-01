package manpro.kel5.proyek_manpro
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class adapterRoute (
    private val listRute: ArrayList<Rute>,

//    ,
//    private val context: Context
): RecyclerView.Adapter<adapterRoute.ListViewHolder>(){
    private lateinit var  onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun  onItemClicked(data : Rute)
        fun delData(pos:Int)
        fun gotoDetail(data : Rute, pos: Int)
    }
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var _tv_label: TextView = itemView.findViewById(R.id.tv_label)
        var _tv_durasi: TextView = itemView.findViewById(R.id.tv_durasi)
        var _tv_harga : TextView = itemView.findViewById(R.id.tv_harga)
        var _btn_choose : Button = itemView.findViewById(R.id.btn_choose)
        val recyclerView: RecyclerView = itemView.findViewById(R.id.listRute)
        val _tv_brgkt = itemView.findViewById<TextView>(R.id.tv_brgkt)
        val _tv_brgkt2 = itemView.findViewById<TextView>(R.id.tv_brgkt2)
        val _iv_busss = itemView.findViewById<ImageView>(R.id.iv_busss)
        val _iv_keretaa = itemView.findViewById<ImageView>(R.id.iv_keretaa)

        init {
            Log.d("adapterRoute", "ViewHolder initialized")
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        Log.d("vv", listRute.toString())
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.list_selectroute,parent,false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listRute.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var rute = listRute[position]
        var totalBiaya = 0

        for(listBiaya in rute.biaya){
            totalBiaya += listBiaya
        }

        Log.d("uiui", rute.toString())
        val firstJamBerangkat = rute.jam_berangkat.first()
        val lastJamSampai = rute.jam_sampai.last()
        val differenceInMinutes = calculateTimeDifference(firstJamBerangkat, lastJamSampai)

        val formattedFirstJamBerangkat = formatTime(firstJamBerangkat)
        val formattedLastJamSampai = formatTime(lastJamSampai)

        holder._tv_brgkt.text = formattedFirstJamBerangkat
        holder._tv_brgkt2.text = formattedLastJamSampai

        holder._tv_durasi.text = differenceInMinutes.toString() + " Menit"

        holder._tv_label.text = "Rute ${position+1}"

        val formattedBiaya = String.format("%,d", totalBiaya)
        holder._tv_harga.text = formattedBiaya


        var titikRute: MutableList<String> = mutableListOf()
        for ((index, listTitik) in rute.nama_source.withIndex()) {
            titikRute.add(listTitik)
            if (index == rute.nama_source.lastIndex) {
                titikRute.add(rute.nama_dest[index])
            }
        }

        var bus = false
        var kereta = false

        for (transportasi in rute.id_transportasi) {
            if (transportasi.startsWith("B")) {
                kereta = true
            } else {
                bus = true

            }
        }

        if (!kereta) {
            holder._iv_busss.visibility = View.GONE
        } else {
            holder._iv_busss.visibility = View.VISIBLE
        }

        if (!bus) {
            holder._iv_keretaa.visibility = View.GONE
        } else {
            holder._iv_keretaa.visibility = View.VISIBLE
        }

        val adapter = ListRuteStop(titikRute)
        holder.recyclerView.adapter = adapter
        holder.recyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)

        holder._btn_choose.setOnClickListener {
            Log.d("mxmx", "masuk0")
            onItemClickCallback.gotoDetail(rute, position+1)
        }
    }
    // Convert "HHMM" string format to total minutes since midnight
    fun convertToMinutes(time: String): Int {
        val hours = time.substring(0, 2).toInt()
        val minutes = time.substring(2).toInt()
        return hours * 60 + minutes
    }

    fun calculateTimeDifference(startMinutes: Int, endMinutes: Int): Int {
        return if (endMinutes >= startMinutes) {
            endMinutes - startMinutes
        } else {
            // Handle case when the end time is on the next day
            endMinutes + (24 * 60) - startMinutes
        }
    }

    fun formatTime(timeInMinutes: Int): String {
        val hours = timeInMinutes / 100
        val minutes = timeInMinutes % 100
        return String.format("%02d:%02d", hours, minutes)
    }


}