package com.example.calculadoracripto

import android.os.Bundle
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

class MainActivity : AppCompatActivity() {

    private lateinit var display: EditText
    private lateinit var resultView: EditText // Para mostrar el resultado en criptoSolution
    private lateinit var bitcoinIcon: ImageView // Referencia al ImageView de Bitcoin
    private lateinit var clearButton: Button // Referencia al botón CE
    private val BASE_URL = "https://api.coingecko.com/api/v3/"
    private lateinit var retrofit: Retrofit
    private lateinit var bitcoinApi: BitcoinApi // Interfaz para la API
    private var currentBtcPrice: Double? = null // Variable para almacenar el precio actual de Bitcoin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        display = findViewById(R.id.text1)
        resultView = findViewById(R.id.criptoSolution)
        bitcoinIcon = findViewById(R.id.btc) // Inicializa el ImageView de Bitcoin
        clearButton = findViewById(R.id.buttonErre) // Inicializa el botón CE

        // Deshabilitar entrada por teclado
        display.isFocusable = false
        display.isFocusableInTouchMode = false
        resultView.isFocusable = false
        resultView.isFocusableInTouchMode = false

        // Inicializa Retrofit
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        bitcoinApi = retrofit.create(BitcoinApi::class.java)

        // Obtiene el precio de Bitcoin al abrir la aplicación
        fetchBitcoinPrice()

        // Llama a la función para obtener el precio de Bitcoin al hacer clic en el ImageView
        bitcoinIcon.setOnClickListener {
            convertUsdToBtc()
        }

        // Configura el botón CE para limpiar el campo de entrada
        clearButton.setOnClickListener {
            clearInput()
        }

        setNumberButtonListeners()
    }

    private fun fetchBitcoinPrice() {
        val call = bitcoinApi.getBitcoinPrice("bitcoin")

        call.enqueue(object : Callback<BitcoinPriceResponse> {
            override fun onResponse(call: Call<BitcoinPriceResponse>, response: Response<BitcoinPriceResponse>) {
                if (response.isSuccessful) {
                    currentBtcPrice = response.body()?.bitcoin?.usd
                    if (currentBtcPrice != null) {
                        // Muestra el precio actual de Bitcoin en un AlertDialog
                        showPriceDialog(currentBtcPrice!!)
                    } else {
                        Toast.makeText(this@MainActivity, "Error: el precio de Bitcoin no está disponible.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Imprimir detalles adicionales sobre el error
                    Toast.makeText(this@MainActivity, "Error al obtener el precio de Bitcoin: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<BitcoinPriceResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun convertUsdToBtc() {
        val usdAmount = display.text.toString().replace(".", "").toDoubleOrNull()

        if (usdAmount == null || usdAmount == 0.0 || currentBtcPrice == null) {
            Toast.makeText(this, "Introduce una cantidad válida y asegúrate de haber obtenido el precio de Bitcoin.", Toast.LENGTH_SHORT).show()
            return
        }

        // Realiza la conversión y muestra el resultado en criptoSolution
        val btcResult = usdAmount / currentBtcPrice!!
        resultView.setText(btcResult.toString()) // Muestra el resultado sin formatear
    }

    private fun showPriceDialog(price: Double) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Precio de Bitcoin")
        builder.setMessage("El precio actual de Bitcoin es: $${price}")
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun clearInput() {
        display.text.clear() // Limpia el campo de entrada de USD
        resultView.text.clear() // Limpia el resultado
    }

    private fun setNumberButtonListeners() {
        val numberButtons = listOf(
            R.id.button0, R.id.button1, R.id.button2,
            R.id.button3, R.id.button4, R.id.button5,
            R.id.button6, R.id.button7, R.id.button8,
            R.id.button9,
        )

        for (buttonId in numberButtons) {
            val button = findViewById<Button>(buttonId)
            button.setOnClickListener {
                appendNumberToDisplay(button.text.toString())
            }
        }
    }

    private fun appendNumberToDisplay(number: String) {
        // Si el campo está vacío o es "0", reemplaza el texto
        if (display.text.toString() == "0" || display.text.isEmpty()) {
            display.setText(number)
        } else {
            // Añade el número al final del texto actual
            display.setText(display.text.toString() + number)
        }
        // Aplica el formato con puntos
        display.setText(formatWithDots(display.text.toString()))
        display.setSelection(display.text.length) // Mantén el cursor al final
    }

    private fun formatWithDots(value: String): String {
        return if (value.isNotEmpty()) {
            val number = value.replace(".", "").toLongOrNull() ?: 0
            String.format("%,d", number).replace(",", ".")
        } else {
            "0"
        }
    }

    // Define la interfaz para la API
    interface BitcoinApi {
        @GET("simple/price")
        fun getBitcoinPrice(@Query("ids") ids: String, @Query("vs_currencies") vs_currencies: String = "usd"): Call<BitcoinPriceResponse>
    }

    // Define los modelos de respuesta
    data class BitcoinPriceResponse(
        val bitcoin: BitcoinData
    )

    data class BitcoinData(
        val usd: Double
    )
}
