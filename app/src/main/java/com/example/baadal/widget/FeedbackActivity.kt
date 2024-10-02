//package com.example.baadal.widget
//
//import android.content.Context
//import android.net.ConnectivityManager
//import android.os.Bundle
//import android.util.Log
//import android.widget.Toast
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.baadal.databinding.ActivityFeedbackBinding

//import com.example.baadal.databinding.ActivityFeedbackBinding
//import com.example.baadal.screen.MainActivity
//import javax.mail.Authenticator
//import javax.mail.Message
//import javax.mail.PasswordAuthentication
//import javax.mail.Session
//import javax.mail.Transport
//import javax.mail.internet.InternetAddress
//import javax.mail.internet.MimeMessage
//
class FeedbackActivity : AppCompatActivity() {
//
    lateinit var binding: ActivityFeedbackBinding
//
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
//        binding = ActivityFeedbackBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        supportActionBar?.title = "Feedback"
//
//        binding.sendFA.setOnClickListener {
//            val msg = binding.feedbackMsgFA.text.toString() + "\n" + binding.emailFA.text.toString()
//            val subject = binding.topicFA.text.toString()
//            val userName = "modhshubhr53@gmail.com"
//            val pass = "qmgcuecqyrwoyoha"
//            val cManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//            if (msg.isNotEmpty() && subject.isNotEmpty() && (cManager.activeNetworkInfo?.isConnectedOrConnecting == true)) {
//                Thread {
//                    try {
//                        val properties = java.util.Properties()
//                        properties["mail.smtp.auth"] = "true"
//                        properties["mail.smtp.starttls.enable"] = "true"
//                        properties["mail.smtp.host"] = "smtp.gmail.com"
//                        properties["mail.smtp.port"] = "587"
//
//                        val session = Session.getInstance(properties, object : Authenticator() {
//                            override fun getPasswordAuthentication(): PasswordAuthentication {
//                                return PasswordAuthentication(userName, pass)
//                            }
//                        })
//
//                        val mail = MimeMessage(session)
//                        mail.subject = subject
//                        mail.setText(msg)
//                        mail.setFrom(InternetAddress(userName))
//                        mail.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userName))
//                        Transport.send(mail)
//                    } catch (e: Exception) { Log.e("Mail error", e.toString()) }
//                }.start()
//                Toast.makeText(this, "Thanks For your Feedback!!", Toast.LENGTH_SHORT).show()
//                finish()
//            }
//            else Toast.makeText(this, "Something Went Wrong!!", Toast.LENGTH_SHORT).show()
//        }
    }
}