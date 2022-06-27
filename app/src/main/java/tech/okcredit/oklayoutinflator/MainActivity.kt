package tech.okcredit.oklayoutinflator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import tech.okcredit.oklayoutinflator.databinding.ActivityMainBinding
import tech.okcredit.layout_inflator.OkLayoutInflater

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null

    private val binding get() = _binding!!

    private val okLayoutInflater by lazy { OkLayoutInflater(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.layout_loading_view)
        okLayoutInflater.inflate(
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


}