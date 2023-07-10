package com.example.mapsforge_offlinemap

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.example.mapsforge_offlinemap.databinding.ActivityMainBinding
import org.mapsforge.core.model.LatLong
import org.mapsforge.core.util.MercatorProjection
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.android.view.MapView
import org.mapsforge.map.layer.overlay.Marker
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.rendertheme.InternalRenderTheme
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapController
import org.osmdroid.views.overlay.ItemizedOverlay
import org.osmdroid.views.overlay.Overlay
import java.io.FileInputStream


class MainActivity : AppCompatActivity() {

    companion object{
        val TURKEY = LatLong(39.924413, 32.814961)
    }

    private lateinit var b: ActivityMainBinding;
    private lateinit var mapView: MapView
    private lateinit var overlayList: ItemizedOverlay<OverlayItem>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AndroidGraphicFactory.createInstance(application)

        Configuration.getInstance().load(applicationContext, androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext))

        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        mapView = findViewById(R.id.map)


        val contract = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){result->
            result?.data?.data?.let { uri->
                openMap(uri)
            }
        }
        b.button.setOnClickListener{
            contract.launch(
                Intent(
                    Intent.ACTION_OPEN_DOCUMENT
                ).apply {
                    type = "*/*"
                    addCategory(Intent.CATEGORY_OPENABLE)
                }
            )
        }

        // Harita üzerinde marker yerleştirmek için bir marker katmanı oluştur
        overlayList = ItemizedIconOverlay<OverlayItem>(this, ArrayList(), null)
        


    }

    fun openMap(uri: Uri){
        b.map.mapScaleBar.isVisible = true
        b.map.setBuiltInZoomControls(true)
        val cache = AndroidUtil.createTileCache(
            this,
            "mycache",
            b.map.model.displayModel.tileSize,
            1f,
            b.map.model.frameBufferModel.overdrawFactor
        )

        val stream = contentResolver.openInputStream(uri) as FileInputStream

        val mapStore = MapFile(stream)

        val renderLayer = TileRendererLayer(
            cache,
            mapStore,
            b.map.model.mapViewPosition,
            AndroidGraphicFactory.INSTANCE
        )

        renderLayer.setXmlRenderTheme(
            InternalRenderTheme.DEFAULT
        )

        b.map.layerManager.layers.add(renderLayer)

        b.map.setCenter(TURKEY)
        b.map.setZoomLevel(10)
    }

}