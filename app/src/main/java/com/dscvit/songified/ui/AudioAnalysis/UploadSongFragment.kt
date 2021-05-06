package com.dscvit.songified.ui.AudioAnalysis

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dscvit.handly.util.*
import com.dscvit.songified.R
import com.dscvit.songified.adapter.UploadsAdapter
import com.dscvit.songified.model.PreviousUploadsResponse
import com.dscvit.songified.model.Result
import com.dscvit.songified.model.SongAnalysisResponse
import com.dscvit.songified.model.UploadedSong
import com.dscvit.songified.ui.search.SearchViewModel
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.create
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class UploadSongFragment : Fragment() {
    val TAG = "UploadSongFragment"
    val RC_PICK_AUDIO = 102
    lateinit var uploadsList: MutableList<UploadedSong>

    lateinit var uploadSongViewModel: UploadSongViewModel
    lateinit var uploadsAdapter: UploadsAdapter
    lateinit var uploadProgressDialog: Dialog
    lateinit var previousUploadsProgressDialog: Dialog
    lateinit var btnUpload:Button
    lateinit var refreshUploads:SwipeRefreshLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload_song, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnUpload = view.findViewById(R.id.btn_upload_fragment_upload) as Button


        uploadSongViewModel = getViewModel()

        uploadProgressDialog = createDialog(requireContext(), false,R.layout.dialog_loading_with_progress)

        val pbUpload =
            uploadProgressDialog.findViewById(R.id.pb_progress_dialog_loading) as LinearProgressIndicator
        val tvProgressMsg =
            uploadProgressDialog.findViewById(R.id.tv_loading_dialog_progress_msg) as TextView
        tvProgressMsg.text = "Uploading file ..."
        pbUpload.isIndeterminate = true

        btnUpload.setOnClickListener {
            val intent = Intent()
                .setType("audio/*")
                .setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Select a song"), RC_PICK_AUDIO)
            //val bundle = bundleOf("analyse_song_id" to "1311646")
            //it.findNavController().navigate(R.id.action_search_to_audio_analysis_result, bundle)
        }
        refreshUploads=view.findViewById(R.id.refresh_previous_uploads) as SwipeRefreshLayout
        refreshUploads.setOnRefreshListener {


            // This method performs the actual data-refresh operation.
            // The method calls setRefreshing(false) when it's finished.
            getPreviousUploads()
        }

        val rvUploads = view.findViewById(R.id.rv_previous_uploads) as RecyclerView
        uploadsAdapter = UploadsAdapter()
        rvUploads.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = uploadsAdapter
        }

        previousUploadsProgressDialog = createProgressDialog(requireContext(), "Loading previous uploads ...")

        getPreviousUploads()



        rvUploads.addOnItemClickListener(object : OnItemClickListener {
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
        if (requestCode == RC_PICK_AUDIO) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "Pick audio file RESULT_OK")
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
                if (audioFile.length()<10000000){
                    val uploadDialog= createDialog(requireContext(),false,R.layout.dialog_upload_song)
                    val btnUploadSubmit=uploadDialog.findViewById(R.id.btn_upload_song_dialog) as Button
                    val etFileName=uploadDialog.findViewById(R.id.et_file_name_dialog_upload_song) as TextInputEditText
                    val btnCancel=uploadDialog.findViewById(R.id.btn_cancel_dialog_upload_song) as Button
                    etFileName.setText(requireContext().contentResolver.getFileName(selectedFileUri))



                    btnUploadSubmit.setOnClickListener {
                        val filePart: MultipartBody.Part = MultipartBody.Part.createFormData(
                            "songFile",
                            audioFile.getName(),
                            audioFile.asRequestBody("audio/*".toMediaTypeOrNull())
                        )
                        uploadProgressDialog.show()
                        val uploadSongName=etFileName.text.toString().toRequestBody()
                        uploadSongViewModel.uploadSong(uploadSongName,filePart).observe(viewLifecycleOwner, Observer {
                            when (it) {
                                is Result.Loading -> {
                                    Log.d(TAG, "Uploading Song")
                                }
                                is Result.Success -> {
                                    Log.d(TAG, "Song Uploaded${it.data?.songProcessId}")
                                    uploadProgressDialog.dismiss()
                                    uploadDialog.dismiss()
                                    getPreviousUploads()


                                }
                            }
                        })
                    }

                    btnCancel.setOnClickListener{uploadDialog.dismiss()}

                    uploadDialog.show()
                }else{
                    Snackbar.make(btnUpload,"Choose a file less than 10 MB",Snackbar.LENGTH_LONG).show()
                }



            } else {
                Log.d(TAG, "Pick audio file result not ok")
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getPreviousUploads() {
        previousUploadsProgressDialog.show()
        uploadSongViewModel.getPreviousUploads().observe(viewLifecycleOwner, Observer {
            when (it) {
                is Result.Loading -> {
                    Log.d(TAG, "Loading previous uploads")
                }
                is Result.Success -> {
                    previousUploadsProgressDialog.dismiss()
                    Log.d(TAG, "Previous uploads loaded")
                    Log.d(TAG, it.data.toString())
                    uploadsList = it.data?.songAnalysisResponse!!
                    uploadsAdapter.updateUploads(uploadsList)
                    refreshUploads.isRefreshing=false

                }
            }
        })


    }


}
