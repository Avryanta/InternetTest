package com.example.internettest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.internettest.databinding.ActivityMainBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import timber.log.Timber.plant
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        plant(Timber.DebugTree())

        val tag = binding.editText.text.toString()

        binding.btnHTTP.setOnClickListener {
            Thread(BackgroundFetcher("HTTP", tag)).start()
        }

        binding.btnOkHTTP.setOnClickListener {
            Thread(BackgroundFetcher("OkHTTP", tag)).start()
        }
    }

    inner class BackgroundFetcher(private val threadName: String, private val tag: String) : Runnable {
         override fun run() {
             val API_FLICKR = "https://api.flickr.com"
             val URI = "/services/rest/"
             val METHOD = "?method=flickr.photos.search"
             val API_KEY = "&api_key=ff49fcd4d4a08aa6aafb6ea3de826464"
             val TAGS = "&tags="
             val FORMAT = "&format=json&nojsoncallback=1"

             val url = API_FLICKR + URI + METHOD + API_KEY + TAGS + tag + FORMAT

             if (threadName == "HTTP") {
                 val info = fetchHTTPInfo(url)
                 Timber.tag("Flickr Cats").d(info)
             } else {
                 val info = fetchOkHTTPInfo(url)
                 Timber.tag("Flickr OkCats").i(info)
             }
        }
    }

    private fun fetchHTTPInfo(url: String): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        return connection.inputStream.bufferedReader().readText()
    }

    private fun fetchOkHTTPInfo(url : String) : String {

            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .build()

            val response = client.newCall(request).execute()
            return response.body?.string().toString()

    }

}


