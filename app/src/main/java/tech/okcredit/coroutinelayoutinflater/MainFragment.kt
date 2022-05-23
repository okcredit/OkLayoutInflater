package tech.okcredit.coroutinelayoutinflater

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tech.okcredit.layout_inflator.CoroutineLayoutInflater

class MainFragment : Fragment() {

    private val asyncLayoutInflater by lazy { CoroutineLayoutInflater(requireContext()) }

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.layout_loading_view, container, false)

        asyncLayoutInflater.inflate(
            R.layout.main_fragment,
            container
        ) {
            (v as? ViewGroup)?.removeAllViewsInLayout()
            (v as? ViewGroup)?.addView(it)
        }
        return v
    }

    override fun onDestroyView() {
        asyncLayoutInflater.cancel()
        super.onDestroyView()
    }
}