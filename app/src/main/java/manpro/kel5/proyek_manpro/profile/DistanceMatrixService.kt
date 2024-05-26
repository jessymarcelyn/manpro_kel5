package manpro.kel5.proyek_manpro.profile

// DistanceMatrixService.kt
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DistanceMatrixService {
    @GET("json")
    fun getDistance(
        @Query("origins") origins: String,
        @Query("destinations") destinations: String,
        @Query("key") apiKey: String
    ): Call<DistanceMatrixResponse>
}

data class DistanceMatrixResponse(val rows: List<Row>)
data class Row(val elements: List<Element>)
data class Element(val distance: Distance)
data class Distance(val text: String, val value: Int)
