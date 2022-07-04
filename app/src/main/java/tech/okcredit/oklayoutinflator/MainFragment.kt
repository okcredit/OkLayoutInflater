package tech.okcredit.oklayoutinflator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import tech.okcredit.layout_inflator.OkLayoutInflater

class MainFragment : Fragment() {

    private val okLayoutInflater by lazy { OkLayoutInflater(this) }

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val loaderView = inflater.inflate(R.layout.layout_loading_view, container, false)

        okLayoutInflater.inflate(R.layout.main_fragment, container) { inflatedView ->
            with(loaderView as ViewGroup) {
                removeAllViewsInLayout()
                addView(inflatedView)
            }
        }

        return loaderView
    }
}