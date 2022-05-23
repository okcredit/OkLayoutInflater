package tech.okcredit.coroutinelayoutinflater

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import tech.okcredit.coroutinelayoutinflater.databinding.ActivityMainBinding
import tech.okcredit.layout_inflator.CoroutineLayoutInflater

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null

    private val binding get() = _binding!!

    private val asyncLayoutInflater by lazy { CoroutineLayoutInflater(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.layout_loading_view)
        asyncLayoutInflater.inflate(
            R.layout.activity_main,
            null,
        ) {
            setContentView(it)
            _binding = ActivityMainBinding.inflate(layoutInflater)
            setupUi()
        }
    }

    private fun setupUi() {
        supportFragmentManager.beginTransaction()
            .replace(binding.mainContent.container.id, MainFragment.newInstance())
            .commitNow()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        asyncLayoutInflater.cancel()
    }
}