package manpro.kel5.proyek_manpro

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.*
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import java.text.SimpleDateFormat

class TravelScheduleAdapter : PagingDataAdapter<TravelSchedule, TravelScheduleAdapter.ViewHolder>(TravelScheduleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.itemschedule, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val schedule = getItem(position)
        schedule?.let {
            holder.bind(it)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTempatAsal: TextView = itemView.findViewById(R.id.tv_stasiun1)
        private val tvTempatTujuan: TextView = itemView.findViewById(R.id.tv_stasiun2)
        private val tvWaktuBerangkat: TextView = itemView.findViewById(R.id.tv_waktuBerangkat)
        private val tvWaktu: TextView = itemView.findViewById(R.id.tv_waktu)
        private val tvBiaya: TextView = itemView.findViewById(R.id.tv_biaya)
        private val tv_transportasi: TextView = itemView.findViewById(R.id.tv_transportasi)
        private val tv_tanggal: TextView = itemView.findViewById(R.id.tv_tanggal)


        fun bind(schedule: TravelSchedule) {
            tvTempatAsal.text = schedule.tempatAsal
            tvTempatTujuan.text = schedule.tempatTujuan
            tv_tanggal.text = schedule.tanggal

            val waktuBerangkat = schedule.waktuBerangkat
            val waktuSampai = schedule.waktuSampai

            val firstJamBerangkat = waktuBerangkat.toDouble()
            val lastJamSampai = waktuSampai.toDouble()

            val formattedTimeBerangkat = formatTime(waktuBerangkat)
            tvWaktuBerangkat.text = "$formattedTimeBerangkat WIB"

            Log.d("kgkg", "tvTempatAsal " +schedule.docId)
            Log.d("kgkg", "tvTempatAsal " +schedule.tempatAsal)
            Log.d("kgkg", "tvTempatTujuan " +schedule.tempatTujuan)
            Log.d("kgkg", "firstJamBerangkat $firstJamBerangkat")
            Log.d("kgkg", "formattedTimeBerangkat $formattedTimeBerangkat")

//            val formattedTimeSampai = formatTime(waktuSampai)
//            tvWaktuSampai.text = "$formattedTimeSampai WIB"

            val (hours, minutes) = calculateTimeDifference(firstJamBerangkat, lastJamSampai)

            if (hours != 0) {
                tvWaktu.text = "$hours Jam ${if (minutes != 0) "$minutes Menit" else ""}"
            } else {
                tvWaktu.text = "$minutes Menit"
            }

            val formattedBiaya = "Rp " + NumberFormat.getNumberInstance(Locale("id", "ID")).format(schedule.biaya)
            tvBiaya.text = formattedBiaya

            tv_transportasi.text = schedule.idTranspor
        }

        fun formatTime(time: Int): String {
            val timeStr = time.toString().padStart(4, '0')
            val hours = timeStr.substring(0, 2).toInt()
            val minutes = timeStr.substring(2).toInt()
            return String.format("%02d:%02d", hours, minutes)
        }

        // Function to calculate the time difference in hours and minutes
        fun calculateTimeDifference(start: Double, end: Double): Pair<Int, Int> {
            val startStr = formatTime(start.toInt())
            val endStr = formatTime(end.toInt())

            val originalFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val dateStart = originalFormat.parse(startStr)
            val dateEnd = originalFormat.parse(endStr)

            val diff = dateEnd.time - dateStart.time
            val diffMinutes = (diff / (60 * 1000)).toInt()
            val hours = diffMinutes / 60
            val minutes = diffMinutes % 60

            return Pair(hours, minutes)
        }
    }

}

class TravelScheduleDiffCallback : DiffUtil.ItemCallback<TravelSchedule>() {
    override fun areItemsTheSame(oldItem: TravelSchedule, newItem: TravelSchedule): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: TravelSchedule, newItem: TravelSchedule): Boolean {
        return oldItem == newItem
    }
}
