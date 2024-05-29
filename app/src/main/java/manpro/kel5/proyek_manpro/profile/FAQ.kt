package manpro.kel5.proyek_manpro.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import manpro.kel5.proyek_manpro.R

class FAQ : AppCompatActivity(), OnItemClickListener {

    private lateinit var back_button: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var itemList: MutableList<ItemFAQ>
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_faq)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.rv_FAQ)
        recyclerView.layoutManager = LinearLayoutManager(this)

        itemList = mutableListOf()
        getQuestionFAQ()

        itemAdapter = ItemAdapter(itemList, this)
        recyclerView.adapter = itemAdapter

        back_button = findViewById(R.id.btn_back)
        back_button.setOnClickListener{
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }


    }

    private fun getQuestionFAQ() {
        // Fetch Database
        db.collection("FAQ")
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        val pertanyaan = document.getString("pertanyaan").toString()
                        itemList.add(ItemFAQ(pertanyaan))
                    }
                    // Notify the adapter that the data has changed
                    itemAdapter.notifyDataSetChanged()
                    Log.d("FAQ", "Fetched ${itemList.size} items")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FAQ", "Error fetching documents: ", exception)
            }
    }

    override fun onItemClick(item: ItemFAQ) {
        // Handle item click here
        // Start DetailFAQ activity and pass question as extra
        val intent = Intent(this, DetailFAQ::class.java)
        intent.putExtra("question", item.question)
        startActivity(intent)
        Log.d("Faq", "Question sent: ${item.question}")
    }
}