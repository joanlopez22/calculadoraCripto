package com.example.calculadoracripto
import com.google.android.material.snackbar.Snackbar
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var display: EditText
    private lateinit var resultView: EditText
    private lateinit var bitcoinIcon: ImageView
    private lateinit var ethereumIcon: ImageView
    private lateinit var cardanoIcon: ImageView // Afegit per Cardano
    private lateinit var solanaIcon: ImageView // Afegit per Solana
    private lateinit var clearButton: Button
    private lateinit var eraseButton: Button
    private lateinit var decimalButton: Button
    private val BASE_URL = "https://api.coingecko.com/api/v3/"
    private lateinit var retrofit: Retrofit
    private lateinit var cryptoApi: CryptoApi
    private var currentBtcPrice: Double? = null
    private var currentEthPrice: Double? = null
    private var currentAdaPrice: Double? = null // Preu de Cardano
    private var currentSolPrice: Double? = null // Preu de Solana
    private var selectedCryptoPrice: Double? = null

    interface CryptoApi {
        @GET("simple/price")
        fun getCryptoPrice(
            @Query("ids") ids: String,
            @Query("vs_currencies") vs: String = "usd"
        ): Call<Map<String, Map<String, Double>>>
    }

    private fun fetchCryptoPrices() {
        val bitcoinCall = cryptoApi.getCryptoPrice("bitcoin")
        val ethereumCall = cryptoApi.getCryptoPrice("ethereum")
        val cardanoCall = cryptoApi.getCryptoPrice("cardano") // Petició per Cardano
        val solanaCall = cryptoApi.getCryptoPrice("solana") // Petició per Solana

        bitcoinCall.enqueue(object : Callback<Map<String, Map<String, Double>>> {
            override fun onResponse(
                call: Call<Map<String, Map<String, Double>>>,
                response: Response<Map<String, Map<String, Double>>>
            ) {
                if (response.isSuccessful) {
                    currentBtcPrice = response.body()?.get("bitcoin")?.get("usd")
                    if (currentBtcPrice != null) {
                        showPriceDialog("Bitcoin", currentBtcPrice!!)
                    } else {
                        Snackbar.make(display, "Error: Bitcoin price not available.", Snackbar.LENGTH_SHORT).show()
                    }
                } else {
                    Snackbar.make(display, "Error: Bitcoin price not available.", Snackbar.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Map<String, Double>>>, t: Throwable) {
                Snackbar.make(display, "Connection error.", Snackbar.LENGTH_SHORT).show()
            }
        })

        ethereumCall.enqueue(object : Callback<Map<String, Map<String, Double>>> {
            override fun onResponse(
                call: Call<Map<String, Map<String, Double>>>,
                response: Response<Map<String, Map<String, Double>>>
            ) {
                if (response.isSuccessful) {
                    currentEthPrice = response.body()?.get("ethereum")?.get("usd")
                    if (currentEthPrice != null) {
                        showPriceDialog("Ethereum", currentEthPrice!!)
                    } else {
                        Toast.makeText(this@MainActivity, "Error: Ethereum price not available.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Snackbar.make(display, "Error: Ethereum price not available.", Snackbar.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Map<String, Double>>>, t: Throwable) {
                Snackbar.make(display, "Connection error.", Snackbar.LENGTH_SHORT).show()
            }
        })

        cardanoCall.enqueue(object : Callback<Map<String, Map<String, Double>>> {
            override fun onResponse(
                call: Call<Map<String, Map<String, Double>>>,
                response: Response<Map<String, Map<String, Double>>>
            ) {
                if (response.isSuccessful) {
                    currentAdaPrice = response.body()?.get("cardano")?.get("usd")
                    if (currentAdaPrice != null) {
                        showPriceDialog("Cardano", currentAdaPrice!!)
                    } else {
                        Toast.makeText(this@MainActivity, "Error: Cardano price not available.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Snackbar.make(display, "Error fetching Cardano price.", Snackbar.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Map<String, Double>>>, t: Throwable) {
                Snackbar.make(display, "Connection error.", Snackbar.LENGTH_SHORT).show()
            }
        })

        solanaCall.enqueue(object : Callback<Map<String, Map<String, Double>>> {
            override fun onResponse(
                call: Call<Map<String, Map<String, Double>>>,
                response: Response<Map<String, Map<String, Double>>>
            ) {
                if (response.isSuccessful) {
                    currentSolPrice = response.body()?.get("solana")?.get("usd")
                    if (currentSolPrice != null) {
                        showPriceDialog("Solana", currentSolPrice!!)
                    } else {
                        Toast.makeText(this@MainActivity, "Error: Solana price not available.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Snackbar.make(display, "Error: Solana price not available.", Snackbar.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Map<String, Double>>>, t: Throwable) {
                Snackbar.make(display, "Connection error", Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    private var decimalPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        display = findViewById(R.id.text1)
        resultView = findViewById(R.id.criptoSolution)
        bitcoinIcon = findViewById(R.id.btc)
        ethereumIcon = findViewById(R.id.eth)
        cardanoIcon = findViewById(R.id.car) // Inicialitza l'icona de Cardano
        clearButton = findViewById(R.id.buttonCE)
        eraseButton = findViewById(R.id.buttonErre)
        decimalButton = findViewById(R.id.buttonComa)
        solanaIcon = findViewById(R.id.sol) // Inicialitza l'icona de Solana

        display.isFocusable = false
        display.isFocusableInTouchMode = false
        resultView.isFocusable = false
        resultView.isFocusableInTouchMode = false

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        cryptoApi = retrofit.create(CryptoApi::class.java)

        fetchCryptoPrices()

        bitcoinIcon.setOnClickListener {
            selectedCryptoPrice = currentBtcPrice
            convertUsdToCrypto(selectedCryptoPrice)
        }
        solanaIcon.setOnClickListener {
            selectedCryptoPrice = currentSolPrice
            convertUsdToCrypto(selectedCryptoPrice)
        }
        ethereumIcon.setOnClickListener {
            selectedCryptoPrice = currentEthPrice
            convertUsdToCrypto(selectedCryptoPrice)
        }
        cardanoIcon.setOnClickListener {
            selectedCryptoPrice = currentAdaPrice
            convertUsdToCrypto(selectedCryptoPrice)
        }

        clearButton.setOnClickListener {
            clearInput()
        }

        eraseButton.setOnClickListener {
            eraseLastCharacter()
        }

        decimalButton.setOnClickListener {
            appendDecimalToDisplay()
        }

        setNumberButtonListeners()

        display.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting || s.isNullOrEmpty()) return

                val currentText = s.toString()

                if (currentText.endsWith(",")) {
                    return
                }

                val parts = currentText.split(",")
                if (parts.size > 1 && parts[1].length > 2) {
                    s?.delete(s.length - 1, s.length)
                    return
                }

                isFormatting = true
                val formatted = formatWithDotsAndCommas(currentText)
                display.setText(formatted)
                display.setSelection(formatted.length)
                isFormatting = false

                convertUsdToCrypto(selectedCryptoPrice)
            }
        })

    }

    private fun eraseLastCharacter() {
        val currentText = display.text.toString()
        if (currentText.isNotEmpty()) {
            display.setText(currentText.substring(0, currentText.length - 1))
            display.setSelection(display.text.length)
        }
    }

    private fun appendDecimalToDisplay() {
        val currentText = display.text.toString()

        if (!currentText.contains(",")) {
            if (currentText.isEmpty()) {
                display.setText("0,")
            } else {
                display.setText("$currentText,")
            }
            decimalPressed = true
        }
    }

    private fun clearInput() {
        display.setText("")
        resultView.setText("")
        decimalPressed = false
    }

    private fun convertUsdToCrypto(cryptoPrice: Double?) {
        val usdAmount = display.text.toString().replace(",", ".").replace(".", "").toDoubleOrNull()

        if (usdAmount == null || usdAmount == 0.0 || cryptoPrice == null) {
            Snackbar.make(display, "You should select a crypto to start converting", Snackbar.LENGTH_SHORT).show()
            return
        }

        val cryptoResult = usdAmount / cryptoPrice
        // Limitar el resultado a 6 decimales
        val formattedResult = String.format("%.6f", cryptoResult)

        // Asignar el resultado formateado a resultView
        resultView.setText(formattedResult)
    }





    private fun formatNumber(value: Double): String {
        val symbols = DecimalFormatSymbols(Locale.US)
        val formatter = DecimalFormat("#,##0.##########", symbols)
        return formatter.format(value)
    }

    private fun formatWithDotsAndCommas(value: String): String {
        val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }

        val parts = value.split(",")
        val integerPart = parts[0].replace(".", "")
        val decimalPart = if (parts.size > 1) parts[1] else ""

        val formattedIntegerPart = DecimalFormat("#,###", symbols).format(integerPart.toLongOrNull() ?: 0)

        return if (decimalPart.isEmpty()) {
            formattedIntegerPart
        } else {
            "$formattedIntegerPart,$decimalPart"
        }
    }
    private fun showPriceDialog(cryptoName: String, price: Double) {
        AlertDialog.Builder(this)
            .setTitle("$cryptoName Price")
            .setMessage("Current price: $price USD")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun setNumberButtonListeners() {
        val numberButtons = listOf(
            R.id.button0 to "0",
            R.id.button1 to "1",
            R.id.button2 to "2",
            R.id.button3 to "3",
            R.id.button4 to "4",
            R.id.button5 to "5",
            R.id.button6 to "6",
            R.id.button7 to "7",
            R.id.button8 to "8",
            R.id.button9 to "9"
        )

        for ((buttonId, number) in numberButtons) {
            findViewById<Button>(buttonId).setOnClickListener {
                appendNumberToDisplay(number)
            }
        }
    }

    private fun appendNumberToDisplay(number: String) {
        val currentText = display.text.toString()
        display.setText(currentText + number)
    }
}
