package ru.ifr0z.fabuserlocation.example

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Color.BLUE
import android.graphics.PointF
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat.checkSelfPermission
import com.yandex.mapkit.Animation
import com.yandex.mapkit.Animation.Type.SMOOTH
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment.LEFT
import com.yandex.mapkit.logo.VerticalAlignment.BOTTOM
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateSource
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider.fromResource
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), UserLocationObjectListener, CameraListener {

    private lateinit var userLocationLayer: UserLocationLayer

    private var routeStartLocation = Point(0.0, 0.0)

    private var permissionLocation = false
    private var followUserLocation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.setApiKey(mapKitApiKey)
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)

        checkPermission()

        userInterface()
    }

    private fun checkPermission() {
        val permissionLocation = checkSelfPermission(this, ACCESS_FINE_LOCATION)
        if (permissionLocation != PERMISSION_GRANTED) {
            requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), requestPermissionLocation)
        } else {
            onMapReady()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            requestPermissionLocation -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                    onMapReady()
                }

                return
            }
        }
    }

    private fun userInterface() {
        val mapLogoAlignment = Alignment(LEFT, BOTTOM)
        map_v.map.logo.setAlignment(mapLogoAlignment)

        user_location_fab.setOnClickListener {
            if (permissionLocation) {
                cameraUserPosition()

                followUserLocation = true
            } else {
                checkPermission()
            }
        }
    }

    private fun onMapReady() {
        userLocationLayer = map_v.map.userLocationLayer
        userLocationLayer.isEnabled = true
        userLocationLayer.isHeadingEnabled = true
        userLocationLayer.setObjectListener(this)

        map_v.map.addCameraListener(this)

        cameraUserPosition()

        permissionLocation = true
    }

    private fun cameraUserPosition() {
        if (userLocationLayer.cameraPosition() != null) {
            routeStartLocation = userLocationLayer.cameraPosition()!!.target
            map_v.map.move(
                CameraPosition(routeStartLocation, 16f, 0f, 0f), Animation(SMOOTH, 1f), null
            )
        } else {
            map_v.map.move(CameraPosition(Point(0.0, 0.0), 16f, 0f, 0f))
        }
    }

    override fun onCameraPositionChanged(
        p0: Map, p1: CameraPosition, p2: CameraUpdateSource, finish: Boolean
    ) {
        if (finish) {
            if (followUserLocation) {
                setAnchor()
            }
        } else {
            if (!followUserLocation) {
                noAnchor()
            }
        }
    }

    private fun setAnchor() {
        userLocationLayer.setAnchor(
            PointF((map_v.width * 0.5).toFloat(), (map_v.height * 0.5).toFloat()),
            PointF((map_v.width * 0.5).toFloat(), (map_v.height * 0.83).toFloat())
        )

        user_location_fab.setImageResource(R.drawable.ic_my_location_black_24dp)

        followUserLocation = false
    }

    private fun noAnchor() {
        userLocationLayer.resetAnchor()

        user_location_fab.setImageResource(R.drawable.ic_location_searching_black_24dp)
    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        setAnchor()

        userLocationView.pin.setIcon(fromResource(this, R.drawable.user_arrow))
        userLocationView.arrow.setIcon(fromResource(this, R.drawable.user_arrow))
        userLocationView.accuracyCircle.fillColor = BLUE
    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {}

    override fun onObjectRemoved(p0: UserLocationView) {}

    override fun onStop() {
        map_v.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        map_v.onStart()
    }

    companion object {
        /**
         * Replace "your_api_key" with a valid developer key.
         * You can get it at the https://developer.tech.yandex.ru/ website.
         */
        const val mapKitApiKey = "your_api_key"
        const val requestPermissionLocation = 1
    }
}
