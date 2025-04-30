// FeeStructureApiService.kt
package com.mihs.schoolsync.data.remote

import com.mihs.schoolsync.ui.finance.models.*
import retrofit2.Response
import retrofit2.http.*

/**
 * API service for fee structure management
 */
interface FeeStructureApiService {
    /**
     * Get all fee structures
     */
    @GET("/api/v1/fees/structures")
    suspend fun getFeeStructures(
        @Query("active_only") activeOnly: Boolean = false
    ): Response<List<FeeStructureResponse>>

    /**
     * Get a specific fee structure
     */
    @GET("/api/v1/fees/structures/{structure_id}")
    suspend fun getFeeStructure(
        @Path("structure_id") structureId: Int
    ): Response<FeeStructureResponse>

    /**
     * Create a new fee structure with items
     */
    @POST("/api/v1/fees/structures")
    suspend fun createFeeStructure(
        @Body request: FeeStructureWithItemsCreate
    ): Response<FeeStructureResponse>

    /**
     * Update an existing fee structure
     */
    @PUT("/api/v1/fees/structures/{structure_id}")
    suspend fun updateFeeStructure(
        @Path("structure_id") structureId: Int,
        @Body structure: FeeStructureUpdateRequest
    ): Response<FeeStructureResponse>

    /**
     * Add a fee item to an existing structure
     */
    @POST("/api/v1/fees/structures/{structure_id}/items")
    suspend fun addFeeItem(
        @Path("structure_id") structureId: Int,
        @Body item: FeeItemCreateRequest
    ): Response<FeeStructureResponse>

    /**
     * Delete a fee item
     */
    @DELETE("/api/v1/fees/items/{item_id}")
    suspend fun deleteFeeItem(
        @Path("item_id") itemId: Int
    ): Response<FeeStructureResponse>
}