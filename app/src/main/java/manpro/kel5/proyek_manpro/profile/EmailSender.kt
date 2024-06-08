package manpro.kel5.proyek_manpro.profile

import android.os.AsyncTask
import android.util.Log
import java.util.Properties
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class EmailSender(private val email: String, private val subject: String, private val message: String) {

    fun send() {
        SendEmailTask().execute()
    }

    private inner class SendEmailTask : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            try {
                val properties = Properties()
                properties["mail.smtp.auth"] = "true"
                properties["mail.smtp.starttls.enable"] = "true"
                properties["mail.smtp.host"] = "smtp.gmail.com"
                properties["mail.smtp.port"] = "587"

                val session = Session.getInstance(properties, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        // Use your Gmail email and app-specific password here
                        return PasswordAuthentication("manproakun@gmail.com", "zjjl lbuu pwwt feyu")
                    }
                })

                val mimeMessage = MimeMessage(session)
                mimeMessage.setFrom(InternetAddress("manproakun@gmail.com"))
                mimeMessage.addRecipient(Message.RecipientType.TO, InternetAddress(email))
                mimeMessage.subject = subject
                mimeMessage.setText(message)

                Transport.send(mimeMessage)
            } catch (e: MessagingException) {
                e.printStackTrace()
                Log.e("EmailSender", "Error sending email: ${e.message}")
            }
            return null
        }
    }
}
