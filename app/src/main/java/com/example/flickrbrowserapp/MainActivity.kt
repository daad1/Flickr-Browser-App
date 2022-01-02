package com.example.flickrbrowserapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {
    private lateinit var imagesList: ArrayList<DataImage>

    private lateinit var rvMain: RecyclerView
    private lateinit var rvAdapter: RVAdapter

    private lateinit var lLayout: LinearLayout
    private lateinit var etSearch: EditText
    private lateinit var btSearch: Button

    private lateinit var ivMain: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imagesList = arrayListOf()
        rvMain = findViewById(R.id.rvMain)
        rvAdapter = RVAdapter(this, imagesList)
        rvMain.adapter = rvAdapter
        rvMain.layoutManager = LinearLayoutManager(this)

        lLayout = findViewById(R.id.lLayout)
        etSearch = findViewById(R.id.etSearch)
        btSearch = findViewById(R.id.btnSearch)

        btSearch.setOnClickListener {
            if (etSearch.text.isNotEmpty()) {
                getAPI()
            } else {
                Toast.makeText(this, "Please enter text to Search", Toast.LENGTH_SHORT).show()
            }
        }
        ivMain = findViewById(R.id.imageMain)
        ivMain.setOnClickListener {
            closeImage()
        }


    }

    private fun getAPI() {
        CoroutineScope(Dispatchers.IO).launch {
            val data = async { getPhoto() }.await()
            if (data.isNotEmpty()) {
                println(data)
                showPhotos(data)
            } else {
                Toast.makeText(this@MainActivity, "No Images Found", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getPhoto(): String {
        var response = ""
        try {
            response =
                URL("https://www.flickr.com/services/rest/?method=flickr.photos.search&per_page=10&api_key=ccae5d31a09fa695df95936ac08465d8&tags=${etSearch.text}&&format=json&nojsoncallback=1").readText(
                    Charsets.UTF_8
                )

        } catch (e: Exception) {
            println("ISSUE: $e")
        }
        return response
    }

    private suspend fun showPhotos(data: String) {
        withContext(Dispatchers.Main) {

            val jsonObj = JSONObject(data)

            val photos = jsonObj.getJSONObject("photos").getJSONArray("photo")
            println("photos")

            println(photos.getJSONObject(0))

            println(photos.getJSONObject(0).getString("farm"))


            for (i in 0 until photos.length()) {
                val title = photos.getJSONObject(i).getString("title")

                val serverID = photos.getJSONObject(i).getString("server")

                val id = photos.getJSONObject(i).getString("id")

                val secret = photos.getJSONObject(i).getString("secret")
                val photosLink = "https://live.staticflickr.com/$serverID/${id}_${secret}.jpg"
                imagesList.add(DataImage(title, photosLink))
            }
            rvAdapter.notifyDataSetChanged()

        }
    }

    fun openImage(link: String) {

        Glide.with(this).load(link).into(ivMain)
        ivMain.isVisible = true
        rvMain.isVisible = false
        lLayout.isVisible = false

    }

    private fun closeImage() {
        ivMain.isVisible = false
        rvMain.isVisible = true
        lLayout.isVisible = true
    }

}