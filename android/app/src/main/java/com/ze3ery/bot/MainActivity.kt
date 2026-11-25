package com.ze3ery.bot

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.*

class MainActivity : Activity() {

    private val gold = Color.rgb(212, 175, 55)
    private val black = Color.rgb(10, 10, 10)
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER
        layout.setPadding(24, 30, 24, 30)
        layout.setBackgroundColor(black)

        val title = TextView(this)
        title.text = "ZE3ERY BOT"
        title.textSize = 32f
        title.setTextColor(gold)
        title.gravity = Gravity.CENTER

        status = TextView(this)
        status.text = "STATUS: DISCONNECTED"
        status.textSize = 18f
        status.setTextColor(Color.WHITE)
        status.gravity = Gravity.CENTER

        val symbol = TextView(this)
        symbol.text = "XAUUSD  |  GOLD\nTIMEFRAME: M5\nMODE: DEMO"
        symbol.textSize = 18f
        symbol.setTextColor(gold)
        symbol.gravity = Gravity.CENTER

        val start = Button(this)
        start.text = "START BOT"
        start.setOnClickListener {
            status.text = "DEMO MODE - NOT CONNECTED"
        }

        val stop = Button(this)
        stop.text = "STOP BOT"
        stop.setOnClickListener {
            status.text = "BOT STOPPED"
        }

        layout.addView(title)
        layout.addView(space())
        layout.addView(symbol)
        layout.addView(space())
        layout.addView(status)
        layout.addView(space())
        layout.addView(start)
        layout.addView(stop)

        setContentView(layout)
    }
private fun sendCommand(enabled: Boolean) {
    val token = tokenInput.text.toString().trim()

    if (token.isEmpty()) {
        status.text = "ENTER CONNECTION CODE"
        status.setTextColor(Color.RED)
        return
    }

    status.text = "CONNECTING..."
    status.setTextColor(gold)

    thread {
        var connection: HttpURLConnection? = null

        try {
            connection = URL("$serverUrl/app/control")
                .openConnection() as HttpURLConnection

            connection.requestMethod = "POST"
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("x-device-token", token)

            connection.outputStream.use {
                it.write("""{"enabled":$enabled}""".toByteArray())
            }

            val code = connection.responseCode
            val response = (if (code >= 400) connection.errorStream else connection.inputStream)
                ?.bufferedReader()?.use { it.readText() } ?: ""

            runOnUiThread {
                status.text = if (code == 200) {
                    if (enabled) "DEMO READY" else "BOT STOPPED"
                } else {
                    "ERROR $code: $response"
                }
                status.setTextColor(if (code == 200) gold else Color.RED)
            }
        } catch (e: Exception) {
            runOnUiThread {
                status.text = "CONNECTION FAILED: ${e.message}"
                status.setTextColor(Color.RED)
            }
        } finally {
            connection?.disconnect()
        }
    }
}
    private fun space(): TextView {
        return TextView(this).apply { text = "\n" }
    }
}
