package com.zaneodell.fossdocs.utilities

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration


/**
 * Utility singleton class for device-related operations.
 */
object DeviceUtils {
    /**
     * Function that calculates the device's aspect ratio.
     */
    @Composable
    fun getDeviceAspectRatio(): Float {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp
        val screenHeight = configuration.screenHeightDp
        return screenWidth.toFloat() / screenHeight.toFloat()
    }
}