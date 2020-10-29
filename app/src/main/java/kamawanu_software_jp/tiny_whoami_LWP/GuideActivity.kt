package kamawanu_software_jp.tiny_whoami_LWP

///import androidx.appcompat.app.AppCompatActivity

import android.app.Activity
import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.annotation.RequiresApi

///import javax.swing.JColorChooser.showDialog


class GuideActivity : Activity() {

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)

        val btn = findViewById<Button>(R.id.btn_install_me)
        btn.setOnClickListener {
            /// http://www.366service.com/jp/qa/4ba75e16f7caf20e5659479467cb96be
            installme()
        }
        val wv = findViewById<WebView>(R.id.wv_privacypolicy)
        //wv.webChromeClient = WebChromeClient()
        ///wv.webViewClient = WebViewClient()
        wv.loadUrl(
            //"https://gist.githubusercontent.com/kamawanu/d07650e31f9b359345bd3fbdba3790ab/raw/51f497bc685d770c7ed2049fa3379564da6f0a02/privacypolicy.txt"
            //"https://gist.githubusercontent.com/kamawanu/d07650e31f9b359345bd3fbdba3790ab/raw/master/privacypolicy.txt"
            "https://gist.githubusercontent.com/kamawanu/d07650e31f9b359345bd3fbdba3790ab/raw"
        )
        wv.setOnClickListener() {}
    }

    val REQUEST_SET_LIVE_WALLPAPER = 0

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun installme() {
        /**
         * http://www.366service.com/jp/qa/4ba75e16f7caf20e5659479467cb96be
         */
        var intent: Intent
        try {
            val component = ComponentName(packageName, "$packageName.LiveWallpaperWHOAMI")
            intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, component)
            startActivityForResult(intent, REQUEST_SET_LIVE_WALLPAPER)
        } catch (e3: ActivityNotFoundException) {
            // try the generic android wallpaper chooser next
            try {
                intent = Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER)
                startActivityForResult(intent, REQUEST_SET_LIVE_WALLPAPER)
            } catch (e2: ActivityNotFoundException) {
                // that failed, let's try the nook intent
                try {
                    intent = Intent()
                    intent.action = "com.bn.nook.CHANGE_WALLPAPER"
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    // everything failed, let's notify the user
                    // showDialog(DIALOG_NO_WALLPAPER_PICKER)
                }
            }
        }
    }

}
