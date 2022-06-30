package tech.okcredit.layout_inflator

import android.view.View
import androidx.core.view.ViewCompat

// taken directly from the core-ktx artifact
internal inline fun View.onViewDetachedFromWindow(crossinline actionOnDetach: () -> Unit) {
    if (!ViewCompat.isAttachedToWindow(this)) {
        actionOnDetach()
    } else {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(view: View) {}
            override fun onViewDetachedFromWindow(view: View) {
                removeOnAttachStateChangeListener(this)
                actionOnDetach()
            }
        })
    }
}