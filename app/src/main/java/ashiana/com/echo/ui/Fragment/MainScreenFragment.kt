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
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ashiana.com.echo.R
import ashiana.com.echo.ui.CurrentSongHelper
import ashiana.com.echo.ui.Songs
import ashiana.com.echo.ui.adaptors.MainScreenAdaptor
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class MainScreenFragment : Fragment() {

    var getsongsList: ArrayList<Songs>? = null
    var nowPlayingBottomBar: RelativeLayout? = null
    var playPauseButton: ImageButton? = null
    var songTitle: TextView? = null
    var trackPosition: Int = 0
    var visibleLayout: RelativeLayout? = null
    var noSongs: RelativeLayout? = null
    var recyclerView: RecyclerView? = null
    var myActivity: Activity? = null
    var mMediaPlayer: MediaPlayer? = null
    var playPauseHelper: CurrentSongHelper? = null
    var _mainScreenAdapter: MainScreenAdaptor? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_main_screen, container, false)
        setHasOptionsMenu(true)
        activity?.title = "All Songs"
        visibleLayout = view?.findViewById<RelativeLayout>(R.id.visibleLayout)
        noSongs = view?.findViewById<RelativeLayout>(R.id.noSongs)
        nowPlayingBottomBar = view?.findViewById<RelativeLayout>(R.id.hiddenBarMainScreen)
        songTitle = view?.findViewById<TextView>(R.id.songTitleMainScreen)
        playPauseButton = view?.findViewById<ImageButton>(R.id.playpauseButton)
        recyclerView = view?.findViewById<RecyclerView>(R.id.contentMain)


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getsongsList = getSongsFromPhone()

        val prefs = activity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)
        val action_sort_ascending = prefs?.getString("action_sort_ascending", "true")
        val action_sort_recent = prefs?.getString("action_sort_recent", "false")

        if (getsongsList == null) {
            visibleLayout?.visibility = View.INVISIBLE
            noSongs?.visibility = View.VISIBLE
        } else {
            _mainScreenAdapter =
                MainScreenAdaptor(getsongsList as ArrayList<Songs>, myActivity as Context)
            val mLayoutManager = LinearLayoutManager(myActivity)
            recyclerView?.layoutManager = mLayoutManager
            recyclerView?.itemAnimator = DefaultItemAnimator()
            recyclerView?.adapter = _mainScreenAdapter

            if (getsongsList != null) {
                if (action_sort_ascending!!.equals("true", true)) {
                    Collections.sort(getsongsList, Songs.Statified.nameComparator)
                    _mainScreenAdapter?.notifyDataSetChanged()
                } else if (action_sort_recent!!.equals("true", true)) {
                    Collections.sort(getsongsList, Songs.Statified.dateComparator)
                    _mainScreenAdapter?.notifyDataSetChanged()
                }
            }
        }
        bottomBar_setup()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
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
            if (getsongsList != null) {
                Collections.sort(getsongsList, Songs.Statified.nameComparator)
            }
            _mainScreenAdapter?.notifyDataSetChanged()
            return false
        } else if (switcher == R.id.action_sort_recent) {

            val editor =
                myActivity?.getSharedPreferences("action sort", Context.MODE_PRIVATE)?.edit()
            editor?.putString("actipn_sort_recent", "true")
            editor?.putString("actipn_sort_ascending", "false")
            editor?.apply()
            if (getsongsList != null) {
                Collections.sort(getsongsList, Songs.Statified.dateComparator)
            }
            _mainScreenAdapter?.notifyDataSetChanged()
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
            val songPathInt = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            while (songCursor.moveToNext()) {
                var currentId = songCursor.getLong(songId)
                var currentTitle = songCursor.getString(songTitle)
                var currentArtist = songCursor.getString(songArtist)
                var fullpath = songCursor.getString(songPathInt)
                var dateadded = songCursor.getLong(dateIndex)
                arrayList.add(Songs(currentId, currentTitle, currentArtist, fullpath, dateadded))
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
}
