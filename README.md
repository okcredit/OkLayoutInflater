## OkLayoutInflater

AndroidX [AsyncLayoutInflater](https://developer.android.com/reference/androidx/asynclayoutinflater/view/AsyncLayoutInflater)
has some limitations. Here is an improved version of AsyncLayoutInflater with Coroutines.

1. Single thread to do all the inflate work
2. Inflate work is not lifecycle aware/There is no way to cancel ongoing inflation
3. Does not support LayoutInflater.Factory2.
4. The default size limit of the cache queue is 10. If it exceeds 10, it will cause the main thread
   to wait.

Using OkLayoutInflater, we have improved threading limitations.

### Usage

**Example in a Fragment**

```
private val okLayoutInflater by lazy { OkLayoutInflater(this) }

override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val loadingView = inflater.inflate(R.layout.loader_view, container, false)
    okLayoutInflater.inflate(contentLayoutId, container) {
        (loadingView as? ViewGroup)?.addView(it)
    }
    return loadingView
}
```

**Example of usage in a View**

```
private val okLayoutInflater by lazy { OkLayoutInflater(this) }

override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    okLayoutInflater.inflate(R.layout.transaction_view, this) { inflatedView ->
        removeAllViews()
        addView(inflatedView, LayoutParams.MATCH_PARENT)
    }
}
```

### Gradle Setup

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.okcredit:OkLayoutInflater:1.0'
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
