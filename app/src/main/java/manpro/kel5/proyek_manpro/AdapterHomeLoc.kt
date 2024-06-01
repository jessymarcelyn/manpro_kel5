package manpro.kel5.proyek_manpro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterHomeLoc(
    private val listNama: MutableList<String>
) : RecyclerView.Adapter<AdapterHomeLoc.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: String, index : Int)
        fun delData(pos: Int)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTujuan2: TextView = itemView.findViewById(R.id.tv_tujuannn2)
        var imgRemove: ImageView = itemView.findViewById(R.id.img_removee)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_tujuann, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listNama.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val nama = listNama[position]
        holder.tvTujuan2.text = nama
        val index = listNama.indexOf(nama)

        holder.tvTujuan2.setOnClickListener {
            onItemClickCallback.onItemClicked(listNama[position], index)
        }

        holder.imgRemove.setOnClickListener {
                onItemClickCallback.delData(position)

        }
    }
}
