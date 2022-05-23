## CoroutineLayoutInflater

[AsyncLayoutInflater](https://developer.android.com/reference/androidx/asynclayoutinflater/view/AsyncLayoutInflater) has some limitations. Here is an improved version of AsyncLayoutInflater with coroutine.

1. Single thread to do all the inflate work
2. Inflatework is not lifecycle aware/There is no way to cancel ongoing inflation
3. Does not support LayoutInflater.Factory2.
4. The default size limit of the cache queue is 10. If it exceeds 10, it will cause the main thread to wait.

Using CoroutineLayoutInflater, we have improved threading limitations.


### Usage
 **Example of usage in fragment**

```
private val asyncLayoutInflater by lazy { AsyncCoroutineLayoutInflater(requireContext()) }

override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.loader_view, container, false)

        asyncLayoutInflater.inflate(
            contentLayoutId,
            container
        ) {
            (v as? ViewGroup)?.addView(it)
        }
        return v
}

override fun onDestroyView() {
    asyncLayoutInflater.cancel()
    super.onDestroyView()
}
```

 **Example of usage in view**

```
private val asyncLayoutInflater by lazy { AsyncCoroutineLayoutInflater(context) }

override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    LayoutInflater.from(context).inflate(R.layout.viewstub_customer_tx_view, this, true)
    asyncLayoutInflater.inflate(
        merchant.okcredit.accounting.R.layout.transaction_view,
        this
    ) {
        removeAllViews()
        addView(it, LayoutParams.MATCH_PARENT)
    }
}

override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    asyncLayoutInflater.cancel()
}
```

### Gradle Setup

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.okcredit:coroutinelayoutinflator:1.0'
}
```

### License

    Copyright 2022 OkCredit.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
