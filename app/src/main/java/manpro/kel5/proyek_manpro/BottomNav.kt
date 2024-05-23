package manpro.kel5.proyek_manpro
import android.app.Activity
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView
import manpro.kel5.proyek_manpro.profile.Profile

class BottomNav {
    companion object {
        fun setupBottomNavigationView(activity: Activity) {
            val bottomNavigationView = activity.findViewById<BottomNavigationView>(R.id.navbar)
            bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.homeFragment -> {
                        if (activity !is Home) {
                            val intent = Intent(activity, Home::class.java)
                            activity.startActivity(intent)
                            activity.finish()
                        }
                        true
                    }
                    R.id.scheduleFragment -> {
                        if (activity !is Schedule) {
                            val intent = Intent(activity, Schedule::class.java)
                            activity.startActivity(intent)
                            activity.finish()
                        }
                        true
                    }
                    R.id.profileFragment -> {
                        if (activity !is Profile) {
                            val intent = Intent(activity, Profile::class.java)
                            activity.startActivity(intent)
                            activity.finish()
                        }
                        true
                    }
                    else -> false
                }
            }
            when (activity) {
                is Home -> bottomNavigationView.selectedItemId = R.id.homeFragment
                is Schedule -> bottomNavigationView.selectedItemId = R.id.scheduleFragment
                is Profile -> bottomNavigationView.selectedItemId = R.id.profileFragment
            }
        }
    }
}