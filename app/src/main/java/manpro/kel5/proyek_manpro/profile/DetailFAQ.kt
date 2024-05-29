package manpro.kel5.proyek_manpro.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import manpro.kel5.proyek_manpro.R

class DetailFAQ : AppCompatActivity() {
    private lateinit var back_button: ImageView
    private lateinit var judul_pertanyaan : TextView
    private lateinit var jawaban : TextView
    private val db = FirebaseFirestore.getInstance()
    private lateinit var question: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_faq)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        back_button = findViewById(R.id.btn_back)
        judul_pertanyaan = findViewById(R.id.tv_judul_pertanyaan)
        jawaban = findViewById(R.id.tv_answer)

        // Retrieve question from intent extra
        question = intent.getStringExtra("question").toString()
        Log.d("DetailFAQ", "Received question: $question")
        judul_pertanyaan.text = question
        getAnswer()

        back_button.setOnClickListener {
            val intent = Intent(this, FAQ::class.java)
            startActivity(intent)
        }

    }

    private fun getAnswer() {
        db.collection("FAQ")
            .whereEqualTo("pertanyaan", question)
            .get()
            .addOnSuccessListener {documents ->
                if(!documents.isEmpty){
                    for(document in documents){
                        val jawabanFAQ = document.getString("jawaban")
                        jawaban.text = jawabanFAQ
                    }
                }
            }
    }
}