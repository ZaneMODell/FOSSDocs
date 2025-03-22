import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.zaneodell.fossdocs.utilities.PdfBitmapConverter
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream

@RunWith(AndroidJUnit4::class)
class PdfBitmapConverterInstrumentedTest {

    @Test
    fun testPdfToBitmaps() = runBlocking<Unit> {
        // Get both contexts
        val testContext = InstrumentationRegistry.getInstrumentation().context
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        // Verify test PDF exists in TEST assets
        val testAssets = testContext.assets.list("")?.toList() ?: emptyList()
        println("Test assets: $testAssets")
        assertTrue("test.pdf missing from test assets", testAssets.contains("test.pdf"))

        // Create file in APP'S cache directory (not test's cache)
        val outputFile = File(appContext.cacheDir, "test.pdf").apply {
            if (exists()) delete()
        }

        // Copy from TEST assets to APP cache
        testContext.assets.open("test.pdf").use { input ->
            FileOutputStream(outputFile).use { output ->
                input.copyTo(output)
                output.flush()
            }
        }

        println("File location: ${outputFile.absolutePath}")
        assertTrue("File not created", outputFile.exists())

        // Use app context for the converter
        val converter = PdfBitmapConverter(appContext)
        val uri = Uri.fromFile(outputFile)

        val bitmaps = converter.pdfToBitmaps(uri)
        assertNotNull(bitmaps)
        assertTrue(bitmaps.isNotEmpty())
        assertTrue(bitmaps.size == 3)
    }
}