package manpro.kel5.proyek_manpro

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class RutePagingSource(private val db: FirebaseFirestore) : PagingSource<QuerySnapshot, TravelSchedule>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, TravelSchedule> {
        return try {
            // Mendapatkan halaman saat ini
            val currentPage = params.key ?: db.collection("Rute")
                .limit(params.loadSize.toLong())
                .get()
                .await()

            val travelSchedules = mutableListOf<TravelSchedule>()
            for (document in currentPage.documents) {
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
                        estimasi,
                        price.toInt()
                    )
                )
            }

            // Mendapatkan halaman sebelumnya
            val prevPage = if (currentPage.isEmpty) null else db.collection("Rute")
                .endBefore(currentPage.documents.first())
                .limit(params.loadSize.toLong() * 2) // Ubah loadSize menjadi loadSize * 2
                .get()
                .await()

            // Mendapatkan halaman selanjutnya
            val nextPage = db.collection("Rute")
                .startAfter(currentPage.documents.last())
                .limit(params.loadSize.toLong() * 2) // Ubah loadSize menjadi loadSize * 2
                .get()
                .await()

            LoadResult.Page(
                data = travelSchedules,
                prevKey = prevPage,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }


    override fun getRefreshKey(state: PagingState<QuerySnapshot, TravelSchedule>): QuerySnapshot? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }
}
