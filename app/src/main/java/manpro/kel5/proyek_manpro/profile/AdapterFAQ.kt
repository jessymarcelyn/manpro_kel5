package manpro.kel5.proyek_manpro.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import manpro.kel5.proyek_manpro.R

class ItemAdapter(private val itemList: List<ItemFAQ>, private val listener: OnItemClickListener) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    // ViewHolder class
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val tvQuestion: TextView = itemView.findViewById(R.id.tv_q)
        val imageView: ImageView = itemView.findViewById(R.id.imageView25)

        init {
            // Set click listener on the whole item view
            itemView.setOnClickListener(this)
        }

        // Handle click event
        override fun onClick(view: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                // Notify the listener about the item click
                listener.onItemClick(itemList[position])
            }
        }
    }

    // Create ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.itemfaq, parent, false)
        return ViewHolder(view)
    }

    // Bind data to ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.tvQuestion.text = item.question
    }

    // Return the number of items in the list
    override fun getItemCount(): Int = itemList.size
}

// Interface to handle item clicks
interface OnItemClickListener {
    fun onItemClick(item: ItemFAQ)
}
