package com.zaneodell.fossdocs

import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.zaneodell.fossdocs.utilities.DeviceUtils
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test for DeviceUtils, which will execute on an Android device or emulator.
 *
 * This class tests composable functions that rely on the Android framework,
 * like getting screen dimensions.
 */
@RunWith(AndroidJUnit4::class)
class DeviceUtilsInstrumentedTest {

    /**
     * The ComposeTestRule provides a testing environment for composable functions.
     * It allows us to set content and interact with it.
     */
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testGetDeviceAspectRatio_returnsPlausibleValue() {
        // Arrange: We need a place to store the result from the composable function.
        var aspectRatio = 0f

        // Act: Set the content to a composable that calls the function under test.
        composeTestRule.setContent {
            // Call the composable function to get the aspect ratio.
            aspectRatio = DeviceUtils.getDeviceAspectRatio()

            // You can also get the configuration directly here to verify against.
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp
            val screenHeight = configuration.screenHeightDp
            println("Device Screen Dimensions (Dp): Width=$screenWidth, Height=$screenHeight")
            println("Calculated Aspect Ratio: $aspectRatio")
        }

        // Assert: Check if the calculated aspect ratio is a plausible number.
        // A device's aspect ratio will almost always be greater than 0 and less than 3.
        // For example, a tall phone might be ~0.46 (e.g., 1080/2340) and a tablet in landscape
        // might be ~1.7 (e.g., 1280/800). This assertion is general enough for most devices.
        assertTrue("Aspect ratio should be a positive value", aspectRatio > .3f)
        assertTrue("Aspect ratio seems unexpectedly large", aspectRatio < 2.8f)

        // A more specific assertion could be made if you run the test on a known device,
        // but a general check is often sufficient.
    }
}