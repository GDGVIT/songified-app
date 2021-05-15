package com.dscvit.songified.ui.songbook

import android.app.Dialog
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dscvit.handly.util.*
import com.dscvit.songified.R
import com.dscvit.songified.adapter.SongbookAdapter
import com.dscvit.songified.databinding.FragmentSongbooksBinding
import com.dscvit.songified.model.*
import com.dscvit.songified.ui.login.LoginBottomSheetFragment
import com.dscvit.songified.util.Constants
import com.dscvit.songified.util.DialogDismissListener
import com.dscvit.songified.util.PrefHelper
import com.dscvit.songified.util.PrefHelper.get

import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import org.koin.android.viewmodel.ext.android.getViewModel

class SongbooksFragment : Fragment() {
    lateinit var songbooks: MutableList<Songbook>
    val mTAG = "SongBookFragment"
    lateinit var songbookViewModel: SongbookViewModel

    private lateinit var songbookAdapter: SongbookAdapter
    private lateinit var songbookLoadingDialog: Dialog
    private lateinit var addSongbookLoading: Dialog
    lateinit var editSongbookLoading: Dialog
    lateinit var delSongbookLoading: Dialog

    private lateinit var sharedPref: SharedPreferences

    private var _binding: FragmentSongbooksBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSongbooksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = PrefHelper.customPrefs(requireContext(), Constants.PREF_NAME)
        if (sharedPref[Constants.PREF_IS_AUTH, false]!!) {


            songbookLoadingDialog = createProgressDialog(requireContext(), "Loading songbooks ...")


            songbookAdapter = SongbookAdapter()
            binding.rvSongbooksFragment.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = songbookAdapter
            }
            songbookViewModel = getViewModel()
            songbookLoadingDialog.show()
            getSongbooks()


            binding.fabNewSongbook.setOnClickListener {
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
                        {
                            when (it) {
                                is Result.Loading -> {
                                    Log.d(mTAG, "Creating new songbook")
                                }

                                is Result.Success -> {
                                    addSongbookLoading.dismiss()
                                    Log.d(mTAG, "Songbook created")
                                    Snackbar.make(
                                        binding.root,
                                        "Created ${newSongbookRequest.songbookName}",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                    dialog.dismiss()
                                    getSongbooks()

                                }
                                is Result.Error -> {

                                }
                            }
                        })
                }

                val btnCancel = dialog.findViewById(R.id.btn_dialog_cancel_new_songbook) as Button
                btnCancel.setOnClickListener { dialog.dismiss() }
                dialog.show()
            }

            binding.rvSongbooksFragment.addOnItemClickListener(object : OnItemClickListener {
                override fun onItemClicked(position: Int, view: View) {
                    val selectedSongbookId = songbooks[position].id
                    val selectedSongbookName = songbooks[position].name
                    val bundle = bundleOf(
                        "selected_songbook_id" to selectedSongbookId,
                        "selected_songbook_name" to selectedSongbookName
                    )
                    Log.d(mTAG, selectedSongbookId)
                    view.findNavController()
                        .navigate(R.id.action_songbooks_to_singlesongbook, bundle)
                }

            })

            binding.rvSongbooksFragment.addOnItemLongClickListener(object : OnItemClickListener {
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
                            val updateSongbookNameRequest = UpdateSongbookNameReqeust(
                                songbooks[position].id,
                                etSongbookName.text.toString()
                            )
                            editSongbookLoading =
                                createProgressDialog(requireContext(), "Updating songbook")
                            editSongbookLoading.show()
                            songbookViewModel.updateSongbookName(updateSongbookNameRequest)
                                .observe(viewLifecycleOwner,
                                    {
                                        when (it) {
                                            is Result.Loading -> {
                                                Log.d(mTAG, "Updating songbook name")
                                            }

                                            is Result.Success -> {
                                                editSongbookLoading.dismiss()
                                                Log.d(mTAG, "Songbook Name updated")
                                                Snackbar.make(
                                                    binding.root,
                                                    "Updated",
                                                    Snackbar.LENGTH_SHORT
                                                ).show()
                                                editSongbookDialog.dismiss()
                                                getSongbooks()

                                            }
                                            is Result.Error -> {

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
                        val alertDialogBuilder =
                            AlertDialog.Builder(requireContext(), R.style.MyAlertDialog)
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
                                    {
                                        when (it) {
                                            is Result.Loading -> {
                                                Log.d(mTAG, "Deleting songbook")
                                            }
                                            is Result.Success -> {

                                                delSongbookLoading.dismiss()
                                                Log.d(mTAG, "Deleted ${songbooks[position].name}")
                                                Snackbar.make(
                                                    binding.root,
                                                    "Deleted ${songbooks[position].name}",
                                                    Snackbar.LENGTH_SHORT
                                                ).show()
                                                getSongbooks()
                                                chooserDialog.dismiss()
                                            }
                                            is Result.Error -> {

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
        } else {
            val loginBottomSheet = LoginBottomSheetFragment()


            loginBottomSheet.dismissListener(object : DialogDismissListener {
                override fun handleDialogClose(dialog: DialogInterface, isSignedIn: Boolean) {
                    if (isSignedIn) {

                        findNavController().navigate(R.id.navigation_songbook)
                    } else {

                        findNavController().navigate(R.id.navigation_search)
                    }

                }

            })
            loginBottomSheet.show(this.parentFragmentManager, "TAG")
        }

    }

    private fun getSongbooks() {
        songbookViewModel.getSongbooks().observe(viewLifecycleOwner, {

            when (it) {
                is Result.Loading -> {
                    Log.d(mTAG, "Loading songbooks")
                }
                is Result.Success -> {

                    Log.d(mTAG, "Songbooks loaded")
                    Log.d(mTAG, it.data.toString())
                    songbooks = it.data?.songbookList!!
                    songbookAdapter.updateSongbookList(songbooks)
                    binding.tvSongbooksCount.text =
                        getString(R.string.songbooks_count_data, songbookAdapter.itemCount)
                    if (songbookAdapter.itemCount == 0) {
                        binding.imgNoDataSongbooks.visibility = View.VISIBLE
                        binding.tvNoDataSongbooks.visibility = View.VISIBLE
                    } else {
                        binding.imgNoDataSongbooks.visibility = View.GONE
                        binding.tvNoDataSongbooks.visibility = View.GONE
                    }
                    songbookLoadingDialog.dismiss()
                }
                is Result.Error -> {

                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}