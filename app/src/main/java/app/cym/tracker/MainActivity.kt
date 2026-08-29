package app.cym.tracker

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import java.util.Base64

class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        WindowInsetsControllerCompat(
            window,
            window.decorView
        ).isAppearanceLightNavigationBars = true

        webView = WebView(this)

        webView.setBackgroundColor(
            0xFFF5F5F5.toInt()
        )

        setContentView(webView)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true

            allowFileAccess = false
            allowContentAccess = false

            mediaPlaybackRequiresUserGesture = false

            cacheMode = WebSettings.LOAD_DEFAULT

            builtInZoomControls = false
            displayZoomControls = false
            setSupportZoom(false)

            useWideViewPort = true
            loadWithOverviewMode = true
        }

        webView.webViewClient = object : WebViewClient() {}

        webView.webChromeClient = WebChromeClient()

        webView.addJavascriptInterface(
            AndroidBridge(),
            "Android"
        )

        if (savedInstanceState == null) {
            webView.loadUrl(
                "file:///android_asset/index.html"
            )
        } else {
            webView.restoreState(savedInstanceState)
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        finish()
                    }
                }
            }
        )
    }

    override fun onSaveInstanceState(
        outState: Bundle
    ) {
        webView.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    inner class AndroidBridge {

        @JavascriptInterface
        fun saveImage(dataUrl: String) {

            try {

                val encoded = dataUrl.substringAfter(
                    "base64,",
                    ""
                )

                val bytes =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Base64.getDecoder().decode(encoded)
                    } else {
                        android.util.Base64.decode(
                            encoded,
                            android.util.Base64.DEFAULT
                        )
                    }

                val values = ContentValues().apply {

                    put(
                        MediaStore.Images.Media.DISPLAY_NAME,
                        "cym-tracker-${System.currentTimeMillis()}.png"
                    )

                    put(
                        MediaStore.Images.Media.MIME_TYPE,
                        "image/png"
                    )

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                        put(
                            MediaStore.Images.Media.RELATIVE_PATH,
                            Environment.DIRECTORY_PICTURES +
                                    "/CYM Tracker"
                        )

                        put(
                            MediaStore.Images.Media.IS_PENDING,
                            1
                        )
                    }
                }

                val uri: Uri =
                    contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values
                    )
                        ?: throw IllegalStateException(
                            "Could not create gallery entry"
                        )

                contentResolver
                    .openOutputStream(uri)
                    .use { output ->

                        if (output == null) {
                            throw IllegalStateException(
                                "Could not open gallery stream"
                            )
                        }

                        output.write(bytes)
                        output.flush()
                    }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    contentResolver.update(
                        uri,
                        ContentValues().apply {
                            put(
                                MediaStore.Images.Media.IS_PENDING,
                                0
                            )
                        },
                        null,
                        null
                    )
                }

                runOnUiThread {

                    Toast.makeText(
                        this@MainActivity,
                        "Image saved to Pictures/CYM Tracker",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {

                runOnUiThread {

                    Toast.makeText(
                        this@MainActivity,
                        "Could not save image: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
