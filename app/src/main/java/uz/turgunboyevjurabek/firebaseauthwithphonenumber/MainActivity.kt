package uz.turgunboyevjurabek.firebaseauthwithphonenumber

import android.app.Activity
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import uz.turgunboyevjurabek.firebaseauthwithphonenumber.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(){
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var auth:FirebaseAuth
    lateinit var storedVerificationId:String
    lateinit var resentToken:PhoneAuthProvider.ForceResendingToken
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth=FirebaseAuth.getInstance()

        binding.btnOk.setOnClickListener {
            sendVerificationCode(binding.edtNumber.text.toString())
        }

        binding.edtPassword.addTextChangedListener {
            verifyCode()
        }


    }

    fun sendVerificationCode(phoneNumber: String){
        val options=PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L,TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callback)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    private val callback=object :PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            Log.d(TAG, "onVerificationCompleted: Uraaa")
            Toast.makeText(this@MainActivity, "callback", Toast.LENGTH_SHORT).show()
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            Toast.makeText(this@MainActivity, "No callback ${p0.message}", Toast.LENGTH_LONG).show()
            Log.d(TAG, "onVerificationCompleted:Failed",p0)
        }
        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
            Log.d(TAG, "onCodeSent: Kod jo'natilidi")
            storedVerificationId=p0
            resentToken=p1
        }

    }

    private fun verifyCode(){
        val code=binding.edtPassword.text.toString()
        if (code.length==6){
            val credential=PhoneAuthProvider.getCredential(storedVerificationId,code)
            signInWithPhoneAuthCredential(credential)

        }
    }
    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential){
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) {
                if (it.isSuccessful){
                    Toast.makeText(this, "Mufaqqiyatli", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "Mufaqiyatsiz", Toast.LENGTH_SHORT).show()
                    if (it.exception is FirebaseAuthInvalidCredentialsException){
                        Toast.makeText(this, "Kod hato kiritildi tekshirib qayta kiriting", Toast.LENGTH_SHORT).show()
                    }

                }
            }
    }
}