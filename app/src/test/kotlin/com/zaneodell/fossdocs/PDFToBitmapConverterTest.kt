
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.zaneodell.fossdocs.utilities.PdfBitmapConverter
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.io.FileNotFoundException

//@TODO FIGURE OUT HOW TO GET THIS TEST TO WORK, PDF FILE IS NOT BEING READ/ACCESSED PROPERLY

@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [Build.VERSION_CODES.TIRAMISU],
    instrumentedPackages = ["androidx.loader.content"]
)
class PdfBitmapConverterTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private lateinit var converter: PdfBitmapConverter
    private lateinit var testPdfUri: Uri
    private lateinit var testPdfFile: File

    @Before
    fun setUp() {
        val pdfInputStream = javaClass.classLoader?.getResourceAsStream("raw/test.pdf")
            ?: throw FileNotFoundException("raw/test.pdf not in test resources")

        testPdfFile = createTempFile(suffix = ".pdf").apply {
            outputStream().use { output ->
                pdfInputStream.copyTo(output)
            }
        }
        testPdfUri = Uri.fromFile(testPdfFile)
        converter = PdfBitmapConverter(context)
    }

    @After
    fun tearDown() {
        converter.renderer?.close()
        testPdfFile.delete()
    }

    @Test
    fun `converts pdf to bitmaps correctly`() {
        runBlocking {
            val result = converter.pdfToBitmaps(testPdfUri)
            assertNotNull(result)
            assertTrue(result.isNotEmpty())
        }
    }

    @Test
    fun `creates correct number of pages`() {
        runBlocking {
            val pageCount = 3 // Replace with the expected page count
            val result = converter.pdfToBitmaps(testPdfUri)
            assertEquals(pageCount, result.size)
        }
    }

    @Test(expected = FileNotFoundException::class)
    fun `handles invalid pdf uri gracefully`() {
        runBlocking {
            converter.pdfToBitmaps(Uri.parse("invalid_uri"))
        }
    }
}