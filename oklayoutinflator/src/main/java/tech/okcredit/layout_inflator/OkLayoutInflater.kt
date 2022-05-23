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
import kotlinx.coroutines.*

/**
 * Androidx AsyncLayoutInflater has the following limitations.
 * 1. Use a single thread for all works. Here we are introducing coroutine
 * 2. If the queue exceeds 10 items, the main thread will be delayed.
 * 3. does not support setting a {@link LayoutInflater.Factory} nor {@link LayoutInflater.Factory2}. Here we are Adding support
 * 4. There is no way to cancel ongoing inflation. here we are exposing coroutine job cancel method
 */
class OkLayoutInflater(context: Context) {

    private val mInflater: LayoutInflater = BasicInflater(context)
    private val coroutineContext = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + coroutineContext)

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

    fun cancel() {
        coroutineContext.cancelChildren()
    }

    private suspend fun inflateView(
        @LayoutRes resId: Int,
        parent: ViewGroup?,
    ): View = try {
        mInflater.inflate(resId, parent, false)
    } catch (ex: RuntimeException) {
        Log.e("AsyncInf", "AsyncInf Failed to inflate on bg thread. message=${ex.message}")

        // Some views need to be inflation-only in the main thread, fall back to inflation in the main thread if there is an exception
        withContext(Dispatchers.Main) {
            mInflater.inflate(resId, parent, false)
        }
    }

    private class BasicInflater constructor(context: Context?) : LayoutInflater(context) {

        override fun cloneInContext(newContext: Context): LayoutInflater {
            return BasicInflater(newContext)
        }

        @Throws(ClassNotFoundException::class)
        override fun onCreateView(name: String, attrs: AttributeSet): View {
            for (prefix in sClassPrefixList) {
                try {
                    val view = createView(name, prefix, attrs)
                    if (view != null) {
                        return view
                    }
                } catch (e: ClassNotFoundException) {
                }
            }
            return super.onCreateView(name, attrs)
        }

        companion object {
            private val sClassPrefixList = arrayOf(
                "android.widget.",
                "android.webkit.",
                "android.app."
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
}
