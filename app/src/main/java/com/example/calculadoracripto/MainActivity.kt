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
    private lateinit var clearButton: Button
    private lateinit var decimalButton: Button
    private val BASE_URL = "https://api.coingecko.com/api/v3/"
    private lateinit var retrofit: Retrofit
    private lateinit var bitcoinApi: BitcoinApi
    private var currentBtcPrice: Double? = null
    private var decimalPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        display = findViewById(R.id.text1)
        resultView = findViewById(R.id.criptoSolution)
        bitcoinIcon = findViewById(R.id.btc)
        clearButton = findViewById(R.id.buttonErre)
        decimalButton = findViewById(R.id.buttonComa)

        display.isFocusable = false
        display.isFocusableInTouchMode = false
        resultView.isFocusable = false
        resultView.isFocusableInTouchMode = false

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        bitcoinApi = retrofit.create(BitcoinApi::class.java)

        fetchBitcoinPrice()

        bitcoinIcon.setOnClickListener {
            convertUsdToBtc()
        }

        clearButton.setOnClickListener {
            clearInput()
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

                // Si el texto termina en coma, no formateamos inmediatamente
                if (currentText.endsWith(",")) {
                    return
                }

                // Limitar a 2 dígitos después de la coma
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

    private fun appendDecimalToDisplay() {
        val currentText = display.text.toString()

        // Verificamos si ya hay una coma o si está vacío
        if (!currentText.contains(",")) {
            if (currentText.isEmpty()) {
                // Si está vacío, añadimos "0,"
                display.setText("0,")
            } else {
                // Si ya hay números, añadimos la coma al final
                display.setText(currentText + ",")
            }
            display.setSelection(display.text.length) // Mantén el cursor al final
        }
    }





    private fun fetchBitcoinPrice() {
        val call = bitcoinApi.getBitcoinPrice("bitcoin")

        call.enqueue(object : Callback<BitcoinPriceResponse> {
            override fun onResponse(call: Call<BitcoinPriceResponse>, response: Response<BitcoinPriceResponse>) {
                if (response.isSuccessful) {
                    currentBtcPrice = response.body()?.bitcoin?.usd
                    if (currentBtcPrice != null) {
                        showPriceDialog(currentBtcPrice!!)
                    } else {
                        Toast.makeText(this@MainActivity, "Error: el precio de Bitcoin no está disponible.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Error al obtener el precio de Bitcoin: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<BitcoinPriceResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun convertUsdToBtc() {
        val usdAmount = display.text.toString().replace(",", ".").replace(".", "").toDoubleOrNull()

        if (usdAmount == null || usdAmount == 0.0 || currentBtcPrice == null) {
            Toast.makeText(this, "Introduce una cantidad válida y asegúrate de haber obtenido el precio de Bitcoin.", Toast.LENGTH_SHORT).show()
            return
        }

        val btcResult = usdAmount / currentBtcPrice!!
        resultView.setText(btcResult.toString())
    }

    private fun showPriceDialog(price: Double) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Precio de Bitcoin")
        builder.setMessage("El precio actual de Bitcoin es: $${price}")
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
            R.id.button0, R.id.button1, R.id.button2,
            R.id.button3, R.id.button4, R.id.button5,
            R.id.button6, R.id.button7, R.id.button8,
            R.id.button9
        )

        for (buttonId in numberButtons) {
            val button = findViewById<Button>(buttonId)
            button.setOnClickListener {
                appendNumberToDisplay(button.text.toString())
            }
        }

        // Añadir el listener para el botón de coma
        val decimalButton = findViewById<Button>(R.id.buttonComa)
        decimalButton.setOnClickListener {
            appendDecimalToDisplay() // Llama a la función que añade la coma
        }
    }



    private fun appendNumberToDisplay(number: String) {
        val currentText = display.text.toString()

        // Si ya hay una coma y 2 dígitos después de ella, no añadir más
        if (currentText.contains(",") && currentText.split(",")[1].length >= 2) {
            return
        }

        if (currentText == "0" || currentText.isEmpty()) {
            display.setText(number)
        } else {
            display.setText(currentText + number)
        }

        display.setSelection(display.text.length)
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

    interface BitcoinApi {
        @GET("simple/price")
        fun getBitcoinPrice(@Query("ids") ids: String, @Query("vs_currencies") vs_currencies: String = "usd"): Call<BitcoinPriceResponse>
    }

    data class BitcoinPriceResponse(
        val bitcoin: BitcoinData
    )

    data class BitcoinData(
        val usd: Double
    )
}
