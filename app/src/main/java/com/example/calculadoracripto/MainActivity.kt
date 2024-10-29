package com.example.calculadoracripto

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
    private lateinit var cardanoIcon: ImageView // Añadido para Cardano
    private lateinit var solanaIcon: ImageView // Añadido para Solana
    private lateinit var clearButton: Button
    private lateinit var eraseButton: Button
    private lateinit var decimalButton: Button
    private val BASE_URL = "https://api.coingecko.com/api/v3/"
    private lateinit var retrofit: Retrofit
    private lateinit var cryptoApi: CryptoApi
    private var currentBtcPrice: Double? = null
    private var currentEthPrice: Double? = null
    private var currentAdaPrice: Double? = null // Precio de Cardano
    private var currentSolPrice: Double? = null // Precio de Solana
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
        val cardanoCall = cryptoApi.getCryptoPrice("cardano") // Petición para Cardano
        val solanaCall = cryptoApi.getCryptoPrice("solana") // Petición para Solana

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
                        Toast.makeText(this@MainActivity, "Error: el precio de Bitcoin no está disponible.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Error al obtener el precio de Bitcoin: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Map<String, Double>>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(this@MainActivity, "Error: el precio de Ethereum no está disponible.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Error al obtener el precio de Ethereum: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Map<String, Double>>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(this@MainActivity, "Error: el precio de Cardano no está disponible.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Error al obtener el precio de Cardano: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Map<String, Double>>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(this@MainActivity, "Error: el precio de Solana no está disponible.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Error al obtener el precio de Solana: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Map<String, Double>>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
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
        cardanoIcon = findViewById(R.id.car) // Inicializa el icono de Cardano
        clearButton = findViewById(R.id.buttonCE)
        eraseButton = findViewById(R.id.buttonErre)
        decimalButton = findViewById(R.id.buttonComa)
        solanaIcon = findViewById(R.id.sol) // Inicializa el icono de Solana

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
                display.setText(currentText + ",")
            }
            display.setSelection(display.text.length)
        }
    }

    private fun convertUsdToCrypto(cryptoPrice: Double?) {
        val usdAmount = display.text.toString().replace(",", ".").replace(".", "").toDoubleOrNull()

        if (usdAmount == null || usdAmount == 0.0 || cryptoPrice == null) {
            Toast.makeText(this, "Introduce una cantidad válida y asegúrate de haber obtenido el precio de la criptomoneda.", Toast.LENGTH_SHORT).show()
            return
        }

        val cryptoResult = usdAmount / cryptoPrice
        resultView.setText(cryptoResult.toString())
    }

    private fun showPriceDialog(cryptoName: String, price: Double) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Precio de $cryptoName")
        builder.setMessage("El precio actual de $cryptoName es: $${price}")
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun clearInput() {
        display.text.clear()
        resultView.text.clear()
        decimalPressed = false
    }

    private fun setNumberButtonListeners() {
        val numberButtons = listOf(
            R.id.button0, R.id.button1, R.id.button2, R.id.button3,
            R.id.button4, R.id.button5, R.id.button6, R.id.button7,
            R.id.button8, R.id.button9
        )

        for (buttonId in numberButtons) {
            findViewById<Button>(buttonId).setOnClickListener {
                appendNumberToDisplay((it as Button).text.toString())
            }
        }
    }

    private fun appendNumberToDisplay(number: String) {
        val currentText = display.text.toString()
        display.setText(currentText + number)
        display.setSelection(display.text.length)

        // Llama a la conversión después de añadir un número
        convertUsdToCrypto(selectedCryptoPrice)
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


}
