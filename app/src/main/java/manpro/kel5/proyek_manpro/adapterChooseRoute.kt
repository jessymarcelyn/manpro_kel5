package manpro.kel5.proyek_manpro

import android.media.browse.MediaBrowser.ItemCallback
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView


class adapterChooseRoute (
    private val listNama: MutableList<String>, private val listJamB: MutableList<Int>, private val listJamS: MutableList<Int>, private val listTranspor: MutableList<String>
): RecyclerView.Adapter<adapterChooseRoute.ListViewHolder>(){

    inner class ListViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        var _tv_lokasi : TextView = itemView.findViewById(R.id.tv_lokasi)
        var _tv_jamBerangkat : TextView = itemView.findViewById(R.id.tv_jamBerangkat)
        var _tv_trans : TextView = itemView.findViewById(R.id.tv_bus)
        var _tv_durasi_transit : TextView = itemView.findViewById(R.id.tv_durasi_transit)
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
        var jamB = listJamB[position]
        var jamS = listJamS[position]
        var trans = listTranspor[position]

        val jamStr = jamB.toString().padStart(4, '0')
        val formattedJam = "${jamStr.substring(0, 2)}:${jamStr.substring(2, 4)}"

        holder._tv_lokasi.text = nama
        holder._tv_jamBerangkat.text = formattedJam
        holder._tv_trans.text = trans

        val duration = calculateTimeDifference(jamB, jamS)
        // NANTI HARUS GANTI KE ESTIMASI
        holder._tv_durasi_transit.text = duration.toString()
        Log.d("pem", jamB.toString())
        Log.d("pem", jamS.toString())
        Log.d("pem", duration.toString())
//        holder._tv_jamBerangkat.text = rutee.jam_berangkat

    }

    // Convert "HHMM" string format to total minutes since midnight
    fun convertToMinutes(time: String): Int {
        val hours = time.substring(0, 2).toInt()
        val minutes = time.substring(2).toInt()
        return hours * 60 + minutes
    }

    // Calculate the difference between two times in minutes
    fun calculateTimeDifference(start: Int, end: Int): Int {
        val startHours = start / 100
        val startMinutes = start % 100
        val endHours = end / 100
        val endMinutes = end % 100

        val startTimeInMinutes = startHours * 60 + startMinutes
        val endTimeInMinutes = endHours * 60 + endMinutes

        return if (endTimeInMinutes >= startTimeInMinutes) {
            endTimeInMinutes - startTimeInMinutes
        } else {
            // Handle case when the end time is on the next day
            endTimeInMinutes + (24 * 60) - startTimeInMinutes
        }
    }

}
