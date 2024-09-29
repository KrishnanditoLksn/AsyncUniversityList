package app.ditodev.multi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import app.ditodev.multi.databinding.ActivityMainBinding
import app.ditodev.multi.utils.Util
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        getRandomQuote()
        binding.btnStart.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        if (p0?.id != null) {
            startActivity(Intent(this@MainActivity, ListQuotesActivity::class.java))
        }
    }

    private fun getRandomQuote() {
        binding.pbLoading.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        //val url = "https://quote-api.dicoding.dev/random"
        val url = "http://universities.hipolabs.com/search?country=United+States"
        client.get(url, object : AsyncHttpResponseHandler() {
            /*
            if SUCCESS
             */
            override fun onSuccess(p0: Int, p1: Array<out Header>, p2: ByteArray) {
                binding.pbLoading.visibility = View.INVISIBLE
                val res = String(p2)

                Log.d(Util.TAG, res)
                try {
                    val resObj = JSONArray(res)
                    //val quota = resObj.getString("en")
                    //val author = resObj.getString("author")
                    val jsonObj = resObj.getJSONObject(0)
                    val quota = jsonObj.getString("name")
                    val author = jsonObj.getString("country")
                    /*
                    bind into related views
                     */
                    binding.tvQuota.text = quota
                    binding.tvAuthor.text = author
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            /*
            if FAIL
             */
            override fun onFailure(
                p0: Int,
                p1: Array<out Header>,
                p2: ByteArray,
                p3: Throwable
            ) {
                binding.pbLoading.visibility = View.INVISIBLE
                val errorMsg = when (p0) {
                    401 -> "$p0 : Bad Request"
                    403 -> "$p0: Forbidden"
                    404 -> "$p0 : Not Found"
                    else -> "$p0 : ${p3.message}"
                }
                Toast.makeText(this@MainActivity, errorMsg, Toast.LENGTH_SHORT).show()
            }

        })
    }


}