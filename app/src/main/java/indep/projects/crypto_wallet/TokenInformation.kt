package indep.projects.crypto_wallet
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
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
    private var descriptions = mutableMapOf<String,String>()
    private val coinMap = mutableMapOf<String,String>()
    private lateinit var web3j: Web3j
    private lateinit var credentials: Credentials
    private lateinit var gasProvider: DefaultGasProvider
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.all_tokens)

        initializeData()
        val tokensLayout = findViewById<LinearLayout>(R.id.tokens)
        val fontTypeFace = ResourcesCompat.getFont(this,R.font.candylaneregular)

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
                intent.putExtra("balance", handleWalletInfo("","0xab83E0071A4894Ce5464378de41cd9eC8A2037fB", button.text.toString()).toString())
                intent.putExtra("description",descriptions[button.text.toString()])
                startActivity(intent)
            }
            tokensLayout.addView(button)
        }
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

    private fun initializeData(){
        coinMap["USDC"] = "0x94a9D9AC8a22534E3FaCa9F4e7F2E2cf85d5E4C8"
        descriptions["USDC"] = "USDC is a stablecoin redeemable on a 1:1 basis for US dollars, backed by dollar denominated assets held in segregated accounts with US regulated financial institutions."
        coinMap["GHO"] = "0xc4bF5CbDaBE595361438F8c6a187bDc330539c60"
        descriptions["GHO"] = "GHO is a decentralized multi-collateral stablecoin that is fully backed, transparent and native to the Aave Protocol."
        coinMap["USDT"] = "0xaA8E23Fb1079EA71e0a56F48a2aA51851D8433D0"
        descriptions["USDT"] = "Tether (USDT) is an Ethereum token that is pegged to the value of a U.S. dollar (also known as a stablecoin). Tether’s issuer claims that USDT is backed by bank reserves and loans which match or exceed the value of USDT in circulation."
        coinMap["DAI"] = "0xFF34B3d4Aee8ddCd6F9AFFFB6Fe49bD371b8a357"
        descriptions["DAI"] = "Dai (DAI) is a decentralized stablecoin running on Ethereum (ETH) that attempts to maintain a value of \$1.00 USD. Unlike centralized stablecoins, Dai isn't backed by US dollars in a bank account. Instead, it’s backed by collateral on the Maker platform."
        coinMap["EURS"] = "0x6d906e526a4e2Ca02097BA9d0caA3c382F52278E"
        descriptions["EURS"] = "EURS is a digital euro, or stablecoin, designed by the platform to mirror the price of the euro. The EURS stablecoin aims to offer the benefits of the combination of reputation and relative stability of the euro and blockchain technology."
        coinList.add("USDC")
        coinList.add("GHO")
        coinList.add("USDT")
        coinList.add("DAI")
        coinList.add("EURS")
    }

    private fun handleWalletInfo(privateKey: String, address: String, coin: String): BigInteger? {
        credentials =
            Credentials.create(privateKey)
        // Define the gas provider
        gasProvider = DefaultGasProvider()
        // Initialize the Web3j service
        web3j =
            Web3j.build(HttpService("https://sepolia.infura.io/v3/be42d95bb60642ee9c8d0dc13770b31e"))
        val balance =
            getTokenBalance(address, coinMap[coin]!!)
        return balance.divide(BigInteger.valueOf(100))
    }

    private fun getTokenBalance(address: String, contractAddress: String ): BigInteger {
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