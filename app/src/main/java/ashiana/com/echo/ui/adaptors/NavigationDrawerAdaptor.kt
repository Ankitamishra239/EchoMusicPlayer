package ashiana.com.echo.ui.adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ashiana.com.echo.R
import ashiana.com.echo.ui.Fragment.AboutUsFragment
import ashiana.com.echo.ui.Fragment.FavouriteFragment
import ashiana.com.echo.ui.Fragment.MainScreenFragment
import ashiana.com.echo.ui.Fragment.SettingsFragment
import ashiana.com.echo.ui.activities.MainActivity

class NavigationDrawerAdaptor(_contentList:ArrayList<String>,_getImages: IntArray, _context:Context)
    : RecyclerView.Adapter<NavigationDrawerAdaptor.NavViewHolder>(){
    var contentList: ArrayList<String>?=null
    var getImages: IntArray?=null
    var mContext: Context?=null

    init {
        this.contentList = _contentList
        this.getImages = _getImages
        this.mContext = _context
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavViewHolder {
       var itemView = LayoutInflater.from(parent?.context)
            .inflate(R.layout.row_custom_navigationdrawer,parent,false)
       val returnThis = NavViewHolder(itemView)
       return returnThis
    }

    override fun getItemCount(): Int {

        return(contentList as ArrayList).size

    }

    override fun onBindViewHolder(holder: NavViewHolder, position: Int) {
        holder.icon_Get?.setBackgroundResource(getImages?.get(position) as Int)
        holder.text_Get?.setText(contentList?.get(position ))
        holder.contentHolder?.setOnClickListener( {
            if(position == 0){
                var mainScreenFragment = MainScreenFragment()
                (mContext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fregment,mainScreenFragment)
                    .commit()
            }else if(position == 1){
                var favouriteFragment = FavouriteFragment()
                (mContext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fregment,favouriteFragment)
                    .commit()
            }else if(position == 2) {
                var settingsFragment = SettingsFragment()
                (mContext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fregment, settingsFragment)
                    .commit()
            }else{
                var aboutusFragment = AboutUsFragment()
                (mContext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fregment, aboutusFragment)
                    .commit()
            }
            MainActivity.Statified.drawerLayout?.closeDrawers()
        })

    }

    class NavViewHolder(itemView: View?):RecyclerView.ViewHolder(itemView!!){
        var icon_Get: ImageView?=null
        var text_Get: TextView?=null
        var contentHolder: RelativeLayout?=null

        init {
            icon_Get=itemView?.findViewById(R.id.icon_navdrawer)
            text_Get=itemView?.findViewById(R.id.text_navdrawer)
            contentHolder=itemView?.findViewById(R.id.navdrawer_item_content_holder)
        }
    }
}