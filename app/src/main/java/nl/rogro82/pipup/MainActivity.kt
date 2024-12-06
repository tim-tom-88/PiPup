package nl.rogro82.pipup

import android.app.Activity
import android.app.PictureInPictureParams
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.View
import android.widget.TextView
import nl.rogro82.pipup.Utils.getIpAddress

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize text views
        val textViewConnection = findViewById<TextView>(R.id.textViewServerAddress)
        val textViewServerAddress = findViewById<TextView>(R.id.textViewServerAddress)

        // Display server connection information
        when (val ipAddress = getIpAddress()) {
            is String -> {
                textViewConnection.setText(R.string.server_running)
                textViewServerAddress.apply {
                    visibility = View.VISIBLE
                    text = resources.getString(
                        R.string.server_address,
                        ipAddress,
                        PiPupService.SERVER_PORT
                    )
                }
            }
            else -> {
                textViewConnection.setText(R.string.no_network_connection)
                textViewServerAddress.visibility = View.INVISIBLE
            }
        }

        // Start service in foreground
        val serviceIntent = Intent(this, PiPupService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    // Trigger PiP mode when the user leaves the activity
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Define PiP aspect ratio (adjust as needed)
            val pipParams = PictureInPictureParams.Builder()
                .setAspectRatio(Rational(16, 9)) // 16:9 aspect ratio
                .build()
            enterPictureInPictureMode(pipParams)
        }
    }

    // Handle UI updates when entering/exiting PiP mode
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration?
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        // Modify UI based on PiP state
        if (isInPictureInPictureMode) {
            // Hide unnecessary UI elements in PiP mode
            findViewById<View>(R.id.textViewServerAddress).visibility = View.GONE
        } else {
            // Restore full UI when leaving PiP mode
            findViewById<View>(R.id.textViewServerAddress).visibility = View.VISIBLE
        }
    }
}
