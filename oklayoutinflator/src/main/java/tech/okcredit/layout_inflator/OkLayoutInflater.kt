package tech.okcredit.layout_inflator

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.LayoutInflaterCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.*

/**
 * OkLayoutInflater solves below mentioned limitations of the **AsyncLayoutInflater**.
 *
 * AndroidX's AsyncLayoutInflater has the following limitations.
 * 1. It uses a single thread for all works.
 * 2. If the queue exceeds 10 items, the Main Thread will be delayed.
 * 3. It does not support setting a [LayoutInflater.Factory] nor [LayoutInflater.Factory2].
 * 4. There is no way to cancel ongoing inflation.
 */
class OkLayoutInflater : LifecycleEventObserver {

    private val tag = "AsyncInf"

    private lateinit var context: Context
    private var fragment: Fragment? = null
    private var componentLifecycle: Lifecycle? = null

    private val mInflater by lazy { BasicInflater(context) }
    private val coroutineContext = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + coroutineContext)

    /**
     * @param context For view inflation & adding Lifecycle Observer, if possible.
     */
    constructor(context: Context) {
        init(context)
    }

    /**
     * @param fragment For view inflation & adding Observer to the [Fragment.getViewLifecycleOwner].
     */
    constructor(fragment: Fragment) {
        this.fragment = fragment
        init(fragment.requireContext())
    }

    /**
     * @param view For child view inflation in Custom Views.
     * Inflation is cancelled if the view provided is detached from the window.
     */
    constructor(view: View) {
        this.context = view.context
        view.onViewDetachedFromWindow { cancelInflation() }
    }

    private fun init(context: Context) {
        this.context = context
        componentLifecycle = if (fragment != null) fragment!!.viewLifecycleOwner.lifecycle
        else if (context is LifecycleOwner) context.lifecycle
        else null

        if (componentLifecycle != null) {
            componentLifecycle!!.addObserver(this)
        } else {
            Log.d(
                tag,
                "Current context does not seem to have a Lifecycle, make sure to call `cancelInflation()` " +
                        "in your onDestroy or other appropriate lifecycle callback."
            )
        }
    }

    fun inflate(
        @LayoutRes resId: Int,
        parent: ViewGroup?,
        callback: suspend (view: View) -> Unit,
    ) {
        scope.launch {
            val view = inflateView(resId, parent)
            withContext(Dispatchers.Main) { callback.invoke(view) }
        }
    }

    fun cancelInflation() {
        coroutineContext.cancel()
        coroutineContext.cancelChildren()
    }

    private suspend fun inflateView(
        @LayoutRes resId: Int,
        parent: ViewGroup?,
    ): View = try {
        mInflater.inflate(resId, parent, false)
    } catch (ex: RuntimeException) {
        Log.e("AsyncInf", "AsyncInf Failed to inflate on bg thread. message=${ex.message}")

        // Some views need to be inflation-only in the main thread,
        // fall back to inflation in the main thread if there is an exception
        withContext(Dispatchers.Main) { mInflater.inflate(resId, parent, false) }
    }

    private class BasicInflater constructor(context: Context) : LayoutInflater(context) {

        override fun cloneInContext(newContext: Context): LayoutInflater {
            return BasicInflater(newContext)
        }

        override fun onCreateView(name: String, attrs: AttributeSet): View {
            for (prefix in sClassPrefixList) {
                try {
                    val view = createView(name, prefix, attrs)
                    if (view != null) return view
                } catch (_: ClassNotFoundException) {
                }
            }
            return super.onCreateView(name, attrs)
        }

        companion object {
            private val sClassPrefixList = arrayOf(
                "android.widget.", "android.webkit.", "android.app."
            )
        }

        init {
            if (context is AppCompatActivity) {
                val appCompatDelegate = context.delegate
                if (appCompatDelegate is Factory2) {
                    LayoutInflaterCompat.setFactory2(this, appCompatDelegate)
                }
            }
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            cancelInflation()
            componentLifecycle?.removeObserver(this)
        }
    }
}
