package app.ditodev.multi

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.ditodev.multi.adapter.QuoteAdapter
import app.ditodev.multi.databinding.ActivityListQuotesBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray

class ListQuotesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListQuotesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListQuotesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val layoutManager = LinearLayoutManager(this)
        binding.rvQuotes.setLayoutManager(layoutManager)
        val itemDec = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvQuotes.addItemDecoration(itemDec)

        getListQuotes()
    }

    private fun getListQuotes() {
        binding.progressBar.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        //val url = "https://quote-api.dicoding.dev/list"
        val url = "http://universities.hipolabs.com/search?country=United+States"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(p0: Int, p1: Array<out Header>, p2: ByteArray) {
                binding.progressBar.visibility = View.INVISIBLE
                val res = String(p2)
                val listUni = ArrayList<String>()
                try {
                    val resObj = JSONArray(res)
                    if (resObj.length() > 1) {
                        for (i in 0 until resObj.length()) {
                            val jsonObj = resObj.getJSONObject(i)
                            val uniName = jsonObj.getString("name")
                            val country = jsonObj.getString("country")
                            listUni.add("\n$uniName\n — $country\n")
                        }

                        val adapter = QuoteAdapter(listUni)
                        binding.rvQuotes.adapter = adapter
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@ListQuotesActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                p0: Int,
                p1: Array<out Header>,
                p2: ByteArray,
                p3: Throwable
            ) {
                binding.progressBar.visibility = View.INVISIBLE

                val errorMsg = when (p0) {
                    401 -> "$p0 : Bad Request"
                    403 -> "$p0: Forbidden"
                    404 -> "$p0 : Not Found"
                    else -> "$p0 : ${p3.message}"
                }
                Toast.makeText(this@ListQuotesActivity, errorMsg, Toast.LENGTH_SHORT).show()
            }
        })
    }
}