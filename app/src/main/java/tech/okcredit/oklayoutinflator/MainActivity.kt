package tech.okcredit.oklayoutinflator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import tech.okcredit.layout_inflator.OkLayoutInflater
import tech.okcredit.oklayoutinflator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val okLayoutInflater by lazy { OkLayoutInflater(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_loading_view)

        okLayoutInflater.inflate(R.layout.activity_main, null) { view ->
            binding = ActivityMainBinding.bind(view)
            setContentView(binding.root)
            setupUi()
        }
    }

    private fun setupUi() {
        supportFragmentManager.beginTransaction()
            .replace(binding.mainContent.container.id, MainFragment.newInstance())
            .commitNow()
    }
}