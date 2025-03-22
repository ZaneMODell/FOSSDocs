import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
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

@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [Build.VERSION_CODES.TIRAMISU],
    instrumentedPackages = ["androidx.loader.content"]
)
class PdfBitmapConverterTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private lateinit var converter: PdfBitmapConverter

    private val testPdfPath = javaClass.classLoader?.getResource("testres/test.pdf")?.path
        ?: throw FileNotFoundException("testres/test.pdf not in test resources")
    private lateinit var testPdfUri: Uri

    @Before
    fun setUp() {
        val testPdfFile = File(testPdfPath).also {
            require(it.exists()) { "Test PDF not found at: $testPdfPath" }
        }
        testPdfUri = Uri.parse("file://$testPdfPath")
        converter = PdfBitmapConverter(context)
    }

    @After
    fun tearDown() {
        converter.renderer?.close()
    }

    @Test
    fun `converts pdf to bitmaps correctly`() {
        runBlocking {
            val result = converter.pdfToBitmaps(testPdfUri)
            assertNotNull(result)
            assertTrue(result.isNotEmpty())
            assertTrue(result.all { it is Bitmap })
        }
    }

    @Test
    fun `creates correct number of pages`() {
        runBlocking {
            val pageCount = PdfRenderer(
                ParcelFileDescriptor.open(
                    File(testPdfPath),
                    ParcelFileDescriptor.MODE_READ_ONLY
                )
            ).use { it.pageCount }

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