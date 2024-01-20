package indep.projects.micro_tasks

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.crypto.Credentials
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.tx.gas.DefaultGasProvider
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.utils.Numeric
import java.math.BigInteger
import org.web3j.abi.datatypes.Function


class MainActivity : ComponentActivity() {
    private lateinit var web3j: Web3j
    private lateinit var credentials: Credentials
    private lateinit var gasProvider: DefaultGasProvider
    private val myMap = mutableMapOf<String,String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myMap["address"] = "private_key"
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
        // Your Ethereum account private key
//        val privateKey = "df765cea3f493f30022a8d87966001701159711a1b9444dfef4dd07920e60a82"
        credentials = Credentials.create("df765cea3f493f30022a8d87966001701159711a1b9444dfef4dd07920e60a82")
        // Define the gas provider
        gasProvider = DefaultGasProvider()
        // Initialize the Web3j service
        web3j = Web3j.build(HttpService("https://sepolia.infura.io/v3/be42d95bb60642ee9c8d0dc13770b31e"))
        // Address you are checking for
//        val address = "0xab83E0071A4894Ce5464378de41cd9eC8A2037fB"
        val balance = getTokenBalance("0xab83E0071A4894Ce5464378de41cd9eC8A2037fB")
        println("Balance: $balance")
        if(myMap[address] == privateKey){
            val tokenInformationIntent = Intent(this,TokenInformation::class.java)
            startActivity(tokenInformationIntent)
        }
        else{
            Toast.makeText(this,"We could not find that wallet",Toast.LENGTH_SHORT).show()
        }
    }

    fun getTokenBalance(address: String): BigInteger {
        // Contract of specific coin/token
        val contractAddress = "0xc4bF5CbDaBE595361438F8c6a187bDc330539c60"
        val function = Function(
            "balanceOf",
            listOf(Address(address)),
            listOf(object : TypeReference<Uint256>() {})
        )
        val encodedFunction = FunctionEncoder.encode(function)

        try {
            val response = web3j.ethCall(
                Transaction.createEthCallTransaction(
                    "0x0000000000000000000000000000000000000000", contractAddress, encodedFunction
                ),
                DefaultBlockParameterName.LATEST
            ).sendAsync().get()

                if (response.hasError()) {
                val errorMessage = "Error executing ethCall: ${response.error.message}"
                println(errorMessage)
                throw Exception(errorMessage)
            }

            val result = response.value
            if (result != null && result.startsWith("0x")) {
                return Numeric.decodeQuantity(result)
            } else {
                val errorMessage = "Invalid response from ethCall: $result"
                println(errorMessage)
                throw Exception(errorMessage)
            }
        } catch (e: Exception) {
            val error = "Exception during ethCall: ${e.message}"
            println(error)
            throw Exception(error, e)
        }
    }
}
