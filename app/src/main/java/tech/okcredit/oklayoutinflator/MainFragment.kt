package tech.okcredit.oklayoutinflator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tech.okcredit.layout_inflator.OkLayoutInflater

class MainFragment : Fragment() {

    private val okLayoutInflater by lazy { OkLayoutInflater(requireContext()) }

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.layout_loading_view, container, false)

        okLayoutInflater.inflate(
            R.layout.main_fragment,
            container
        ) {
            (v as? ViewGroup)?.removeAllViewsInLayout()
            (v as? ViewGroup)?.addView(it)
        }
        return v
    }

    override fun onDestroyView() {
        okLayoutInflater.cancel()
        super.onDestroyView()
    }
}