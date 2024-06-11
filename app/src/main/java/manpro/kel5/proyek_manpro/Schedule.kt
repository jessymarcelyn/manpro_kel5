package manpro.kel5.proyek_manpro

import RutePagingSource
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Schedule : AppCompatActivity() {
    private lateinit var rvSchedule: RecyclerView
    private lateinit var adapter: TravelScheduleAdapter
    private var _filterOpt: Int = 1
    private var bus: Boolean = true
    private var train: Boolean = true
    private lateinit var sharedPreferences: SharedPreferences
    private var selectedDate: String = ""
    private var userInput: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)
        BottomNav.setupBottomNavigationView(this)

        val _searchText = findViewById<EditText>(R.id.searchTujuan)
        val _btnSearch1 = findViewById<ImageButton>(R.id.btnSearch1)

        Log.d("kk", "_searchText "+ _searchText.text.toString())
//        _btnSearch1.setOnClickListener{
//            userInput = _searchText.text.toString()
//            Log.d("kk", "_searchText: $userInput")
//            getTravelSchedules()
//        }

        _searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                userInput = s.toString()
                Log.d("ukuk", "User input: $userInput")
                getTravelSchedules()
                reloadSchedules()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        val spinner: Spinner = findViewById(R.id.dateDropdownSchedule)
        val dates = getNextSevenDays()
        val adapterSpin = ArrayAdapter(this, R.layout.dropdown_item_sch, dates)
        adapterSpin.setDropDownViewResource(R.layout.dropdown_item_sch)
        spinner.adapter = adapterSpin

        val initialPosition = 0
        if (initialPosition != -1) {
            spinner.setSelection(initialPosition)
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedDate = parent?.getItemAtPosition(position).toString()
                getTravelSchedules()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        rvSchedule = findViewById(R.id.rv_schedule)
        rvSchedule.layoutManager = LinearLayoutManager(this)
        rvSchedule.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        val btnFilter = findViewById<ImageButton>(R.id.btnFilter1)
        btnFilter.setOnClickListener {
            showFilterDialog()
        }

        adapter = TravelScheduleAdapter()
        rvSchedule.adapter = adapter
        getTravelSchedules()
    }

    private fun getNextSevenDays(): List<String> {
        val dates = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

        for (i in 0 until 7) {
            dates.add(dateFormat.format(calendar.time))
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return dates
    }

    private fun reloadSchedules() {
        adapter.refresh()
    }

    private fun showFilterDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.filter_menu_schedule)
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

        sharedPreferences = getSharedPreferences("FilterPrefSchedule", Context.MODE_PRIVATE)
        val savedFilterOpt = sharedPreferences.getInt("filterOptSchedule", -1)
        val savedBus = sharedPreferences.getBoolean("busSchedule", true)
        val savedTrain = sharedPreferences.getBoolean("trainSchedule", true)

        _idSwitch.isChecked = savedBus
        _idSwitch2.isChecked = savedTrain

        when (savedFilterOpt) {
            1 -> _btnRadio1.isChecked = true
            2 -> _btnRadio2.isChecked = true
            3 -> _btnRadio3.isChecked = true
        }

        var selectedRadioButtonId = -1

        _radioGroupFilter.setOnCheckedChangeListener { group, checkedId ->
            selectedRadioButtonId = checkedId
        }

        _btnSort.setOnClickListener {
            bus = _idSwitch.isChecked
            train = _idSwitch2.isChecked

            if (selectedRadioButtonId != -1) {
                val selectedRadioButton = dialog.findViewById<RadioButton>(selectedRadioButtonId)
                val filterText = selectedRadioButton.text.toString()
                _filterOpt = when (selectedRadioButtonId) {
                    _btnRadio1.id -> 1
                    _btnRadio2.id -> 2
                    _btnRadio3.id -> 3
                    else -> _filterOpt
                }

                Toast.makeText(this, "Filter applied: $filterText", Toast.LENGTH_SHORT).show()
            }

            with(sharedPreferences.edit()) {
                putInt("filterOptSchedule", _filterOpt)
                putBoolean("busSchedule", bus)
                putBoolean("trainSchedule", train)
                apply()
            }

            getTravelSchedules()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun getTravelSchedules() {
        val db = FirebaseFirestore.getInstance()
        val pagingSource = { RutePagingSource(db, bus, train, _filterOpt, selectedDate, userInput) }
        val pager = Pager(
            config = PagingConfig(pageSize = 100, enablePlaceholders = false),
            pagingSourceFactory = pagingSource
        )

        val flow = pager.flow
        lifecycleScope.launch {
            flow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
        reloadSchedules()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
        val specificSharedPreferences = getSharedPreferences("FilterPrefSchedule", Context.MODE_PRIVATE)
        specificSharedPreferences.edit().clear().apply()
    }
}

