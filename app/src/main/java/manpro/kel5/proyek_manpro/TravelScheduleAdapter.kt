package manpro.kel5.proyek_manpro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.*

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

        fun bind(schedule: TravelSchedule) {
            tvTempatAsal.text = schedule.tempatAsal
            tvTempatTujuan.text = schedule.tempatTujuan
            val formattedTime = String.format(Locale.getDefault(), "%02d:%02d WIB", schedule.waktu / 60, schedule.waktu % 60)
            tvWaktuBerangkat.text = formattedTime
            tvWaktu.text = schedule.waktu.toString()
            val formattedBiaya = NumberFormat.getCurrencyInstance().format(schedule.biaya)
            tvBiaya.text = formattedBiaya
        }
    }
}

class TravelScheduleDiffCallback : DiffUtil.ItemCallback<TravelSchedule>() {
    override fun areItemsTheSame(oldItem: TravelSchedule, newItem: TravelSchedule): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TravelSchedule, newItem: TravelSchedule): Boolean {
        return oldItem == newItem
    }
}
