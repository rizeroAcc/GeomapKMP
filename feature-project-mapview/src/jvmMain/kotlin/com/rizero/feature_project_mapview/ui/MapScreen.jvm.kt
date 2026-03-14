package com.rizero.feature_project_mapview.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rizero.feature_project_mapview.component.MapScreenComponent
import com.rizero.shared_core_component.theme.AppTheme
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.map.RenderOptions
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.style.rememberStyleState

@Composable
actual fun MapScreenContent(
    mapScreenComponent: MapScreenComponent,
    modifier: Modifier
) {
    val cameraState = rememberCameraState()
    val styleState = rememberStyleState()
    Row(
        modifier = Modifier.then(modifier)
    ) {
        Box(
            modifier = Modifier
                .background(AppTheme.Colors.DefaultPageBackgroundColor)
                .fillMaxWidth(0.5f)
                .fillMaxHeight()
        ){
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Other controls")
                Button(onClick = {}){
                    Text("Some button")
                }
            }
        }
        Box(Modifier.fillMaxWidth().fillMaxHeight()) {
            MaplibreMap(
                cameraState = cameraState,
                styleState = styleState,
                options = MapOptions(
                    renderOptions = RenderOptions.Standard,
                    ornamentOptions = OrnamentOptions.OnlyLogo
                ),
                baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty"),
            ){

            }
        }
    }
}