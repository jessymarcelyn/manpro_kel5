package manpro.kel5.proyek_manpro

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class RutePagingSource(private val db: FirebaseFirestore) : PagingSource<QuerySnapshot, TravelSchedule>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, TravelSchedule> {
        return try {
            val currentPage = params.key ?: db.collection("Rute").get().await()

            val travelSchedules = mutableListOf<TravelSchedule>()
            for (document in currentPage) {
                val idStopSource = document.getString("id_stop_source") ?: ""
                val idStopDest = document.getString("id_stop_dest") ?: ""
                val jamBerangkatStr = document.getString("jam_berangkat") ?: ""
                val estimasi = document.getLong("estimasi") ?: 0
                val price = document.getLong("price") ?: 0

                val waktuBerangkat = jamBerangkatStr.toDoubleOrNull()

                travelSchedules.add(
                    TravelSchedule(
                        idStopSource,
                        idStopDest,
                        waktuBerangkat.toString(),
                        estimasi.toInt(),
                        price.toInt()
                    )
                )
            }

            LoadResult.Page(
                data = travelSchedules,
                prevKey = null, // Since it's the first page
                nextKey = currentPage // Use the same query snapshot as the next key
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, TravelSchedule>): QuerySnapshot? {
        TODO("Not yet implemented")
    }
}
