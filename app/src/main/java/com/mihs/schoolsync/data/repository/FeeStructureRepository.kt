// FeeStructureRepository.kt
package com.mihs.schoolsync.data.repository

import com.mihs.schoolsync.data.remote.FeeStructureApiService
import com.mihs.schoolsync.ui.finance.models.*
import com.mihs.schoolsync.utils.Result
import javax.inject.Inject

/**
 * Repository interface for fee structure management
 */
interface FeeStructureRepository {
    suspend fun getFeeStructures(activeOnly: Boolean = false): Result<List<FeeStructureResponse>>
    suspend fun getFeeStructure(structureId: Int): Result<FeeStructureResponse>
    suspend fun createFeeStructure(
        structure: FeeStructureCreateRequest,
        items: List<FeeItemCreateRequest>
    ): Result<FeeStructureResponse>
    suspend fun updateFeeStructure(
        structureId: Int,
        structure: FeeStructureUpdateRequest
    ): Result<FeeStructureResponse>
    suspend fun addFeeItem(
        structureId: Int,
        item: FeeItemCreateRequest
    ): Result<FeeStructureResponse>
    suspend fun deleteFeeItem(itemId: Int): Result<FeeStructureResponse>
}

/**
 * Implementation of fee structure repository
 */
class FeeStructureRepositoryImpl @Inject constructor(
    private val feeStructureApiService: FeeStructureApiService
) : FeeStructureRepository {

    override suspend fun getFeeStructures(activeOnly: Boolean): Result<List<FeeStructureResponse>> {
        return try {
            val response = feeStructureApiService.getFeeStructures(activeOnly)
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error(response.message() ?: "Failed to get fee structures")
            }
        } catch (e: Exception) {
            Result.Error("Error fetching fee structures: ${e.message}")
        }
    }

    override suspend fun getFeeStructure(structureId: Int): Result<FeeStructureResponse> {
        return try {
            val response = feeStructureApiService.getFeeStructure(structureId)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(response.message() ?: "Failed to get fee structure")
            }
        } catch (e: Exception) {
            Result.Error("Error fetching fee structure: ${e.message}")
        }
    }

    override suspend fun createFeeStructure(
        structure: FeeStructureCreateRequest,
        items: List<FeeItemCreateRequest>
    ): Result<FeeStructureResponse> {
        return try {
            val request = FeeStructureWithItemsCreate(structure, items)
            val response = feeStructureApiService.createFeeStructure(request)

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(response.message() ?: "Failed to create fee structure")
            }
        } catch (e: Exception) {
            Result.Error("Error creating fee structure: ${e.message}")
        }
    }

    override suspend fun updateFeeStructure(
        structureId: Int,
        structure: FeeStructureUpdateRequest
    ): Result<FeeStructureResponse> {
        return try {
            val response = feeStructureApiService.updateFeeStructure(structureId, structure)

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(response.message() ?: "Failed to update fee structure")
            }
        } catch (e: Exception) {
            Result.Error("Error updating fee structure: ${e.message}")
        }
    }

    override suspend fun addFeeItem(
        structureId: Int,
        item: FeeItemCreateRequest
    ): Result<FeeStructureResponse> {
        return try {
            val response = feeStructureApiService.addFeeItem(structureId, item)

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(response.message() ?: "Failed to add fee item")
            }
        } catch (e: Exception) {
            Result.Error("Error adding fee item: ${e.message}")
        }
    }

    override suspend fun deleteFeeItem(itemId: Int): Result<FeeStructureResponse> {
        return try {
            val response = feeStructureApiService.deleteFeeItem(itemId)

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(response.message() ?: "Failed to delete fee item")
            }
        } catch (e: Exception) {
            Result.Error("Error deleting fee item: ${e.message}")
        }
    }
}