package kamawanu_software_jp.tiny_whoami_LWP

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Build
import android.os.Handler
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import androidx.annotation.RequiresApi
import java.net.Inet4Address
import java.net.NetworkInterface


class LiveWallpaperWHOAMI : WallpaperService() {

    private val refreshTimer = Handler()

    override fun onCreateEngine(): WallpaperService.Engine {
        return LiveWallpaperEngine()
    }

    inner class LiveWallpaperEngine : WallpaperService.Engine() {

        private var screen_width = 0

        private var screen_height = 0

        private var isshowing = true

        override fun onSurfaceCreated(holder: SurfaceHolder?) {
            super.onSurfaceCreated(holder)
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder?,
            format: Int,
            width: Int,
            height: Int
        ) {
            super.onSurfaceChanged(holder, format, width, height)
            screen_width = width
            screen_height = height
            surfaceDrawer()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
            isshowing = false
            refreshTimer.removeCallbacks(foregroundInvoker)
        }

        override fun onVisibilityChanged(visibility: Boolean) {
            super.onVisibilityChanged(visibility)
            isshowing = visibility
            if (visibility) {
                surfaceDrawer()
            } else {
                refreshTimer.removeCallbacks(foregroundInvoker)
            }
        }

        private fun surfaceDrawer() {
            val canvas = surfaceHolder.lockCanvas()
            drawInformations(canvas)
            surfaceHolder.unlockCanvasAndPost(canvas)

            val interval = 10000L
            refreshTimer.removeCallbacks(foregroundInvoker)
            if (isshowing) {
                refreshTimer.postDelayed(foregroundInvoker, interval)
            }
        }

        private val foregroundInvoker = Runnable { surfaceDrawer() }

        private fun buildInformationTexts(): List<String> {
            val psb = ArrayList<String>()
            //psb.add(android.os.Build.BRAND)
            psb.add(Build.MANUFACTURER)
            psb.add(Build.MODEL)
            psb.add(Build.SERIAL)
            ///psb.add(Build.ID)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                psb.add(getABIS())
            } else {
                psb.add(Build.CPU_ABI)
            }
            psb.add(getDPI())
            psb.add(Build.TIME.toString())

            if (BuildConfig.VERSION_NAME != "1.0") {
                val ipaddr: String? =
                    try {
                        getIpv4HostAddress()
                    } catch (ex: java.net.SocketException) {
                        null
                    }

                psb.add(ipaddr ?: "IP-address: <need permission.INTERNET>")
            }

            return psb
        }

        private fun getDPI(): String {
            val dm = resources.displayMetrics
            return "V:%d<%6.2fdpi> ,H:%d<%6.2fdpi>".format(
                dm.heightPixels,
                dm.xdpi,
                dm.widthPixels,
                dm.ydpi
            )
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun getABIS(): String {
            return Build.SUPPORTED_ABIS.first()
        }

        /**
         * android - How to get IP address of the device from code? - Stack Overflow https://stackoverflow.com/questions/6064510/how-to-get-ip-address-of-the-device-from-code/58348312#58348312
         */
        private fun getIpv4HostAddress(): String? {
            NetworkInterface.getNetworkInterfaces()?.toList()?.map { networkInterface ->
                networkInterface.inetAddresses?.toList()?.find {
                    !it.isLoopbackAddress && it is Inet4Address
                }?.let { return it.hostAddress }
            }
            return null
        }

        private fun buildTextPath(xx: Float, yy: Float): Path {
            val path0 = Path()
            path0.moveTo(xx, yy)
            path0.lineTo(xx + 900F, yy - 200F)
            return path0
        }

        private fun drawInformations(canvas: Canvas) {
            ////canvas.drawColor(Color.WHITE)
            val text_paint = Paint()
            text_paint.color = Color.WHITE
            text_paint.textSize = 50F

            var xx = 100f
            var yy = 500f

            for (linestr in buildInformationTexts()) {
                val path0 = buildTextPath(xx, yy)
                canvas.drawTextOnPath(linestr, path0, 0F, 0F, text_paint)
                ////canvas.drawPath(path0, guide_paint)
                yy += 60F
                xx += 10F
            }

            val appstr =
                BuildConfig.APPLICATION_ID + ":" + BuildConfig.VERSION_NAME + "@" + BuildConfig.BUILD_TYPE
            text_paint.color = Color.GREEN
            text_paint.textSize = 30F
            canvas.drawTextOnPath(appstr, buildTextPath(xx, yy), 0F, 0F, text_paint)
        }

    }

}
