// ----------------------------------------------------------------------------
// Pedro Caso
// 241286
// Lab 8 plataformas
// ----------------------------------------------------------------------------

package com.example.lab8platadormas_pc

import com.squareup.moshi.Json
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class PexelsResponse(
    val page: Int,
    @Json(name = "per_page") val perPage: Int,
    val photos: List<PexelsPhoto>,
    @Json(name = "next_page") val nextPage: String?
)

@Parcelize
data class PexelsPhoto(
    val id: Long,
    val width: Int,
    val height: Int,
    val url: String,
    val photographer: String,
    @Json(name = "photographer_url") val photographerUrl: String,
    @Json(name = "photographer_id") val photographerId: Long,
    @Json(name = "avg_color") val avgColor: String?,
    val src: PexelsSrc,
    val liked: Boolean,
    val alt: String?
) : Parcelable

@Parcelize
data class PexelsSrc(
    val original: String,
    val large2x: String?,
    val large: String?,
    val medium: String?,
    val small: String?,
    val portrait: String?,
    val landscape: String?,
    val tiny: String?
) : Parcelable
