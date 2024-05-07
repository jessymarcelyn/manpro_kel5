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

class adapterRouteDetail (
    private val listStop: ArrayList<String>,

//    ,
//    private val context: Context
): RecyclerView.Adapter<adapterRouteDetail.ListViewHolder>(){

    inner class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.list_selectroute,parent,false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listStop.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var rute = listStop[position]


    }

}