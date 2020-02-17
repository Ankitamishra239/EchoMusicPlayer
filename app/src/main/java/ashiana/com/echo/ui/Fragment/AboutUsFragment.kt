package ashiana.com.echo.ui.Fragment


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import ashiana.com.echo.R

/**
 * A simple [Fragment] subclass.
 */
class AboutUsFragment : Fragment() {


    var devInfo: TextView? = null
    var devLayout: RelativeLayout? = null
    var devImage: ImageView? = null
    var androidInfo: TextView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_about_us, container, false)
        activity?.title = "About Us"
        devLayout = view?.findViewById(R.id.developerDetailScreen)
        devImage = view.findViewById(R.id.developerImage)
        devInfo = view.findViewById(R.id.developerInfo)
        androidInfo = view.findViewById(R.id.androidInfo)
        return view

    }



}
