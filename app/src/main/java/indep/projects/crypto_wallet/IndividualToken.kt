package indep.projects.crypto_wallet

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class IndividualToken : ComponentActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.token_information)
        val balanceTextView = findViewById<TextView>(R.id.balance)
        balanceTextView.text = "Balance: ${intent.getStringExtra("balance")} coins"

        val tokenTextView = findViewById<TextView>(R.id.title)
        tokenTextView.text = intent.getStringExtra("button_text")

        val descriptionTextView = findViewById<TextView>(R.id.information_about_coin)
        descriptionTextView.text = intent.getStringExtra("description")

        val bottomBar : BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomBar.selectedItemId = R.id.tokens
        bottomBar.setOnItemSelectedListener { item ->
            when (item.itemId){
                R.id.tokens -> {
                    val intent = Intent(this, TokenInformation::class.java)
                    startActivity(intent)
                    true
                }
                R.id.sign_out -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}