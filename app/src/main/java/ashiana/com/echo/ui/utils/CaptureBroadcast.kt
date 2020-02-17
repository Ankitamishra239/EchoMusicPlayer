package ashiana.com.echo.ui.utils

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import ashiana.com.echo.R
import ashiana.com.echo.ui.Fragment.SongPlayingFragment
import ashiana.com.echo.ui.activities.MainActivity
import java.lang.Exception

class CaptureBroadcast : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1?.action == Intent.ACTION_NEW_OUTGOING_CALL) {
            try {
                MainActivity.Statified.notificationManager?.cancel(1970)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                    SongPlayingFragment.Statified.mediaPlayer?.pause()
                    SongPlayingFragment.Statified.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            val tm: TelephonyManager =
                p0?.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager

            when (tm?.callState) {
                TelephonyManager.CALL_STATE_RINGING -> {

                    try {
                        MainActivity.Statified.notificationManager?.cancel(1970)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    try {
                        if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                            SongPlayingFragment.Statified.mediaPlayer?.pause()
                            SongPlayingFragment.Statified.playpauseImageButton?.setBackgroundResource(
                                R.drawable.play_icon
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                else -> {
                }


            }
        }
    }

}