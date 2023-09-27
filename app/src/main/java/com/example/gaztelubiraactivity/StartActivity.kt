package com.example.gaztelubiraactivity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class StartActivity : AppCompatActivity() {

    private var dataLoaded = false // Variable para rastrear si los datos se han cargado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_activity)

        // Obtén los valores de email y provider de los extras del Intent actual
        val email = intent.getStringExtra("email")
        val provider = intent.getStringExtra("provider")

        // Inicia la carga de datos en segundo plano
        loadDataInBackground()

        // Configura un temporizador para cerrar la pantalla de presentación después de un tiempo máximo
        Handler().postDelayed({
            if (dataLoaded) {
                // Si los datos se han cargado, inicia MainActivity y cierra SplashActivity
                val mainIntent = Intent(this, MainActivity::class.java)
                mainIntent.putExtra("email", email)
                mainIntent.putExtra("provider", provider)
                startActivity(mainIntent)
                finish()
            } else {
                // Si los datos aún no se han cargado, espera activamente (en este ejemplo, durante otros 3 segundos)
                Handler().postDelayed({
                    // Comprueba nuevamente si los datos se han cargado
                    if (dataLoaded) {
                        val mainIntent = Intent(this, MainActivity::class.java)
                        mainIntent.putExtra("email", email)
                        mainIntent.putExtra("provider", provider)
                        startActivity(mainIntent)
                    }
                    finish()
                }, 3000) // Espera durante 3 segundos más
            }
        }, 3000) // Espera durante 3 segundos antes de verificar si los datos se han cargado
    }

    private fun loadDataInBackground() {
        // Realiza la carga de datos en segundo plano usando corrutinas (suspend)
        CoroutineScope(Dispatchers.IO).launch {
            SupabaseManager.getData()
            dataLoaded = true // Marca que los datos se han cargado
        }
    }
}
