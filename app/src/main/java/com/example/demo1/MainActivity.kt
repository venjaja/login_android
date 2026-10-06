package com.example.demo1

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.demo1.datos.Conexion
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private lateinit var rutEditText: EditText
    private lateinit var claveEditText: EditText
    private lateinit var botonIngresar: Button
    private lateinit var botonRegistrar: Button

    private lateinit var requestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        relacionamosVistas()
        requestQueue = Volley.newRequestQueue(this)


        botonIngresar.setOnClickListener {
            validarIngreso()
        }

        botonRegistrar.setOnClickListener {
            validarRegistro()
        }
    }

    private fun relacionamosVistas() {
        // Enlaces con los IDs del activity_main.xml
        rutEditText = findViewById(R.id.rut)
        claveEditText = findViewById(R.id.clave)
        botonIngresar = findViewById(R.id.botonIngresar)
        botonRegistrar = findViewById(R.id.botonRegistrar)
    }

    private fun encriptarClave(texto: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(texto.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun esRutValido(rut: String): Boolean {
        // Valida formato: 7 u 8 dígitos, guion, y dígito verificador (0-9 o K)
        val regex = Regex("^\\d{7,8}-[0-9kK]$")
        return regex.matches(rut)
    }

    private fun validarRegistro() {
        val rut = rutEditText.text.toString().trim()
        val clave = claveEditText.text.toString().trim()

        if (rut.isEmpty()) {
            rutEditText.error = "El RUT es obligatorio"
            rutEditText.requestFocus()
            return
        }

        if (!esRutValido(rut)) {
            rutEditText.error = "RUT inválido. Formato requerido: 12345678-K o 1234567-8"
            rutEditText.requestFocus()
            return
        }

        if (clave.isEmpty()) {
            claveEditText.error = "La clave es obligatoria"
            claveEditText.requestFocus()
            return
        }

        val claveEncriptada = encriptarClave(clave)
        val url = "${Conexion.URL_WEB_SERVICES}registro.php"

        val stringRequest = object : StringRequest(
            Method.POST, url,
            { response ->
                val respuesta = response.trim()
                if (respuesta.contains("exitoso", ignoreCase = true) || respuesta == "1") {
                    Toast.makeText(applicationContext, "Registro insertado exitoso", Toast.LENGTH_LONG).show()
                    rutEditText.setText("")
                    claveEditText.setText("")
                } else if (respuesta.contains("Sesion iniciada correctamente", ignoreCase = true)) {
                    Toast.makeText(applicationContext, "Sesion iniciada correctamente", Toast.LENGTH_LONG).show()
                } else if (respuesta.contains("Usuario no existe", ignoreCase = true)) {
                    Toast.makeText(applicationContext, "Usuario no existe", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(applicationContext, respuesta, Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                val detalleError = error.networkResponse?.statusCode?.let { "HTTP $it" }
                    ?: error.message ?: error.toString()
                Toast.makeText(applicationContext, "Error comunicación: $detalleError", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["rut"] = rut
                params["clave"] = claveEncriptada
                return params
            }
        }
        requestQueue.add(stringRequest)
    }

    private fun validarIngreso() {
        val rut = rutEditText.text.toString().trim()
        val clave = claveEditText.text.toString().trim()

        if (rut.isEmpty()) {
            rutEditText.error = "El RUT es obligatorio"
            rutEditText.requestFocus()
            return
        }

        if (!esRutValido(rut)) {
            rutEditText.error = "RUT inválido. Formato requerido: 12345678-K o 1234567-8"
            rutEditText.requestFocus()
            return
        }

        if (clave.isEmpty()) {
            claveEditText.error = "La clave es obligatoria"
            claveEditText.requestFocus()
            return
        }

        val claveEncriptada = encriptarClave(clave)
        val url = "${Conexion.URL_WEB_SERVICES}ingreso.php"

        val stringRequest = object : StringRequest(
            Method.POST, url,
            { response ->
                val respuesta = response.trim()
                if (respuesta.contains("Sesion iniciada correctamente", ignoreCase = true) ||
                    respuesta == "1"
                ) {
                    Toast.makeText(applicationContext, "Sesion iniciada correctamente", Toast.LENGTH_LONG).show()
                } else if (respuesta.contains("Usuario no existe", ignoreCase = true) ||
                    respuesta == "0"
                ) {
                    Toast.makeText(applicationContext, "Usuario no existe", Toast.LENGTH_LONG).show()
                } else if (respuesta.contains("exitoso", ignoreCase = true)) {
                    Toast.makeText(applicationContext, "Registro insertado exitoso", Toast.LENGTH_LONG).show()
                    rutEditText.setText("")
                    claveEditText.setText("")
                } else {
                    Toast.makeText(applicationContext, respuesta, Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                val detalleError = error.networkResponse?.statusCode?.let { "HTTP $it" }
                    ?: error.message ?: error.toString()
                Toast.makeText(applicationContext, "Error comunicación: $detalleError", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["rut"] = rut
                params["clave"] = claveEncriptada
                return params
            }
        }
        requestQueue.add(stringRequest)
    }
}