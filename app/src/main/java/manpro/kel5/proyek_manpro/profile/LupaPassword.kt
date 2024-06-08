package manpro.kel5.proyek_manpro.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import manpro.kel5.proyek_manpro.R
import kotlin.random.Random

class LupaPassword : AppCompatActivity() {

    private lateinit var emailLupaPassword : TextInputEditText
    private lateinit var buttonKirimOTP : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lupa_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        emailLupaPassword = findViewById(R.id.ti_email_lupa_pass)
        buttonKirimOTP = findViewById(R.id.btn_kirim_otp)

        buttonKirimOTP.setOnClickListener {
            // send otp to email
            val email = emailLupaPassword.text.toString().trim()
            if (email.isNotEmpty()) {
                val otp = generateOTP()
                val subject = "Your OTP Code"
                val message = "Your OTP code is $otp"
                EmailSender(email, subject, message).send()
            } else {
                Toast.makeText(this, "Email Empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateOTP(): String {
        val otp = StringBuilder()
        for (i in 0 until 6) {  // generate a 6-digit OTP
            otp.append(Random.nextInt(10))
        }
        return otp.toString()
    }
}
