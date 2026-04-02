package com.sohaib.pdfmaker

import androidx.annotation.RequiresApi
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.aspose.words.Document as WordsDocument
import com.aspose.words.SaveFormat
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * PDF creation from images (iText) and Word documents (Aspose).
 * Input uses [Context.getContentResolver]; output goes to Downloads/[OUTPUT_FOLDER_NAME].
 */
class PdfMakerManager(private val context: Context) {

    sealed class ConversionResult {
        data class Success(val summary: String) : ConversionResult()
        data class Failure(val message: String, val error: Throwable? = null) : ConversionResult()
    }

    fun convertImagesToPdf(imageUris: List<Uri>): ConversionResult {
        if (imageUris.isEmpty()) {
            return ConversionResult.Failure("No images selected")
        }
        val fileName = "PM_images_${System.currentTimeMillis()}.pdf"
        return try {
            writePdf(fileName) { outputStream ->
                val pdfDocument = PdfDocument(PdfWriter(outputStream))
                val document = Document(pdfDocument)
                try {
                    val marginHorizontal = 60f
                    val marginVertical = 50f
                    document.setMargins(marginVertical, marginHorizontal, marginVertical, marginHorizontal)
                    document.pdfDocument.defaultPageSize = PageSize.DEFAULT
                    with(document.pdfDocument.documentInfo) {
                        addCreationDate()
                        title = fileName
                        author = DOC_AUTHOR
                        creator = DOC_CREATOR
                    }
                    val imageWidth =
                        document.pdfDocument.defaultPageSize.width - (marginHorizontal + marginHorizontal)
                    val imageHeight =
                        document.pdfDocument.defaultPageSize.height - (marginVertical + marginVertical)
                    for (uri in imageUris) {
                        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                            ?: error("Cannot read image")
                        val image = Image(ImageDataFactory.create(bytes))
                        image.scaleToFit(imageWidth, imageHeight)
                        document.add(image)
                    }
                } finally {
                    document.close()
                }
            }
        } catch (e: Exception) {
            ConversionResult.Failure(e.message ?: "Conversion failed", e)
        }
    }

    fun convertDocToPdf(docUri: Uri): ConversionResult {
        val fileName = "PM_doc_${System.currentTimeMillis()}.pdf"
        return try {
            writePdf(fileName) { outputStream ->
                val input = context.contentResolver.openInputStream(docUri)
                    ?: error("Cannot open document")
                input.use { stream ->
                    val doc = WordsDocument(stream)
                    doc.save(outputStream, SaveFormat.PDF)
                }
            }
        } catch (e: Exception) {
            ConversionResult.Failure(e.message ?: "Conversion failed", e)
        }
    }

    private fun writePdf(fileName: String, block: (OutputStream) -> Unit): ConversionResult {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            writePdfViaMediaStore(fileName, block)
        } else {
            writePdfViaLegacyFile(fileName, block)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun writePdfViaMediaStore(fileName: String, block: (OutputStream) -> Unit): ConversionResult {
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/$OUTPUT_FOLDER_NAME")
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }
        val collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI
        val uri = resolver.insert(collection, values)
            ?: return ConversionResult.Failure("Could not create file in Downloads")
        try {
            resolver.openOutputStream(uri)?.use(block)
                ?: return ConversionResult.Failure("Could not open file for writing")
            values.clear()
            values.put(MediaStore.MediaColumns.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
            return ConversionResult.Success("Downloads/$OUTPUT_FOLDER_NAME/$fileName")
        } catch (e: Exception) {
            resolver.delete(uri, null, null)
            throw e
        }
    }

    private fun writePdfViaLegacyFile(fileName: String, block: (OutputStream) -> Unit): ConversionResult {
        val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            OUTPUT_FOLDER_NAME
        )
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file = File(dir, fileName)
        FileOutputStream(file).use(block)
        return ConversionResult.Success(file.absolutePath)
    }

    companion object {
        const val OUTPUT_FOLDER_NAME = "Pdf Maker"
        private const val DOC_AUTHOR = "Pdf Maker"
        private const val DOC_CREATOR = "Sohaib Ahmed"
    }
}
