import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import manpro.kel5.proyek_manpro.TravelSchedule
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RutePagingSource(
    private val db: FirebaseFirestore,
    private val bus: Boolean,
    private val train: Boolean,
    private val filterOpt: Int
) : PagingSource<DocumentSnapshot, TravelSchedule>() {

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, TravelSchedule> {
        return try {
            val currentPage = params.key?.let {
                db.collection("Rute")
                    .startAfter(it)
                    .limit(params.loadSize.toLong())
                    .get()
                    .await()
            } ?: db.collection("Rute")
                .limit(params.loadSize.toLong())
                .get()
                .await()

            val travelSchedules = mutableListOf<TravelSchedule>()
            for (document in currentPage.documents) {
                val idStopSource = document.getString("id_stop_source") ?: ""
                val idStopDest = document.getString("id_stop_dest") ?: ""
                val jamBerangkatStr = document.getString("jam_berangkat") ?: ""
                val jamSampai = document.getString("jam_sampai") ?: ""
                val estimasi = document.getLong("estimasi") ?: 0
                val price = document.getLong("price") ?: 0
                val idTransportasi = document.getString("id_transportasi") ?: ""

                val travelSchedule = TravelSchedule(
                    idStopSource,
                    idStopDest,
                    jamBerangkatStr,
                    jamSampai,
                    estimasi.toInt(),
                    price.toInt(),
                    idTransportasi
                )

                // Apply filtering based on bus and train flags
                if ((bus && train) || (!bus && !train)) {
                    travelSchedules.add(travelSchedule)
                } else if (bus && travelSchedule.idTranspor.startsWith("B")) {
                    travelSchedules.add(travelSchedule)
                } else if (train && travelSchedule.idTranspor.startsWith("T")) {
                    travelSchedules.add(travelSchedule)
                }
            }

            // Log filter option and the number of schedules before sorting
            Log.d("dvdv", "Filter Option: $filterOpt, Before Sorting: ${travelSchedules.size}")

            // Durasi tercepat
            if (filterOpt == 1) {
                val timeFormat = SimpleDateFormat("HHmm", Locale.getDefault())
                travelSchedules.sortBy { schedule ->
                    try {
                        val jamBerangkat = schedule.waktuBerangkat
                        val jamSampai = schedule.waktuSampai

                        // Parsing departure time
                        val departureDate = timeFormat.parse(jamBerangkat)
                        val departureCalendar = Calendar.getInstance().apply { time = departureDate }
                        val departureHours = departureCalendar.get(Calendar.HOUR_OF_DAY)
                        val departureMinutes = departureCalendar.get(Calendar.MINUTE)
                        val departureMinutesOfDay = departureHours * 60 + departureMinutes

                        // Parsing arrival time
                        val arrivalDate = timeFormat.parse(jamSampai)
                        val arrivalCalendar = Calendar.getInstance().apply { time = arrivalDate }
                        val arrivalHours = arrivalCalendar.get(Calendar.HOUR_OF_DAY)
                        val arrivalMinutes = arrivalCalendar.get(Calendar.MINUTE)
                        val arrivalMinutesOfDay = arrivalHours * 60 + arrivalMinutes

                        // Calculate duration
                        val duration = if (arrivalMinutesOfDay >= departureMinutesOfDay) {
                            arrivalMinutesOfDay - departureMinutesOfDay
                        } else {
                            (24 * 60 - departureMinutesOfDay) + arrivalMinutesOfDay // Handle next day arrival
                        }

                        duration
                    } catch (e: Exception) {
                        Log.e("dvdv", "Error parsing time: ${schedule.waktuBerangkat} - ${schedule.waktuSampai}", e)
                        Int.MAX_VALUE // Sort invalid times to the end
                    }
                }
                //biaya termurah
            }  else if (filterOpt == 2) {
                val sortedRute = travelSchedules.sortedBy { schedule ->
                    schedule.biaya
                }

                travelSchedules.clear()
                travelSchedules.addAll(sortedRute)

            //waktu berangkat terpagi
        }  else if (filterOpt == 3) {
                travelSchedules.sortBy { schedule ->
                    try {
                        // Extract hours and minutes from the departure time
                        val jamBerangkat = schedule.waktuBerangkat
                        val hours = jamBerangkat.substring(0, 2).toInt()
                        val minutes = jamBerangkat.substring(2).toInt()

                        // Convert hours and minutes into minutes of the day
                        val departureMinutesOfDay = hours * 60 + minutes
                        departureMinutesOfDay
                    } catch (e: Exception) {
                        Log.e("dvdv", "Error parsing time: ${schedule.waktuBerangkat}", e)
                        Int.MAX_VALUE // Sort invalid times to the end
                    }
                }
            }



            // Log the number of schedules after sorting
            Log.d("dvdv", "After Sorting: ${travelSchedules.size}")

            val lastDocumentSnapshot = currentPage.documents.lastOrNull()
            val nextKey = lastDocumentSnapshot

            LoadResult.Page(
                data = travelSchedules,
                prevKey = null, // Since it's the first page
                nextKey = nextKey
            )
        } catch (e: Exception) {
            Log.e("dvdv", "Error loading data", e)
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, TravelSchedule>): DocumentSnapshot? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestItemToPosition(anchorPosition)?.let { item ->
                state.closestPageToPosition(anchorPosition)?.nextKey ?: state.closestPageToPosition(anchorPosition)?.prevKey
            }
        }
    }
}
