package com.intelliviz.quakereport.ui

import EarthquakeRecentOptionsDialog
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.intelliviz.quakereport.QueryPreferences
import com.intelliviz.quakereport.QueryPreferences.MODE_RANGE
import com.intelliviz.quakereport.R
import kotlinx.android.synthetic.main.activity_navigation.*

class NavigationActivity : AppCompatActivity(),
        EarthquakeOptionsDialog.OnOptionsSelectedListener,
        EarthquakeRecentOptionsDialog.OnOptionsSelectedListener {

    var fragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        val toolbar = findViewById<Toolbar>(R.id.app_toolbar)
        setSupportActionBar(toolbar)

        initBottomNavigation()

        val fm = supportFragmentManager
        val fragment = fm.findFragmentById(R.id.content_frame)
        if (fragment == null) {
            val selectedItem = bottom_navigation.menu.getItem(0)
            selectNavFragment(selectedItem)
        }
    }

    private fun initBottomNavigation() {
        bottom_navigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            selectNavFragment(item)
            true
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.options_item -> {
                if(fragment?.tag == RANGE_FRAG_TAG) {
                    val mode = QueryPreferences.getMode(this)
                    val startDate = QueryPreferences.getStartDate(this)
                    val endDate = QueryPreferences.getEndDate(this)
                    val minMag = QueryPreferences.getMinMag(this)
                    val maxMag = QueryPreferences.getMaxMag(this)
                    val numDays = QueryPreferences.getNumDays(this)
                    val dialog = EarthquakeOptionsDialog.newInstance(mode, startDate, endDate, minMag, maxMag, numDays)
                    dialog.show(supportFragmentManager, "options")
                } else if(fragment?.tag == RECENT_FRAG_TAG) {
                    val numDays = QueryPreferences.getNumDays(this)
                    val minMag = QueryPreferences.getMinMag(this)
                    val maxMag = QueryPreferences.getMaxMag(this)
                    val dialog = EarthquakeRecentOptionsDialog.newInstance(0, numDays, minMag, maxMag)
                    dialog.show(supportFragmentManager, "options")
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onOptionsSelected(mode: Int, startDate: String, endDate: String, minMag: Int, maxMag: Int, numDays: Int) {
        QueryPreferences.setStartDate(this, startDate)
        QueryPreferences.setEndDate(this, endDate)
        QueryPreferences.setMinMag(this, minMag)
        QueryPreferences.setMaxMag(this, maxMag)
        val tempFragment = fragment
        if(tempFragment is EarthquakeRangeFragment) {
            fragment?.let {
                if(mode == MODE_RANGE) {
                    tempFragment.loadEarthquakes(startDate, endDate, minMag, maxMag)
                } else {
                    tempFragment.loadEarthquakes(minMag, maxMag, numDays)
                }
            }
        }
    }

    override fun onOptionsSelected(numDays: Int, minMag: Int, maxMag: Int) {
        QueryPreferences.setNumDays(this, numDays)
        QueryPreferences.setMinMag(this, minMag)
        QueryPreferences.setMaxMag(this, maxMag)
        val tempFragment = fragment
        if(tempFragment is EarthquakeRecentFragment) {
            tempFragment?.let {
                tempFragment.loadEarthquakes(numDays, minMag, maxMag)
            }
        }
    }

    private fun selectNavFragment(item: MenuItem) {

        val fragmentTag: String
        when (item.itemId) {
            R.id.range_menu -> {
                fragment = EarthquakeRangeFragment.newInstance()
                fragmentTag = RANGE_FRAG_TAG
            }
            R.id.recent_menu -> {
                fragment = EarthquakeRecentFragment.newInstance()
                fragmentTag = RECENT_FRAG_TAG
            }
            R.id.trend_menu -> {
                fragment = EarthquakeTrendFragment.newInstance()
                fragmentTag = TREND_FRAG_TAG
            }
            else -> return
        }

        if(fragment == null) {
            return
        }

        val fm = supportFragmentManager
        val frag = fm.findFragmentById(R.id.content_frame)
        var oldTag: String? = ""
        if (frag != null) {
            oldTag = frag.tag
        }
        if (oldTag == null || oldTag == fragmentTag) {
            return
        }
        val ft: FragmentTransaction
        ft = fm.beginTransaction()
        handleAnimation(ft, oldTag, fragmentTag)
        ft.replace(R.id.content_frame, fragment!!, fragmentTag)
        ft.commit()
    }

    private fun handleAnimation(ft: FragmentTransaction, oldTag: String, newTag: String) {

        if (oldTag.isEmpty() || oldTag == RANGE_FRAG_TAG) {
            ft.setCustomAnimations(R.anim.slide_left_in, 0)
        } else if (oldTag == TREND_FRAG_TAG) {
            ft.setCustomAnimations(R.anim.slide_right_in, 0)
        } else if (oldTag == RECENT_FRAG_TAG) {
            if (newTag == RANGE_FRAG_TAG) {
                ft.setCustomAnimations(R.anim.slide_left_in, 0)
            } else {
                ft.setCustomAnimations(R.anim.slide_right_in, 0)
            }
        }
    }

    companion object {
        val RANGE_FRAG_TAG: String = "range frag tag"
        val RECENT_FRAG_TAG: String = "recent frag tag"
        val TREND_FRAG_TAG: String = "trend frag tag"
    }
}
