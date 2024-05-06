package manpro.kel5.proyek_manpro

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SelectRuteDetail : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_select_rute_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val dataIntent = intent.getParcelableExtra<Rute>("kirimData")
        Log.d("jhjh", dataIntent.toString())

        var _tvAsal = findViewById<TextView>(R.id.tvAsal)
        var _tvTujuan = findViewById<TextView>(R.id.tvTujuan)
        var _tvDurasi = findViewById<TextView>(R.id.tvDurasi)
        var _tvHarga = findViewById<TextView>(R.id.tvHarga)
        var _tvJamBerangkat = findViewById<TextView>(R.id.tvJamBerangkat)
        var _tvJamSampai = findViewById<TextView>(R.id.tvJamSampai)
        var _btn_back2 = findViewById<ImageView>(R.id.btn_back2)

        var totalBiaya = 0;
        var totalDetik = 0;
        if (dataIntent != null) {
            for(listBiaya in dataIntent.biaya){
                totalBiaya += listBiaya
            }
            for(listDetik in dataIntent.durasi){
                totalDetik += listDetik
            }
            val menit = totalDetik / 60
            _tvAsal.text = dataIntent.nama_source[0]
            _tvTujuan.text = dataIntent.nama_dest.last()
            _tvDurasi.text = menit.toString()
            _tvHarga.text = totalBiaya.toString()

            var formattedTime = dataIntent.jam_berangkat[0]
            formattedTime = formattedTime.substring(0, 2) + "." + formattedTime.substring(2)
            _tvJamBerangkat.text = formattedTime

            formattedTime = dataIntent.jam_sampai.last()
            formattedTime = formattedTime.substring(0, 2) + "." + formattedTime.substring(2)
            _tvJamSampai.text = formattedTime

        }

        _btn_back2.setOnClickListener{
            finish()
        }






    }
}