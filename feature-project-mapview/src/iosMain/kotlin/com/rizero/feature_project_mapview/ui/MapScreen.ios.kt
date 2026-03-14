package com.rizero.feature_project_mapview.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rizero.feature_project_mapview.component.MapScreenComponent
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.map.RenderOptions
import org.maplibre.compose.material3.CompassButton
import org.maplibre.compose.material3.ExpandingAttributionButton
import org.maplibre.compose.material3.ScaleBar
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.style.rememberStyleState

@Composable
actual fun MapScreenContent(
    mapScreenComponent: MapScreenComponent,
    modifier: Modifier
) {
    val cameraState = rememberCameraState()
    val styleState = rememberStyleState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
    ) {
        MaplibreMap(
            cameraState = cameraState,
            styleState = styleState,
            options = MapOptions(
                renderOptions = RenderOptions.Standard,
                ornamentOptions = OrnamentOptions.OnlyLogo
            ),
            baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty"),
        )

        Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            ScaleBar(cameraState.metersPerDpAtTarget, modifier = Modifier.align(Alignment.TopStart))
            CompassButton(cameraState, modifier = Modifier.align(Alignment.TopEnd))
            ExpandingAttributionButton(
                cameraState = cameraState,
                styleState = styleState,
                modifier = Modifier.align(Alignment.BottomEnd),
                contentAlignment = Alignment.BottomEnd,
            )
        }
    }
}