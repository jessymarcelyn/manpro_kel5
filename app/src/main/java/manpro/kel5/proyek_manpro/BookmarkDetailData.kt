package manpro.kel5.proyek_manpro

data class BookmarkDetailData(
    val docId: String = "",
    val idStopSource: String = "",
    val idStopDest: String = "",
    val idUser: String = "",
    val idRute: List<String> = emptyList(),
    var bookmarkName: String = ""
)
