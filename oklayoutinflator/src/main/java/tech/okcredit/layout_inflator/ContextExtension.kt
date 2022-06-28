package tech.okcredit.layout_inflator

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity


/** get view LifeCycleOwner from Context. */
fun Context?.getLifeCycleOwner(): AppCompatActivity? = when (this) {
    is ContextWrapper -> if (this is AppCompatActivity) this else this.baseContext.getLifeCycleOwner()
    else -> null
}