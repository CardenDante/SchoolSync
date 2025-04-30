// ReceiptRepository.kt
package com.mihs.schoolsync.data.repository

import android.content.Context
import com.mihs.schoolsync.data.remote.ReceiptApiService
import com.mihs.schoolsync.ui.finance.models.ReceiptResponse
import com.mihs.schoolsync.utils.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

/**
 * Repository interface for receipt-related operations
 */
interface ReceiptRepository {
    suspend fun getReceipt(paymentId: Int): Result<ReceiptResponse>
    suspend fun generateReceipt(paymentId: Int): Result<ReceiptResponse>
    suspend fun downloadReceiptPdf(receipt: ReceiptResponse): Result<File>
}

/**
 * Implementation of the receipt repository
 */
class ReceiptRepositoryImpl @Inject constructor(
    private val receiptApiService: ReceiptApiService,
    @ApplicationContext private val context: Context
) : ReceiptRepository {

    override suspend fun getReceipt(paymentId: Int): Result<ReceiptResponse> {
        return try {
            val response = receiptApiService.getReceipt(paymentId)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(response.message() ?: "Failed to get receipt")
            }
        } catch (e: Exception) {
            Result.Error("Error fetching receipt: ${e.message}")
        }
    }

    override suspend fun generateReceipt(paymentId: Int): Result<ReceiptResponse> {
        return try {
            val response = receiptApiService.generateReceipt(paymentId)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(response.message() ?: "Failed to generate receipt")
            }
        } catch (e: Exception) {
            Result.Error("Error generating receipt: ${e.message}")
        }
    }

    override suspend fun downloadReceiptPdf(receipt: ReceiptResponse): Result<File> {
        return withContext(Dispatchers.IO) {
            try {
                // Get PDF URL from receipt
                val pdfUrl = receipt.pdfUrl ?: return@withContext Result.Error("Receipt PDF URL not found")

                // Download PDF file
                val response = receiptApiService.downloadReceiptPdf(pdfUrl)

                if (!response.isSuccessful) {
                    return@withContext Result.Error("Failed to download receipt PDF")
                }

                val responseBody = response.body() ?: return@withContext Result.Error("Empty response body")

                // Save to file
                val downloadedFile = saveResponseBodyToFile(
                    responseBody,
                    "receipt_${receipt.receiptNumber.replace("/", "_")}.pdf"
                )

                Result.Success(downloadedFile)
            } catch (e: Exception) {
                Result.Error("Error downloading receipt PDF: ${e.message}")
            }
        }
    }

    private fun saveResponseBodyToFile(body: ResponseBody, filename: String): File {
        val downloadDir = File(context.getExternalFilesDir(null), "Receipts")
        if (!downloadDir.exists()) {
            downloadDir.mkdirs()
        }

        val file = File(downloadDir, filename)

        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null

        try {
            inputStream = body.byteStream()
            outputStream = FileOutputStream(file)

            val buffer = ByteArray(4096)
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            outputStream.flush()
            return file
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    }
}