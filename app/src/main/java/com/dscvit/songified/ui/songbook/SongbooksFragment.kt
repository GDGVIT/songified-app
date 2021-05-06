package com.dscvit.songified.ui.songbook

import android.app.Dialog
import android.content.DialogInterface
import android.content.SharedPreferences
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.dscvit.handly.util.*
import com.dscvit.songified.R
import com.dscvit.songified.adapter.SongListAdapter
import com.dscvit.songified.adapter.SongbookAdapter
import com.dscvit.songified.adapter.SongbookSongAdapter
import com.dscvit.songified.model.*
import com.dscvit.songified.ui.login.LoginBottomSheetFragment
import com.dscvit.songified.util.Constants
import com.dscvit.songified.util.DialogDismissListener
import com.dscvit.songified.util.PrefHelper
import com.dscvit.songified.util.PrefHelper.get
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.w3c.dom.Text

class SongbooksFragment : Fragment() {
    lateinit var songbooks: MutableList<Songbook>
    val TAG = "SongBookFragment"
    lateinit var songbookViewModel: SongbookViewModel
    lateinit var tvSongbooksCount: TextView
    lateinit var songbookAdapter: SongbookAdapter
    lateinit var songbookLoadingDialog: Dialog
    lateinit var addSongbookLoading: Dialog
    lateinit var editSongbookLoading: Dialog
    lateinit var delSongbookLoading: Dialog
    lateinit var tvNodata: TextView
    lateinit var imgNoData: ImageView
    lateinit var sharedPref: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_songbooks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rvSongbook = view.findViewById(R.id.rv_songbooks_fragment) as RecyclerView
        sharedPref = PrefHelper.customPrefs(requireContext(), Constants.PREF_NAME)
        if (sharedPref.get(Constants.PREF_IS_AUTH, false)!!) {


            songbookLoadingDialog = createProgressDialog(requireContext(), "Loading songbooks ...")
            tvSongbooksCount = view.findViewById(R.id.tv_songbooks_count) as TextView

            tvNodata = view.findViewById(R.id.tv_no_data_songbooks) as TextView
            imgNoData = view.findViewById(R.id.img_no_data_songbooks) as ImageView
            songbookAdapter = SongbookAdapter()
            rvSongbook.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = songbookAdapter
            }
            songbookViewModel = getViewModel()
            songbookLoadingDialog.show()
            getSongbooks()

            val fabNewSongbook = view.findViewById(R.id.fab_new_songbook) as FloatingActionButton
            fabNewSongbook.setOnClickListener {
                val dialog = createDialog(requireContext(), false, R.layout.dialog_new_songbook)


                val etSongbookName =
                    dialog.findViewById(R.id.et_dialog_new_songbook) as TextInputEditText
                val btnCreateSongbook =
                    dialog.findViewById(R.id.btn_dialog_create_new_songbook) as Button

                btnCreateSongbook.setOnClickListener {
                    val newSongbookRequest = NewSongbookRequest(etSongbookName.text.toString())
                    addSongbookLoading =
                        createProgressDialog(requireContext(), "Creating songbook ...")
                    addSongbookLoading.show()
                    songbookViewModel.newSongbook(newSongbookRequest).observe(viewLifecycleOwner,
                        Observer {
                            when (it) {
                                is Result.Loading -> {
                                    Log.d(TAG, "Creating new songbook")
                                }

                                is Result.Success -> {
                                    addSongbookLoading.dismiss()
                                    Log.d(TAG, "Songbook created")
                                    shortToast("Songbook Created")
                                    dialog.dismiss()
                                    getSongbooks()
                                }
                            }
                        })
                }

                val btnCancel = dialog.findViewById(R.id.btn_dialog_cancel_new_songbook) as Button
                btnCancel.setOnClickListener { dialog.dismiss() }
                dialog.show()
            }

            rvSongbook.addOnItemClickListener(object : OnItemClickListener {
                override fun onItemClicked(position: Int, view: View) {
                    val selectedSongbookId = songbooks[position].id
                    val selectedSongbookName = songbooks[position].name
                    val bundle = bundleOf(
                        "selected_songbook_id" to selectedSongbookId,
                        "selected_songbook_name" to selectedSongbookName
                    )
                    Log.d(TAG,selectedSongbookId)
                    view.findNavController()
                        .navigate(R.id.action_songbooks_to_singlesongbook, bundle)
                }

            })

