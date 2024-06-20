package manpro.kel5.proyek_manpro

data class BookmarkData(
    val docId: String = "",
    val idStopSource: String = "",
    val idStopDest: String = "",
    val idUser: String = "",
    val idRute: List<String> = emptyList(),
    val bookmarkName: String?
)
