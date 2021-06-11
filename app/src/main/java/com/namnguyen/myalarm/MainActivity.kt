package com.namnguyen.myalarm

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.namnguyen.myalarm.databinding.ActivityMainBinding
import com.namnguyen.myalarm.service.AlarmService
import com.namnguyen.myalarm.util.DateTimeUtils.formatDate
import com.namnguyen.myalarm.util.setSafeOnClickListener
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var alarmService: AlarmService

    private val repetitiveLive = MutableLiveData(false)
    private val timeMillisLive = MutableLiveData<Long>(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        alarmService = AlarmService(this)

        initView()
        observer()
    }

    private fun initView() = with(mBinding) {
        btnReset.setSafeOnClickListener {
            timeMillisLive.postValue(0)
            alarmService.resetAlarm()
            Toast.makeText(this@MainActivity, "RESET DONE", Toast.LENGTH_SHORT).show()
        }

        btnSetTime.setSafeOnClickListener {
            if (timeMillisLive.value ?: 0 < 1) return@setSafeOnClickListener
            if (repetitiveLive.value == true) {
                alarmService.setRepetitiveAlarm(timeMillisLive.value ?: 0L)
            } else {
                alarmService.setExactAlarm(timeMillisLive.value ?: 0L)
            }
            Toast.makeText(this@MainActivity, "SET ALARM DONE", Toast.LENGTH_SHORT).show()
            btnSetTime.isEnabled = false
        }

        cbRepetitive.setOnCheckedChangeListener { _, isChecked ->
            repetitiveLive.postValue(isChecked)
        }

        tpAlarm.setSafeOnClickListener {
            setAlarm { timeMillisLive.postValue(it) }
        }
    }

    private fun observer() {
        timeMillisLive.observe(this@MainActivity) { millis ->
            if (millis < 1) {
                cbRepetitive.isChecked = false
                tvTime.text = ""
            } else {
                cbRepetitive.isEnabled = true
                tvTime.text = millis.formatDate()
            }
            cbRepetitive.isEnabled = millis > 0
            btnSetTime.isEnabled = millis > 0
            btnReset.isEnabled = millis > 0
        }

        repetitiveLive.observe(this@MainActivity) {
            Log.d(TAG, "isRepetitive: $it")
        }
    }

    private fun setAlarm(callback: (Long) -> Unit) {
        Calendar.getInstance().apply {
            this.set(Calendar.SECOND, 0)
            this.set(Calendar.MILLISECOND, 0)
            DatePickerDialog(
                this@MainActivity,
                0,
                { _, year, month, day ->
                    this.set(Calendar.YEAR, year)
                    this.set(Calendar.MONTH, month)
                    this.set(Calendar.DAY_OF_MONTH, day)
                    TimePickerDialog(
                        this@MainActivity,
                        0,
                        { _, hour, minute ->
                            this.set(Calendar.HOUR_OF_DAY, hour)
                            this.set(Calendar.MINUTE, minute)
                            callback(this.timeInMillis)
                        },
                        this.get(Calendar.HOUR_OF_DAY),
                        this.get(Calendar.MINUTE),
                        false
                    ).show()
                },
                this.get(Calendar.YEAR),
                this.get(Calendar.MONTH),
                this.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.name
    }
}