            rvSongbook.addOnItemLongClickListener(object : OnItemClickListener {
                override fun onItemClicked(position: Int, view: View) {
                    val chooserDialog =
                        createDialog(requireContext(), true, R.layout.dialog_modify_chooser)
                    //chooserDialog.setContentView(R.layout.dialog_modify_chooser)
                    val tvEdit = chooserDialog.findViewById(R.id.tv_edit_dialog_chooser) as TextView
                    val tvDel =
                        chooserDialog.findViewById(R.id.tv_delete_dialog_chooser) as TextView

                    tvEdit.setOnClickListener {
                        //TODO Update songbook api
                        chooserDialog.dismiss()
                        val editSongbookDialog =
                            createDialog(requireContext(), false, R.layout.dialog_update_songbook)
                        //editSongbookDialog.setContentView(R.layout.dialog_update_songbook)
                        val etSongbookName =
                            editSongbookDialog.findViewById(R.id.et_dialog_name_edit_songbook) as EditText
                        etSongbookName.setText(songbooks[position].name)
                        val btnUpdateSongbook =
                            editSongbookDialog.findViewById(R.id.btn_update_dialog_edit_songbook) as Button
                        btnUpdateSongbook.setOnClickListener {
                            val updateSongbookNameReqeust = UpdateSongbookNameReqeust(
                                songbooks[position].id,
                                etSongbookName.text.toString()
                            )
                            editSongbookLoading =
                                createProgressDialog(requireContext(), "Updating songbook")
                            editSongbookLoading.show()
                            songbookViewModel.updateSongbookName(updateSongbookNameReqeust)
                                .observe(viewLifecycleOwner,
                                    Observer {
                                        when (it) {
                                            is Result.Loading -> {
                                                Log.d(TAG, "Updating sonbgook name")
                                            }

                                            is Result.Success -> {
                                                editSongbookLoading.dismiss()
                                                Log.d(TAG, "Songbook Name updated")
                                                editSongbookDialog.dismiss()
                                                getSongbooks()

                                            }
                                        }
                                    })
                        }
                        val btnCancelEditSongbook =
                            editSongbookDialog.findViewById(R.id.btn_cancel_dialog_edit_songbook) as Button
                        btnCancelEditSongbook.setOnClickListener {
                            editSongbookDialog.dismiss()
                        }
                        editSongbookDialog.show()
                    }

                    tvDel.setOnClickListener {
                        //TODO Delete songbook api
                        val alertDialogBuilder = AlertDialog.Builder(requireContext(),R.style.MyAlertDialog)
                        alertDialogBuilder.setTitle("Delete Songbook")
                        alertDialogBuilder.setMessage("Are you sure you want to delete ${songbooks[position].name} ?")
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

                        alertDialogBuilder.setPositiveButton("Yes") { dialog, which ->
                            val songbookDeleteRequest =
                                SongbookDeleteRequest(songbooks[position].id)
                            delSongbookLoading =
                                createProgressDialog(requireContext(), "Deleting songbook ...")
                            delSongbookLoading.show()
                            songbookViewModel.deleteSongbook(songbookDeleteRequest)
                                .observe(viewLifecycleOwner,
                                    Observer {
                                        when (it) {
                                            is Result.Loading -> {
                                                Log.d(TAG, "Deleting songbook")
                                            }
                                            is Result.Success -> {

                                                delSongbookLoading.dismiss()
                                                Log.d(TAG, "Songbook Deleted")
                                                getSongbooks()
                                                chooserDialog.dismiss()
                                            }
                                        }
                                    })
                        }

                        alertDialogBuilder.setNegativeButton("No") { dialog, which ->
                            dialog.dismiss()
                            chooserDialog.dismiss()
                        }

                        alertDialogBuilder.show()


                    }
                    chooserDialog.show()
                }

            })
        }else{
            val loginBottomSheet = LoginBottomSheetFragment()


            loginBottomSheet.DismissListener(object : DialogDismissListener {
                override fun handleDialogClose(dialog: DialogInterface, isSignedIn: Boolean) {
                    if (isSignedIn) {
                        shortToast("Signed in")
                        findNavController().navigate(R.id.navigation_songbook)
                    } else {
                        shortToast("Not signed in")
                        findNavController().navigate(R.id.navigation_search)
                    }

                }

            })
            loginBottomSheet.show(this.parentFragmentManager, "TAG")
        }

    }

    private fun getSongbooks() {
        songbookViewModel.getSongbooks().observe(viewLifecycleOwner, Observer {

            when (it) {
                is Result.Loading -> {
                    Log.d(TAG, "Loading songbooks")
                }
                is Result.Success -> {

                    Log.d(TAG, "Songbooks loaded")
                    Log.d(TAG,it.data.toString())
                    songbooks = it.data?.songbookList!!
                    songbookAdapter.updateSongbookList(songbooks)
                    tvSongbooksCount.text = "${songbookAdapter.itemCount} Books"
                    if (songbookAdapter.itemCount == 0) {
                        imgNoData.visibility = View.VISIBLE
                        tvNodata.visibility = View.VISIBLE
                    } else {
                        imgNoData.visibility = View.GONE
                        tvNodata.visibility = View.GONE
                    }
                    songbookLoadingDialog.dismiss()
                }
            }
        })
    }

}