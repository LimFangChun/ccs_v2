package my.edu.tarc.communechat_v2

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_advanced_search.*
import my.edu.tarc.communechat_v2.model.Student

class AdvancedSearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advanced_search)

        assert(supportActionBar != null)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //note: in kotlin you don't need to initialize views like you do in Java
        //you can directly refer the view id and do all the set on click listener etc

        initFacultySpinnerItems()
        initializeListeners()
    }

    private fun initializeListeners() {
        //faculty spinner
        spinner_faculty.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                //index 0 == All
                if (position == 0) {
                    setViewEnable(false)
                } else {
                    setViewEnable(true)
                    initCourseSpinnerItems(p0!!.getItemAtPosition(position).toString())
                    initYearSpinnerItems()
                    initTutorialGroupSpinnerItems()
                }
            }
        }

        button_reset.setOnClickListener {
            resetSpinner()
        }

        button_search.setOnClickListener {
            if (spinner_faculty.selectedItemPosition == 0) {
                val builder = AlertDialog.Builder(this@AdvancedSearchActivity)
                builder.setTitle(R.string.notice)
                builder.setMessage(getString(R.string.advanced_search_empty_criteria_desc))
                builder.setNeutralButton(R.string.ok, null)
                builder.show()
            } else {
                startFindFriendActivity()
            }
        }
    }

    private fun startFindFriendActivity() {
        val intent = Intent(this, FindFriendResult::class.java)

        val faculty: String = if (spinner_faculty.selectedItemPosition == 0) "" else {
            spinner_faculty.selectedItem.toString()
        }

        val course: String = if (spinner_course.selectedItemPosition == 0) "" else {
            spinner_course.selectedItem.toString()
        }

        val year: String = if (spinner_year.selectedItemPosition == 0) "-1" else {
            spinner_year.selectedItem.toString()
        }

        val tutorialGroup: String = if (spinner_tutorialGroup.selectedItemPosition == 0) "-1" else {
            spinner_tutorialGroup.selectedItem.toString()
        }

        intent.putExtra("Type", 6)
        intent.putExtra(Student.COL_FACULTY, faculty)
        intent.putExtra(Student.COL_COURSE, course)
        intent.putExtra(Student.COL_ACADEMIC_YEAR, year)
        intent.putExtra(Student.COL_TUTORIAL_GROUP, tutorialGroup)
        startActivity(intent)
    }

    private fun initFacultySpinnerItems() {
        spinner_faculty.isEnabled = true
        val faculty = arrayOf("-Tap here-", "FOCS")
        val facultyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, faculty)
        spinner_faculty.adapter = facultyAdapter
        spinner_faculty.setSelection(0)
    }

    private fun initCourseSpinnerItems(faculty: String) {
        spinner_course.isEnabled = true
        var course: Array<String> = arrayOf()

        when (faculty) {
            "FOCS" -> course = arrayOf("All", "RSD", "REI", "RSF", "RIT", "RMM")
        }

        val courseAdapter = ArrayAdapter(baseContext, android.R.layout.simple_spinner_dropdown_item, course)
        spinner_course.adapter = courseAdapter
        spinner_course.setSelection(0)
    }

    private fun initYearSpinnerItems() {
        spinner_year.isEnabled = true
        val year = arrayOf("All", "1", "2", "3")
        val yearAdapter = ArrayAdapter(baseContext, android.R.layout.simple_spinner_dropdown_item, year)
        spinner_year.adapter = yearAdapter
        spinner_year.setSelection(0)
    }

    private fun initTutorialGroupSpinnerItems() {
        spinner_tutorialGroup.isEnabled = true
        val tutorialGroup = arrayOf("All", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        val tutorialGroupAdapter = ArrayAdapter(baseContext, android.R.layout.simple_spinner_dropdown_item, tutorialGroup)
        spinner_tutorialGroup.adapter = tutorialGroupAdapter
        spinner_tutorialGroup.setSelection(0)
    }

    private fun resetSpinner() {
        spinner_faculty.setSelection(0)
        spinner_course.setSelection(0)
        spinner_year.setSelection(0)
        spinner_tutorialGroup.setSelection(0)
    }

    private fun setViewEnable(enable: Boolean) {
        spinner_tutorialGroup.isEnabled = enable
        spinner_course.isEnabled = enable
        spinner_year.isEnabled = enable
    }
}
