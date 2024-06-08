import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import manpro.kel5.proyek_manpro.TravelSchedule
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RutePagingSource(
    private val db: FirebaseFirestore,
    private val bus: Boolean,
    private val train: Boolean,
    private val filterOpt: Int,
    private val tanggalDate : String
) : PagingSource<DocumentSnapshot, TravelSchedule>() {

    private lateinit var travelSchedule: TravelSchedule
    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, TravelSchedule> {
        return try {
            Log.d("mfmf", "tanggalDate : $tanggalDate")
            val currentPage = params.key?.let {
                db.collection("RuteBaru")
                    .startAfter(it)
                    .limit(params.loadSize.toLong())
                    .get()
                    .await()
            } ?: db.collection("RuteBaru")
                .limit(params.loadSize.toLong())
                .get()
                .await()

            val travelSchedules = mutableListOf<TravelSchedule>()
            for (document in currentPage.documents) {
                val idStopSource = document.getString("id_stop_source") ?: ""
                val idStopDest = document.getString("id_stop_dest") ?: ""
                val jamBerangkatStr = document.getLong("jam_berangkat")?.toInt() ?: 0
                val jamSampai = document.getLong("jam_sampai")?.toInt() ?: 0
                val estimasi = document.getLong("estimasi") ?: 0
                val price = document.getLong("price") ?: 0
                val idTransportasi = document.getString("id_transportasi") ?: ""
                val ruteBaruStartDate = document.get("startDate")
                val ruteBaruFinishDate = document.get("finishDate")

//                val ruteBaruStartDateInt = if (ruteBaruStartDate is Number) {
//                    ruteBaruStartDate.toInt()
//                } else {
//                    0
//                }
//
//                val ruteBaruFinishDateInt = if (ruteBaruFinishDate is Number) {
//                    ruteBaruFinishDate.toInt()
//                } else {
//                    0
//                }
//                val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
//                val tanggalDateParsed = dateFormat.parse(tanggalDate)
//
//                val ruteBaruStartDateParsed = formatDate(ruteBaruStartDateInt)
//                val ruteBaruFinishDateParsed = if (ruteBaruFinishDateInt != 0) {
//                    formatDate(ruteBaruFinishDateInt)
//                } else {
//                    ""
//                }
//
//                val ruteBaruStartDateDate = dateFormat.parse(ruteBaruStartDateParsed)
//                val ruteBaruFinishDateDate = if (ruteBaruFinishDateParsed != "") {
//                    dateFormat.parse(ruteBaruFinishDateParsed)
//                } else {
//                    null
//                }
//
//                Log.d("mimi", "docId : " + document.id)
//                Log.d("mimi", "ruteBaruStartDateDate : $ruteBaruStartDateDate")
//                Log.d("mimi", "ruteBaruFinishDateDate : $ruteBaruFinishDateDate")
//                Log.d("mimi", "tanggalDateParsed : $tanggalDateParsed")

                travelSchedule = TravelSchedule(
                    document.id,
                    idStopSource,
                    idStopDest,
                    jamBerangkatStr,
                    jamSampai,
                    estimasi.toInt(),
                    price.toInt(),
                    idTransportasi, tanggalDate
                )
//
//                if (ruteBaruFinishDateDate == null) {
//                    if (tanggalDateParsed.after(ruteBaruStartDateDate)) {
//                        Log.d("mimi", "masuk1")
//                        //isi
//                        if ((bus && train) || (!bus && !train)) {
//                            travelSchedules.add(travelSchedule)
//                        } else if (bus && travelSchedule.idTranspor.startsWith("B")) {
//                            travelSchedules.add(travelSchedule)
//                        } else if (train && travelSchedule.idTranspor.startsWith("T")) {
//                            travelSchedules.add(travelSchedule)
//                        }
//                    }
//                } else {
//                    if (tanggalDateParsed.after(ruteBaruStartDateDate) &&
//                        tanggalDateParsed.before(ruteBaruFinishDateDate)
//                    ) {
//                        Log.d("mimi", "masuk2")
//                        //isi
//                        if ((bus && train) || (!bus && !train)) {
//                            Log.d("mimi", "masuk3")
//                            travelSchedules.add(travelSchedule)
//                        } else if (bus && travelSchedule.idTranspor.startsWith("B")) {
//                            Log.d("mimi", "masuk4")
//                            travelSchedules.add(travelSchedule)
//                        } else if (train && travelSchedule.idTranspor.startsWith("T")) {
//                            Log.d("mimi", "masuk5")
//                            travelSchedules.add(travelSchedule)
//                        }
//                    }
//                }

                if ((bus && train) || (!bus && !train)) {
                    Log.d("mimi", "masuk3")
                    travelSchedules.add(travelSchedule)
                } else if (bus && travelSchedule.idTranspor.startsWith("B")) {
                    Log.d("mimi", "masuk4")
                    travelSchedules.add(travelSchedule)
                } else if (train && travelSchedule.idTranspor.startsWith("T")) {
                    Log.d("mimi", "masuk5")
                    travelSchedules.add(travelSchedule)
                }

                Log.d("min", "docId : " + document.id)
                Log.d("min", "jamBerangkatStr : " + jamBerangkatStr)
                Log.d("min", "jamSampai : " + jamSampai)
                Log.d("mdmd", "TravelSchedules: $travelSchedules")
            }

           // Ensure this log is before any sorting or filtering
            Log.d("dvdv", "Filter Option: $filterOpt, Before Sorting: ${travelSchedules.size}")

            // Apply filtering and sorting as needed...

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


    fun padDateString(dateString: String): String {
        return if (dateString.length == 7) {
            "0$dateString"
        } else {
            dateString
        }
    }

    fun parseDate(dateString: String): Date? {
        val paddedDateString = padDateString(dateString)
        val dateFormat = SimpleDateFormat("ddMMyyyy", Locale("id", "ID"))
        return try {
            dateFormat.parse(paddedDateString)
        } catch (e: Exception) {
            null
        }
    }

    fun formatDate(dateInt: Int): String {
        val dateString = dateInt.toString()
        val date = parseDate(dateString)
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        return date?.let { outputFormat.format(it) } ?: "Invalid date"
    }

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, TravelSchedule>): DocumentSnapshot? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestItemToPosition(anchorPosition)?.let { item ->
                state.closestPageToPosition(anchorPosition)?.nextKey ?: state.closestPageToPosition(anchorPosition)?.prevKey
            }
        }
    }
}
