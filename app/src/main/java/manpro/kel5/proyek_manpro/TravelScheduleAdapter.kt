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

        fun bind(schedule: TravelSchedule) {
            tvTempatAsal.text = schedule.tempatAsal
            tvTempatTujuan.text = schedule.tempatTujuan


            val originalFormat = SimpleDateFormat("HHmm", Locale.getDefault())
            val targetFormat = SimpleDateFormat("HH.mm", Locale.getDefault())

            val waktuBerangkat = schedule.waktuBerangkat
            val date = originalFormat.parse(waktuBerangkat)
            val formattedTime = targetFormat.format(date)
            tvWaktuBerangkat.text = formattedTime + " WIB"

            val firstJamBerangkat = schedule.waktuBerangkat.toDouble()
            val lastJamSampai = schedule.waktuSampai.toDouble()
            Log.d("ijij", "schedule.waktuBerangkat $firstJamBerangkat")
            Log.d("ijij", "schedule.waktuSampai $lastJamSampai")

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

        fun convertToMinutes(time: Double): Int {
            val timeString = String.format("%04d", time.toInt())
            val hours = timeString.substring(0, 2).toInt()
            val minutes = timeString.substring(2).toInt()
            return hours * 60 + minutes
        }

        // Calculate the difference between two times and return the result as hours and minutes
        fun calculateTimeDifference(start: Double, end: Double): Pair<Int, Int> {
            val startMinutes = convertToMinutes(start)
            val endMinutes = convertToMinutes(end)
            val totalMinutes = if (endMinutes >= startMinutes) {
                endMinutes - startMinutes
            } else {
                // Handle case when the end time is on the next day
                endMinutes + (24 * 60) - startMinutes
            }

            val hours = totalMinutes / 60
            val minutes = totalMinutes % 60
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
