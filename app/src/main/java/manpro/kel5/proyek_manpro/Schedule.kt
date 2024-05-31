package manpro.kel5.proyek_manpro

import RutePagingSource
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.Toast
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
    private  var _filterOpt: Int = 1
    private var bus : Boolean = false
    private var train : Boolean = false

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

        val btnFilter = findViewById<ImageButton>(R.id.btnFilter1)

        btnFilter.setOnClickListener{
            showFilterDialog()
        }

        adapter = TravelScheduleAdapter()
        rvSchedule.adapter = adapter
        getTravelSchedules()
    }

    private fun showFilterDialog() {
        // Create a new dialog
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.filter_menu)

        // Set the dialog properties
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)

        val _btnSort = dialog.findViewById<Button>(R.id.btnSort)
        val _btnRadio1 = dialog.findViewById<RadioButton>(R.id.btnRadio1)
        val _btnRadio2 = dialog.findViewById<RadioButton>(R.id.btnRadio2)
        val _btnRadio3 = dialog.findViewById<RadioButton>(R.id.btnRadio3)
        val _radioGroupFilter = dialog.findViewById<RadioGroup>(R.id.radioGroupFilter)

        val _idSwitch = dialog.findViewById<Switch>(R.id.idSwitch)
        val _idSwitch2 = dialog.findViewById<Switch>(R.id.idSwitch2)

        var selectedRadioButtonId = -1

        // Set a checked change listener for the radio group
        _radioGroupFilter.setOnCheckedChangeListener { group, checkedId ->
            selectedRadioButtonId = checkedId
        }

        _btnSort.setOnClickListener {
            bus = _idSwitch.isChecked
            train = _idSwitch2.isChecked

            //blm bisa mempertahanin kalau diclick

            Log.d("nbnb", "bus : " + bus)
            Log.d("nbnb", "train : " + train)
            if (selectedRadioButtonId != -1) {
                val selectedRadioButton = dialog.findViewById<RadioButton>(selectedRadioButtonId)
                val filterText = selectedRadioButton.text.toString()
                if(selectedRadioButtonId == _btnRadio1.id){
                    _filterOpt = 1
                    Toast.makeText(this, "Filter applied1: $filterText", Toast.LENGTH_SHORT).show()
                }
                else if(selectedRadioButtonId == _btnRadio2.id){
                    _filterOpt = 2
                    Toast.makeText(this, "Filter applied2: $filterText", Toast.LENGTH_SHORT).show()
                }
                else if(selectedRadioButtonId == _btnRadio3.id){
                    _filterOpt = 3
                    Toast.makeText(this, "Filter applied3: $filterText", Toast.LENGTH_SHORT).show()
                }
            }
            getTravelSchedules()
            dialog.dismiss()
        }


        // Show the dialog
        dialog.show()
    }

    private fun getTravelSchedules() {
        val db = FirebaseFirestore.getInstance()
        val pagingSource = { RutePagingSource(db, bus, train, _filterOpt) }
        val pager = Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
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
