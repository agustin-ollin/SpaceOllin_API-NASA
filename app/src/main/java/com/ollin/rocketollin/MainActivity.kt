package com.ollin.rocketollin

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ollin.rocketollin.databinding.ActivityMainBinding
import com.ollin.rocketollin.local_date.DatePickerFragment
import com.ollin.rocketollin.models.APIService
import com.ollin.rocketollin.models.NasaResponse
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initEventListener()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initEventListener(){
        with(binding){
            fechaRegistroTextField.setOnClickListener {
                showDatePickerDialog()
            }

            btnLoad.setOnClickListener {
                searchByDate("apod?api_key=DEMO_KEY&date=${fechaRegistroTextField.text.toString() ?: LocalDateTime.now()}")
            }
        }
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl("https://api.nasa.gov/planetary/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun searchByDate(query: String){
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(APIService::class.java).getImageByLocalDate("$query")
            val obj = call.body()

            runOnUiThread {
                if (call.isSuccessful) {
                    obj.let {
                        with(binding){
                            Picasso.get().load(obj!!.url).into(imagenShow)

                            titleImage.text = "${obj.title}"

                            imagenShow.setOnClickListener {
                                showFullInfo(obj)
                            }
                        }
                    }
                } else {
                    showError()
                }
            }
        }
    }

    private fun showFullInfo(obj: NasaResponse) {
        MaterialAlertDialogBuilder(this)
            .setTitle(obj.title)
            .setMessage("${obj.explanation}\n\nBy ${obj.copyright ?: "NASA"}")
            .setPositiveButton(resources.getString(R.string.close)){dialog, which ->

            }.show()
    }

    private fun showError() = Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()

    /**
     * Función para mostrar cuadro de fecha
     */
    private fun showDatePickerDialog() {
        val datePicker =
            DatePickerFragment({ day, month, year -> onDateSelected(day, month, year) })
        datePicker.show(supportFragmentManager, "datePicker")
    }

    /**
     * Función para asignar la fecha al EditText
     */
    fun onDateSelected(day: Int, month: Int, year: Int) {
        val dia: String = if (day < 10) "0$day" else "$day"
        val mes: String = if (month < 10) "0$month" else "$month"

        binding.fechaRegistroTextField.setText("$year-$mes-$dia")
    }
}