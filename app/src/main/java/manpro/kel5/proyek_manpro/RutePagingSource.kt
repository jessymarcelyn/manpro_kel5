import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import manpro.kel5.proyek_manpro.TravelSchedule

class RutePagingSource(
    private val db: FirebaseFirestore,
    private var bus: Boolean,
    private var train: Boolean,
    private var _filterOpt: Int
) : PagingSource<QuerySnapshot, TravelSchedule>() {

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
                val idTransportasi = document.getString("id_transportasi") ?: ""

                val waktuBerangkat = jamBerangkatStr.toDoubleOrNull()

                val travelSchedule = TravelSchedule(
                    idStopSource,
                    idStopDest,
                    waktuBerangkat.toString(),
                    estimasi.toInt(),
                    price.toInt(),
                    idTransportasi
                )

                // Apply filtering based on bus and train flags
                if ((bus && train) || (!bus && !train)) {
                    // Include all routes
                    travelSchedules.add(travelSchedule)
                } else if (bus && travelSchedule.idTranspor.startsWith("B")) {
                    // Include bus routes
                    travelSchedules.add(travelSchedule)
                } else if (train && travelSchedule.idTranspor.startsWith("T")) {
                    // Include train routes
                    travelSchedules.add(travelSchedule)
                }
            }

            val prevKey = params.key
            val nextKey = if (currentPage.size() > 0) currentPage else null

            LoadResult.Page(
                data = travelSchedules,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, TravelSchedule>): QuerySnapshot? {
        return null
    }
}
