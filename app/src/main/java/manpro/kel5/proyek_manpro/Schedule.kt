package manpro.kel5.proyek_manpro

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class Schedule : AppCompatActivity() {
    private lateinit var rvSchedule: RecyclerView
    private lateinit var adapter: TravelScheduleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)
        BottomNav.setupBottomNavigationView(this)

        rvSchedule = findViewById(R.id.rv_schedule)
        rvSchedule.layoutManager = LinearLayoutManager(this)
        rvSchedule.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )

        adapter = TravelScheduleAdapter()
        rvSchedule.adapter = adapter
        getTravelSchedules()
    }

    private fun getTravelSchedules() {
        val db = FirebaseFirestore.getInstance()
        val pagingSource = { RutePagingSource(db) }
        val pager = Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                prefetchDistance = 5,
                initialLoadSize = 20
            ),
            pagingSourceFactory = pagingSource
        )

        val flow = pager.flow
        lifecycleScope.launch {
            flow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
