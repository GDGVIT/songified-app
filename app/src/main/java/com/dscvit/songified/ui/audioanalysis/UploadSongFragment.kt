package com.dscvit.songified.ui.audioanalysis

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dscvit.handly.util.*
import com.dscvit.songified.R
import com.dscvit.songified.adapter.UploadsAdapter
import com.dscvit.songified.databinding.FragmentUploadSongBinding
import com.dscvit.songified.model.Result
import com.dscvit.songified.model.UploadedSong
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.android.viewmodel.ext.android.getViewModel
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class UploadSongFragment : Fragment() {
    private val mTAG = "UploadSongFragment"

    private val rcPickAudio = 102


    private var _binding: FragmentUploadSongBinding? = null


    private lateinit var uploadsList: MutableList<UploadedSong>

    private lateinit var uploadSongViewModel: UploadSongViewModel
    private lateinit var uploadsAdapter: UploadsAdapter
    private lateinit var uploadProgressDialog: Dialog
    private lateinit var previousUploadsProgressDialog: Dialog

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUploadSongBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        uploadSongViewModel = getViewModel()
        binding.toolbarUploadFragment.setNavigationOnClickListener {
            it.findNavController().navigateUp()
        }

        uploadProgressDialog =
            createDialog(requireContext(), false, R.layout.dialog_loading_with_progress)

        val pbUpload =
            uploadProgressDialog.findViewById(R.id.pb_progress_dialog_loading) as LinearProgressIndicator
        val tvProgressMsg =
            uploadProgressDialog.findViewById(R.id.tv_loading_dialog_progress_msg) as TextView
        tvProgressMsg.text = getString(R.string.uploading_file)
        pbUpload.isIndeterminate = true

        binding.btnUploadFragmentUpload.setOnClickListener {
            val intent = Intent()
                .setType("audio/*")
                .setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Select a song"), rcPickAudio)


        }

        binding.refreshPreviousUploads.setOnRefreshListener {


            // This method performs the actual data-refresh operation.
            // The method calls setRefreshing(false) when it's finished.
            getPreviousUploads()
        }

        uploadsAdapter = UploadsAdapter()
        binding.rvPreviousUploads.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = uploadsAdapter
            fixSwipeToRefresh(binding.refreshPreviousUploads)
        }

        previousUploadsProgressDialog =
            createProgressDialog(requireContext(), "Loading previous uploads ...")

        getPreviousUploads()



        binding.rvPreviousUploads.addOnItemClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                val selectedUploadId = uploadsList[position].id
                val selectedSongName = uploadsList[position].name
                val bundle = bundleOf(
                    "analyse_song_id" to selectedUploadId,
                    "analyse_song_name" to selectedSongName
                )
                findNavController().navigate(R.id.action_upload_to_analysis_result, bundle)
            }
        })


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == rcPickAudio) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(mTAG, "Pick audio file RESULT_OK")
                val selectedFileUri = data?.data!!


                //val audioFile = File(selectedFile.toString())

                val parcelFileDescriptor =
                    requireContext().contentResolver.openFileDescriptor(selectedFileUri, "r", null)
                        ?: return

                val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                val audioFile = File(
                    requireContext().cacheDir,
                    requireContext().contentResolver.getFileName(selectedFileUri)
                )


                val outputStream = FileOutputStream(audioFile)
                inputStream.copyTo(outputStream)
                if (audioFile.length() < 10000000) {
                    val uploadDialog =
                        createDialog(requireContext(), false, R.layout.dialog_upload_song)
                    val btnUploadSubmit =
                        uploadDialog.findViewById(R.id.btn_upload_song_dialog) as Button
                    val etFileName =
                        uploadDialog.findViewById(R.id.et_file_name_dialog_upload_song) as TextInputEditText
                    val btnCancel =
                        uploadDialog.findViewById(R.id.btn_cancel_dialog_upload_song) as Button
                    etFileName.setText(requireContext().contentResolver.getFileName(selectedFileUri))



                    btnUploadSubmit.setOnClickListener {

                        if (etFileName.text.toString().trim() != "") {


                            val filePart: MultipartBody.Part = MultipartBody.Part.createFormData(
                                "songFile",
                                audioFile.name,
                                audioFile.asRequestBody("audio/*".toMediaTypeOrNull())
                            )
                            uploadProgressDialog.show()
                            val uploadSongName = etFileName.text.toString().toRequestBody()
                            uploadSongViewModel.uploadSong(uploadSongName, filePart)
                                .observe(viewLifecycleOwner,
                                    {
                                        when (it) {
                                            is Result.Loading -> {
                                                Log.d(mTAG, "Uploading Song")
                                                createDialog(requireContext(),true,R.layout.dialog_server_maintenance).show();
                                            }
                                            is Result.Success -> {
                                                Log.d(
                                                    mTAG,
                                                    "Song Uploaded${it.data?.songProcessId}"
                                                )
                                                uploadProgressDialog.dismiss()
                                                uploadDialog.dismiss()
                                                getPreviousUploads()


                                            }
                                            is Result.Error -> {
                                                if(it.message?.contains("librarylimitreached")==true){
                                                        createDialog(requireContext(),true,R.layout.dialog_server_maintenance).show();

                                                }

                                            }
                                        }
                                    })
                        }else{
                            etFileName.error="required"
                        }
                    }

                    btnCancel.setOnClickListener { uploadDialog.dismiss() }

                    uploadDialog.show()
                } else {
                    Snackbar.make(
                        binding.root,
                        "Choose a file less than 10 MB",
                        Snackbar.LENGTH_LONG
                    ).show()
                }


            } else {
                Log.d(mTAG, "Pick audio file result not ok")
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getPreviousUploads() {
        previousUploadsProgressDialog.show()
        uploadSongViewModel.getPreviousUploads().observe(viewLifecycleOwner, {
            when (it) {
                is Result.Loading -> {
                    Log.d(mTAG, "Loading previous uploads")
                }
                is Result.Success -> {

                    previousUploadsProgressDialog.dismiss()
                    Log.d(mTAG, "Previous uploads loaded")
                    Log.d(mTAG, it.data.toString())
                    uploadsList = it.data?.songAnalysisResponse!!
                    uploadsAdapter.updateUploads(uploadsList)
                    binding.refreshPreviousUploads.isRefreshing = false

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
