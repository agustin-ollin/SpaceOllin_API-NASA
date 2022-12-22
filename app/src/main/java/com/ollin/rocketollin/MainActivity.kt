package com.ollin.rocketollin

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
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
    // Your API KEY -> generate here https://api.nasa.gov/
    private val keyAPI = "DEMO_KEY"

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
                showMessage("Wait a moment please!")
                searchByDate("apod?api_key=$keyAPI&date=${fechaRegistroTextField.text.toString() ?: LocalDateTime.now()}")
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

    @SuppressLint("WrongConstant")
    private fun searchByDate(query: String){
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(APIService::class.java).getImageByLocalDate("$query")
            val obj = call.body()

            runOnUiThread {
                if (call.isSuccessful) {
                    with(binding){
                        showImage.visibility = View.VISIBLE
                        titleImage.text = "${obj!!.title}"
                        explanationImage.text = "${obj.explanation}"
                        copyright.text = "By ${obj.copyright ?: "NASA"}"

                        showImage.setOnClickListener {
                            showImageFull(obj)
                        }
                    }
                } else {
                    showMessage("An error has occurred!")
                }
            }
        }
    }

    private fun showMessage(message: String) = Toast.makeText(this, "$message", Toast.LENGTH_LONG).show()

    private fun showImageFull(obj: NasaResponse) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.fullscreenalert)
        val view: View = layoutInflater.inflate(R.layout.full_alert_dialog, null)

        val image: ImageView = view.findViewById(R.id.imageViewNasa)
        Picasso.get().load(obj.url).error(R.drawable.error).into(image)

        builder.setView(view)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

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
        val mes: String = if (month < 9) "0${month + 1}" else "${month + 1}"

        binding.fechaRegistroTextField.setText("$year-$mes-$dia")
    }
}