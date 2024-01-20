package indep.projects.micro_tasks
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.core.content.res.ResourcesCompat
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.DefaultGasProvider
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.utils.Numeric
import java.math.BigInteger
import org.web3j.abi.datatypes.Function

class TokenInformation : ComponentActivity() {
    private var coinList = ArrayList<String>()
    private val coinMap = mutableMapOf<String,String>()
    private lateinit var web3j: Web3j
    private lateinit var credentials: Credentials
    private lateinit var gasProvider: DefaultGasProvider
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.all_tokens)
        coinMap["USDC"] = "0x94a9D9AC8a22534E3FaCa9F4e7F2E2cf85d5E4C8"
        coinMap["GHO"] = "0xc4bF5CbDaBE595361438F8c6a187bDc330539c60"
        coinMap["USDT"] = "0xaA8E23Fb1079EA71e0a56F48a2aA51851D8433D0"
        coinMap["DAI"] = "0xFF34B3d4Aee8ddCd6F9AFFFB6Fe49bD371b8a357"
        coinMap["EURS"] = "0x6d906e526a4e2Ca02097BA9d0caA3c382F52278E"
        val tokensLayout = findViewById<LinearLayout>(R.id.tokens)
        val fontTypeFace = ResourcesCompat.getFont(this,R.font.candylaneregular)
        coinList.add("USDC")
        coinList.add("GHO")
        coinList.add("USDT")
        coinList.add("DAI")
        coinList.add("EURS")

        coinList.forEach { name ->
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val button = Button(this)
            button.text = name
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f)
            button.setBackgroundResource(R.drawable.coin_background)
            params.setMargins(0,20,0,20)
            params.height = 380
            button.layoutParams = params
            button.typeface = fontTypeFace
            button.setOnClickListener{
                val intent = Intent(this, IndividualToken::class.java)
                intent.putExtra("button_text", button.text.toString())
                intent.putExtra("balance", handleWalletInfo("","", button.text.toString()).toString())
                startActivity(intent)
            }
            tokensLayout.addView(button)
        }
    }

    private fun handleWalletInfo(privateKey: String, address: String, coin: String): BigInteger? {
        // Your Ethereum account private key
//        val privateKey = "private-key"
        credentials =
            Credentials.create("private-key")
        // Define the gas provider
        gasProvider = DefaultGasProvider()
        // Initialize the Web3j service
        web3j =
            Web3j.build(HttpService("https://sepolia.infura.io/v3/be42d95bb60642ee9c8d0dc13770b31e"))
        // Address you are checking for
//        val address = "0xab83E0071A4894Ce5464378de41cd9eC8A2037fB"
        val balance =
            getTokenBalance("0xab83E0071A4894Ce5464378de41cd9eC8A2037fB", coinMap[coin]!!)
        println("Balance: $balance")
        return balance.divide(BigInteger.valueOf(100))
    }

    fun getTokenBalance(address: String, contractAddress: String ): BigInteger {
        // Contract of specific coin/token
//        val contractAddress = "0xc4bF5CbDaBE595361438F8c6a187bDc330539c60"
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