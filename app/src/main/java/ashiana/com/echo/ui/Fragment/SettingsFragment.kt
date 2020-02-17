package ashiana.com.echo.ui.Fragment


import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import ashiana.com.echo.R

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : Fragment() {

    var myActivity: Activity? = null
    var shakeSwitch: Switch?= null

    object Statified{
        var MY_PRESS_NAME = "ShakeFeature"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater!!.inflate(R.layout.fragment_settings, container, false)
        activity?.title = "Settings"
        shakeSwitch = view?.findViewById(R.id.switchShake)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
       myActivity = activity
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val prefs = myActivity?.getSharedPreferences(Statified.MY_PRESS_NAME, Context.MODE_PRIVATE)
        var isAllowed = prefs?.getBoolean("feature", false)

        if(isAllowed as Boolean){
            shakeSwitch?.isChecked = true
        } else {
            shakeSwitch?.isChecked = false
        }

        shakeSwitch?.setOnCheckedChangeListener({
            compoundButton, b->
            if(b){
                val editor = myActivity?.getSharedPreferences(Statified.MY_PRESS_NAME, Context.MODE_PRIVATE)?.edit()
                editor?.putBoolean("feature", true)
                editor?.apply()
            } else {
                val editor = myActivity?.getSharedPreferences(Statified.MY_PRESS_NAME, Context.MODE_PRIVATE)?.edit()
                editor?.apply()
            }
        })
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val items = menu?.findItem(R.id.action_sort)
        items?.isVisible = false
    }
}
