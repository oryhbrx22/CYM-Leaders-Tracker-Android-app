package app.cym.tracker

import android.os.Bundle
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

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
            0xFFF3F4F6.toInt()
        )

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true

            allowFileAccess = false
            allowContentAccess = false

            cacheMode = WebSettings.LOAD_DEFAULT

            builtInZoomControls = false
            displayZoomControls = false
            setSupportZoom(false)

            useWideViewPort = true
            loadWithOverviewMode = false

            mixedContentMode =
                WebSettings.MIXED_CONTENT_NEVER_ALLOW
        }

        /*
         * IMPORTANT:
         * We are loading the WORKING GitHub Pages site.
         */
        webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(
                view: WebView,
                url: String
            ) {
                super.onPageFinished(view, url)

                Toast.makeText(
                    this@MainActivity,
                    "CYM Tracker loaded",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        /*
         * JavaScript console logging.
         * Useful if Firebase/WebView has an error.
         */
        webView.webChromeClient = object : WebChromeClient() {

            override fun onConsoleMessage(
                consoleMessage: ConsoleMessage
            ): Boolean {

                android.util.Log.d(
                    "CYM_WEBVIEW",
                    "${consoleMessage.message()} " +
                        "(${consoleMessage.sourceId()}:" +
                        "${consoleMessage.lineNumber()})"
                )

                return true
            }
        }

        setContentView(webView)

        if (savedInstanceState == null) {

            webView.loadUrl(
                "https://oryhbrx22.github.io/CYM-Leaders-Tracker-App/"
            )

        } else {

            webView.restoreState(
                savedInstanceState
            )
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

        super.onSaveInstanceState(
            outState
        )
    }

    override fun onDestroy() {

        webView.apply {
            stopLoading()
            webChromeClient = null
            destroy()
        }

        super.onDestroy()
    }
}
