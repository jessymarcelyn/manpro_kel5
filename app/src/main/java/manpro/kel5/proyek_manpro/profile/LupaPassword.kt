package manpro.kel5.proyek_manpro.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import manpro.kel5.proyek_manpro.Home
import manpro.kel5.proyek_manpro.R
import manpro.kel5.proyek_manpro.R.*
import kotlin.random.Random

class LupaPassword : AppCompatActivity() {

    private lateinit var emailLupaPassword : TextInputEditText
    private lateinit var buttonKirimOTP : Button
    private lateinit var dialog: AlertDialog
    private lateinit var otp : String
    private lateinit var oldPassword: String

    private lateinit var keLogin: TextView
    private lateinit var keDaftarAkun : TextView

    private val db = FirebaseFirestore.getInstance()
    private val auth  = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_lupa_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        emailLupaPassword = findViewById(id.ti_email_lupa_pass)
        buttonKirimOTP = findViewById(id.btn_kirim_otp)
        keLogin = findViewById(R.id.tv_kembali_ke_login)
        keDaftarAkun = findViewById(R.id.tv_daftar)

        buttonKirimOTP.setOnClickListener {
            // send otp to email
            val email = emailLupaPassword.text.toString().trim()
            if (email.isNotEmpty()) {
                otp = generateOTP()
                val subject = "Your OTP Code"
                val message = "Your OTP code is $otp"
                EmailSender(email, subject, message).send()
                Toast.makeText(this, "OTP Sent", Toast.LENGTH_SHORT).show()
                showOTPDialog(email)
            } else {
                Toast.makeText(this, "Email Empty", Toast.LENGTH_SHORT).show()
            }
        }

        keLogin.setOnClickListener{
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        keDaftarAkun.setOnClickListener{
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

    }

    private fun generateOTP(): String {
        val otp = StringBuilder()
        for (i in 0 until 6) {  // generate a 6-digit OTP
            otp.append(Random.nextInt(10))
        }
        return otp.toString()
    }

    private fun showOTPDialog(email: String) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(layout.otp_popup, null)

        builder.setView(dialogLayout)
        dialog = builder.create()
        dialog.show()

        // Declare ID from otp_popup.xml
        val angkaOTP1 = dialogLayout.findViewById<EditText>(R.id.editTextNumber)
        val angkaOTP2 = dialogLayout.findViewById<EditText>(R.id.editTextNumber2)
        val angkaOTP3 = dialogLayout.findViewById<EditText>(R.id.editTextNumber3)
        val angkaOTP4 = dialogLayout.findViewById<EditText>(R.id.editTextNumber4)
        val angkaOTP5 = dialogLayout.findViewById<EditText>(R.id.editTextNumber5)
        val angkaOTP6 = dialogLayout.findViewById<EditText>(R.id.editTextNumber6)

        var buttonVerifikasiOTP = dialogLayout.findViewById<Button>(R.id.btn_verifikasi_otp)

        val emailTextView = dialogLayout.findViewById<TextView>(id.tv_show_email_verifikasi_otp)
        emailTextView.text = email



        buttonVerifikasiOTP.setOnClickListener {
            val otpInput = "${angkaOTP1.text}${angkaOTP2.text}${angkaOTP3.text}${angkaOTP4.text}${angkaOTP5.text}${angkaOTP6.text}"
            if (otp == otpInput){
                dialog.dismiss()
                showNewPasswordDialog(email)
            }
            else {
                Toast.makeText(this, "OTP false", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showNewPasswordDialog(email: String) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(layout.new_password_otp, null)

        builder.setView(dialogLayout)
        dialog = builder.create()
        dialog.show()

        // Declare ID
        val newPasswordTI = dialogLayout.findViewById<TextInputEditText>(R.id.ti_email_lupa_pass)
        val buttonGantiPassword = dialogLayout.findViewById<Button>(R.id.btn_kirim_otp)

        signInWithEmail(email)

        buttonGantiPassword.setOnClickListener {
            val newPassword = newPasswordTI.text.toString()
            auth.currentUser?.updatePassword(newPassword)

            db.collection("User")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val userRef = documents.first().reference
                        userRef.update("password",newPassword)
                        val intent = Intent(this, Home::class.java)
                        startActivity(intent)

                    } else {
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun signInWithEmail(email: String){
        db.collection("User")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val userRef = documents.first().toObject(User::class.java)
                    val password = userRef.password
                    auth.signInWithEmailAndPassword(email, password)
                } else {
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
