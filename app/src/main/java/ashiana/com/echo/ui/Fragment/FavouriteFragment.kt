package ashiana.com.echo.ui.Fragment


import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ashiana.com.echo.R
import ashiana.com.echo.ui.CurrentSongHelper
import ashiana.com.echo.ui.adaptors.FavouriteAdaptor
import ashiana.com.echo.ui.Songs
import ashiana.com.echo.ui.databases.EchoDatabases
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class FavouriteFragment : Fragment() {

    var myActivity: Activity? = null
    var noFavourites: TextView? = null
    var nowPlayingBottomBar: RelativeLayout? = null
    var playPauseButton: ImageButton? = null
    var songTitle: TextView? = null
    var recyclerView: RecyclerView? = null
    var trackPosition: Int = 0
    var mMediaPlayer: MediaPlayer? = null
    var playPauseHelper: CurrentSongHelper? = null
    var favouriteContent: EchoDatabases? = null
    var refreshList: ArrayList<Songs>? = null
    var getListFromDatabases: ArrayList<Songs>? = null
    var _favAdapter: FavouriteAdaptor? = null

    object Statified {
        var mediaPlayer: MediaPlayer? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)
        activity?.title = "Favourites"
        noFavourites = view?.findViewById(R.id.noFavourites)
        nowPlayingBottomBar = view.findViewById(R.id.hiddenBarFavScreen)
        songTitle = view.findViewById(R.id.songTitleFavScreen)
        playPauseButton = view.findViewById(R.id.playpauseButton)
        recyclerView = view.findViewById(R.id.favouriteRecycler)
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {

        super.onAttach(activity)
        myActivity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        favouriteContent = EchoDatabases(myActivity)
        display_favorites_by_searching()
        bottomBar_setup()


    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.main, menu)
        return
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val switcher = item?.itemId
        if (switcher == R.id.action_sort_ascending) {

            val editor =
                myActivity?.getSharedPreferences("action sort", Context.MODE_PRIVATE)?.edit()
            editor?.putString("actipn_sort_ascending", "true")
            editor?.putString("actipn_sort_recent", "false")
            editor?.apply()
            if (favouriteContent != null) {
                Collections.sort(refreshList, Songs.Statified.nameComparator)
            }
            _favAdapter?.notifyDataSetChanged()
            return false
        } else if (switcher == R.id.action_sort_recent) {

            val editor =
                myActivity?.getSharedPreferences("action sort", Context.MODE_PRIVATE)?.edit()
            editor?.putString("actipn_sort_recent", "true")
            editor?.putString("actipn_sort_ascending", "false")
            editor?.apply()
            if (favouriteContent != null) {
                Collections.sort(refreshList, Songs.Statified.dateComparator)
            }
            _favAdapter?.notifyDataSetChanged()
            return false
        }
        return super.onOptionsItemSelected(item)

    }

    fun getSongsFromPhone(): ArrayList<Songs> {
        var arrayList = ArrayList<Songs>()
        var contentResolver = myActivity?.contentResolver

        var songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songCursor = contentResolver?.query(songUri, null, null, null, null)

        if (songCursor != null && songCursor.moveToFirst()) {
            val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)


            while (songCursor?.moveToNext() as Boolean) {
                var currentId = songCursor.getLong(songId)
                var currentTitle = songCursor.getString(songTitle)
                var currentArtist = songCursor.getString(songArtist)
                var currentData = songCursor.getString(songData)
                var currentDate = songCursor.getLong(dateIndex)

                arrayList.add(
                    Songs(
                        currentId,
                        currentTitle,
                        currentArtist,
                        currentData,
                        currentDate
                    )
                )
            }
        }
        return arrayList
    }

    fun bottomBar_setup() {
        nowPlayingBottomBar?.isClickable = false
        bottomBarClickHandlers()
        try {
            songTitle?.text = SongPlayingFragment.Statified.currentSongHelper?.songTitle
            SongPlayingFragment.Statified.mediaPlayer?.setOnCompletionListener({
                SongPlayingFragment.Staticated.onSongComplete()
                songTitle?.text = SongPlayingFragment.Statified.currentSongHelper?.songTitle
            })
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {

                playPauseHelper?.isPlaying = true
                nowPlayingBottomBar?.visibility = View.VISIBLE
                nowPlayingBottomBar?.layoutParams?.height = RecyclerView.LayoutParams.WRAP_CONTENT
                nowPlayingBottomBar?.setPadding(0, 11, 0, 11)
                nowPlayingBottomBar?.requestLayout()
            } else {
                playPauseHelper?.isPlaying = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    fun bottomBarClickHandlers() {

        nowPlayingBottomBar?.setOnClickListener({


            try {
                mMediaPlayer = SongPlayingFragment.Statified.mediaPlayer
                val songPlayingFragment = SongPlayingFragment()
                val _fetch_from_Songs_Fragment = SongPlayingFragment.Statified.fetchSongs
                val args = Bundle()

                args.putString("BottomBar", "true")
                args.putString(
                    "songTitle",
                    SongPlayingFragment.Statified.currentSongHelper?.songTitle
                )
                args.putString(
                    "songArtist",
                    SongPlayingFragment.Statified.currentSongHelper?.songArtist
                )
                args.putInt(
                    "SongID",
                    SongPlayingFragment.Statified.currentSongHelper?.songId?.toInt() as Int
                )
                args.putInt(
                    "SongPosition",
                    SongPlayingFragment.Statified.currentSongHelper?.currentPosition?.toInt() as Int
                )
                args.putParcelableArrayList("songsData", _fetch_from_Songs_Fragment)
                songPlayingFragment.arguments = args
                fragmentManager?.beginTransaction()
                    ?.replace(R.id.details_fregment, songPlayingFragment)
                    ?.addToBackStack("MainScreenFragment")
                    ?.commit()
            } catch (e: Exception) {
                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        })

        playPauseButton?.setOnClickListener({
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                SongPlayingFragment.Statified.mediaPlayer?.pause()
                trackPosition = SongPlayingFragment.Statified.mediaPlayer?.currentPosition as Int
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            } else {

                SongPlayingFragment.Statified.mediaPlayer?.seekTo(trackPosition)
                SongPlayingFragment.Statified.mediaPlayer?.start()
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })

    }


    fun display_favorites_by_searching() {
        if (favouriteContent?.checkSize() as Int > 0) {
            noFavourites?.visibility = View.INVISIBLE
            refreshList = ArrayList<Songs>()
            getListFromDatabases = favouriteContent?.queryDBList()
            val fetchList = getSongsFromPhone()
            if (fetchList != null) {
                for (i in 0..fetchList?.size - 1) {
                    for (j in 0..getListFromDatabases?.size as Int - 1) {
                        if ((getListFromDatabases as ArrayList<Songs>).get(j).songId === fetchList?.get(i).songId) {
                            (refreshList as ArrayList<Songs>).add((getListFromDatabases as ArrayList<Songs>)[j])
                        } else {
                        }
                    }

                }

            }
            if ((refreshList as ArrayList<Songs>).size === 0) {
                recyclerView?.visibility = View.INVISIBLE
                noFavourites?.visibility = View.VISIBLE
            }

            val _mainScreenAdapter = FavouriteAdaptor( refreshList as ArrayList<Songs>, myActivity as Context)
            val mLayoutManager = LinearLayoutManager(activity)
            recyclerView?.layoutManager = mLayoutManager
            recyclerView?.itemAnimator = DefaultItemAnimator()
            recyclerView?.adapter = _mainScreenAdapter
            recyclerView?.setHasFixedSize(true)


        } else {
            recyclerView?.visibility = View.INVISIBLE
            noFavourites?.visibility = View.VISIBLE
        }
    }

}





//Fragment screen code completed.