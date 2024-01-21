package indep.projects.crypto_wallet

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.crypto.Credentials
import org.web3j.tx.gas.DefaultGasProvider


class MainActivity : ComponentActivity() {
    private lateinit var web3j: Web3j
    private lateinit var credentials: Credentials
    private lateinit var gasProvider: DefaultGasProvider
    private val myMap = mutableMapOf<String,String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myMap["0xab83E0071A4894Ce5464378de41cd9eC8A2037fB"] = "insert-private-key"
        val enterButton: Button = findViewById(R.id.enter_wallet_button)
        enterButton.setOnClickListener{
            val editTextPrivateKey: EditText = findViewById(R.id.private_key_input)
            val privateKey = editTextPrivateKey.text.toString()
            val editTextAddress: EditText = findViewById(R.id.wallet_address_input)
            val address = editTextAddress.text.toString()
            handleWalletInfo(privateKey,address)
        }
    }

    private fun handleWalletInfo(privateKey: String, address: String) {
        if(isValidHexadecimal(privateKey)) {
            // Your Ethereum account private key
            credentials = Credentials.create(privateKey)
            // Define the gas provider
            gasProvider = DefaultGasProvider()
            // Initialize the Web3j service
            web3j =
                Web3j.build(HttpService("https://sepolia.infura.io/v3/be42d95bb60642ee9c8d0dc13770b31e"))
            if (myMap[address] == privateKey) {
                val tokenInformationIntent = Intent(this, TokenInformation::class.java)
                tokenInformationIntent.putExtra("private-key", privateKey)
                tokenInformationIntent.putExtra("address", address)
                startActivity(tokenInformationIntent)
            } else {
                Toast.makeText(this, "We could not find that wallet", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(this, "Enter a valid private key", Toast.LENGTH_SHORT).show()
        }
    }
//    checks if private key is a valid private key
    private fun isValidHexadecimal(inputString: String): Boolean {
        val hexPattern = Regex("[0-9a-fA-F]+")
        return hexPattern.matches(inputString)
    }
}
