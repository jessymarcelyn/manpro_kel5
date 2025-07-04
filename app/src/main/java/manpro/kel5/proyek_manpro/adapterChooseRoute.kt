package manpro.kel5.proyek_manpro

import android.graphics.Color
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
    private val listNama: MutableList<String>, private val listJamB: MutableList<Int>, private val listJamS: MutableList<Int>, private val listTranspor: MutableList<String>, private val transpor_first: String
): RecyclerView.Adapter<adapterChooseRoute.ListViewHolder>(){

    private val colorMap = mutableMapOf<String, Int>()
    private var currentColorIndex = 0
    private val colors = listOf(
        Color.RED,
        Color.GREEN,
        Color.BLUE,
        Color.YELLOW,
        Color.CYAN,
        Color.MAGENTA,
        Color.DKGRAY,
        Color.LTGRAY,
        Color.BLACK
    )

    inner class ListViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        var _tv_lokasi : TextView = itemView.findViewById(R.id.tv_lokasi)
        var _tv_jamBerangkat : TextView = itemView.findViewById(R.id.tv_jamBerangkat)
        var _tv_trans : TextView = itemView.findViewById(R.id.tv_bus)
        var _tv_durasi_transit : TextView = itemView.findViewById(R.id.tv_durasi_transit)
        var _view_garis : View = itemView.findViewById(R.id.view_garis)
        var _iv_transport : ImageView = itemView.findViewById(R.id.iv_transport)

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

        holder._tv_durasi_transit.text = duration.toString() + " Menit"
//        holder._tv_jamBerangkat.text = rutee.jam_berangkat

        val color = colorMap.getOrPut(trans) {
            if(trans == transpor_first){
                colors[0]
            }else{
                colors[currentColorIndex++ % colors.size + 1]
            }

        }
        holder._view_garis.setBackgroundColor(color)

        var bus = false
        var kereta = false

        for (transportasi in trans) {
            if (transportasi.toString().startsWith("B")) {
                bus = true
            } else if (transportasi.toString().startsWith("K")) {
                kereta = true
            }
        }
        // Set image based on the transportation mode
        if (kereta) {
            holder._iv_transport.setImageResource(R.drawable.ic_train)
        } else if (bus) {
            holder._iv_transport.setImageResource(R.drawable.ic_bus)
        } else {
            // Handle default case if neither bus nor train is true
        }

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
