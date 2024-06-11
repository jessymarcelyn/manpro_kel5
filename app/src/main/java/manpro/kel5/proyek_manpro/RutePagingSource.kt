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

                val ruteBaruStartDateInt = if (ruteBaruStartDate is Number) {
                    ruteBaruStartDate.toInt()
                } else {
                    0
                }

                val ruteBaruFinishDateInt = if (ruteBaruFinishDate is Number) {
                    ruteBaruFinishDate.toInt()
                } else {
                    0
                }
                val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
                val tanggalDateParsed = dateFormat.parse(tanggalDate)

                val ruteBaruStartDateParsed = formatDate(ruteBaruStartDateInt)
                val ruteBaruFinishDateParsed = if (ruteBaruFinishDateInt != 0) {
                    formatDate(ruteBaruFinishDateInt)
                } else {
                    ""
                }
                Log.d("meme", "docId : "+ document.id)
                val ruteBaruStartDateDate = dateFormat.parse(ruteBaruStartDateParsed)

                val ruteBaruFinishDateDate = if (ruteBaruFinishDateParsed != "") {
                    dateFormat.parse(ruteBaruFinishDateParsed)
                } else {
                    null
                }

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

                if (ruteBaruFinishDateDate == null) {
                    if (tanggalDateParsed.after(ruteBaruStartDateDate)) {
                        if ((bus && train) || (!bus && !train)) {
                            travelSchedules.add(travelSchedule)
                        } else if (bus && travelSchedule.idTranspor.startsWith("B")) {
                            travelSchedules.add(travelSchedule)
                        } else if (train && travelSchedule.idTranspor.startsWith("T")) {
                            travelSchedules.add(travelSchedule)
                        }
                    }
                } else {
                    if (tanggalDateParsed.after(ruteBaruStartDateDate) &&
                        tanggalDateParsed.before(ruteBaruFinishDateDate)
                    ) {
                        if ((bus && train) || (!bus && !train)) {
//                            Log.d("mimi", "masuk3")
                            travelSchedules.add(travelSchedule)
                        } else if (bus && travelSchedule.idTranspor.startsWith("B")) {
//                            Log.d("mimi", "masuk4")
                            travelSchedules.add(travelSchedule)
                        } else if (train && travelSchedule.idTranspor.startsWith("K")) {
//                            Log.d("mimi", "masuk5")
                            travelSchedules.add(travelSchedule)
                        }
                    }
                }

            }

            // Durasi tercepat
            if (filterOpt == 1) {
                val timeFormat = SimpleDateFormat("HHmm", Locale.getDefault())
                travelSchedules.sortBy { schedule ->
                    try {
                        val jamBerangkat = schedule.waktuBerangkat
                        val jamSampai = schedule.waktuSampai

                        val departureDate = timeFormat.parse(jamBerangkat.toString())
                        val departureCalendar = Calendar.getInstance().apply { time = departureDate }
                        val departureHours = departureCalendar.get(Calendar.HOUR_OF_DAY)
                        val departureMinutes = departureCalendar.get(Calendar.MINUTE)
                        val departureMinutesOfDay = departureHours * 60 + departureMinutes

                        val arrivalDate = timeFormat.parse(jamSampai.toString())
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
                        val jamBerangkat = schedule.waktuBerangkat.toString()
                        val hours = jamBerangkat.substring(0, jamBerangkat.length - 2).toInt()
                        val minutes = jamBerangkat.substring(jamBerangkat.length - 2).toInt()

                        Log.d("mbmb", "jamBerangkat : $jamBerangkat")
                        Log.d("mbmb", "hours : $hours")
                        Log.d("mbmb", "minutes : $minutes")

                        // Convert hours and minutes into minutes of the day
                        val departureMinutesOfDay = hours * 60 + minutes
                        departureMinutesOfDay
                    } catch (e: Exception) {
                        Log.e("dvdv", "Error parsing time: ${schedule.waktuBerangkat}", e)
                        Int.MAX_VALUE // Sort invalid times to the end
                    }
                }
            }


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
