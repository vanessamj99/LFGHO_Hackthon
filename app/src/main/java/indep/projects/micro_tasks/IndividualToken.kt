package indep.projects.micro_tasks

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity

class IndividualToken : ComponentActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.token_information)
        val balanceTextView = findViewById<TextView>(R.id.balance)
        balanceTextView.text = "Balance: ${intent.getStringExtra("balance")}"

        val tokenTextView = findViewById<TextView>(R.id.title)
        tokenTextView.text = intent.getStringExtra("button_text")
    }
}