package com.intelliviz.quakereport.ui

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.intelliviz.quakereport.R
import kotlinx.android.synthetic.main.activity_navigation.*

class NavigationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
    }

    private fun initBottomNavigation() {
        bottom_navigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            selectNavFragment(item)
            true
        })
    }

    private fun selectNavFragment(item: MenuItem) {
        val fragment: Fragment
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
        ft.replace(R.id.content_frame, fragment, fragmentTag)
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
