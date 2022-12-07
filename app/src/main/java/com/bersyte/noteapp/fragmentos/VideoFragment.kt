package com.bersyte.noteapp.fragmentos

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bersyte.noteapp.databinding.FragmentVideoBinding
import com.bersyte.noteapp.db.NoteDatabase
import com.bersyte.noteapp.model.Multimedia
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class VideoFragment : Fragment() {

    private lateinit var binding: FragmentVideoBinding
    private lateinit var videoURI: Uri
    private lateinit var miContext: Context
    private lateinit var controladordemedia: MediaController

    override fun onAttach(context: Context) {
        super.onAttach(context)
        miContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoBinding.inflate(layoutInflater)

        binding.takeVideo.setOnClickListener {
            validarPermisos()
        }

        binding.saveVideo.setOnClickListener {
            val file = Multimedia (
                arguments?.getString("id")!!.toInt(),
                "video",
                videoURI.toString(),
                binding.description.text.toString()
            )
            //Insertar
            NoteDatabase.getInstance(requireActivity().applicationContext).MultimediaDao().insert(file)

            binding.saveVideo.visibility = View.INVISIBLE
            binding.takeVideo.visibility = View.INVISIBLE
            binding.description.isEnabled = false
        }

        controladordemedia = MediaController(miContext)
        controladordemedia.setAnchorView(binding.root)
        binding.videoView .setMediaController(controladordemedia)

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_TAKE_VIDEO && resultCode == AppCompatActivity.RESULT_OK){
            binding.videoView.setVideoURI(videoURI)
            binding.videoView.start()
            binding.videoView.setOnClickListener {
                controladordemedia.show()
            }
        }
    }

    private lateinit var currentVideoPath: String
    @Throws(IOException::class)
    fun createVideoFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = activity?.getExternalFilesDir(null)

        return File.createTempFile(
            "VIDEO_${timeStamp}_",
            ".mp4",
            storageDir /* directory */
        ).apply {
            // Save a file
            currentVideoPath = absolutePath
        }
    }

    private val REQUEST_TAKE_VIDEO: Int = 1001
    private fun tomarVideo() {
        Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takePictureIntent ->

            activity?.let {
                takePictureIntent.resolveActivity(it.packageManager)?.also {

                    val photoFile: File? = try {
                        createVideoFile()
                    } catch (ex: IOException) {

                        null
                    }

                    photoFile?.also {
                        videoURI= FileProvider.getUriForFile(
                            miContext,
                            "com.example.noteeapp.fileprovider",
                            it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI)
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_VIDEO)
                    }
                }
            }
        }
    }

    private fun validarPermisos() {
        when {
            ContextCompat.checkSelfPermission(
                miContext,
                "android.permission.WRITE_EXTERNAL_STORAGE"
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        miContext,
                        "android.permission.CAMERA"
                    ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                tomarVideo()
            }
            shouldShowRequestPermissionRationale("android.permission.CAMERA") -> {
                val dialog = AlertDialog.Builder(miContext).apply {
                    setTitle("Acepta permisos, por favor :c")
                    setMessage("Acepta los permisos para poder guardar archivos multimedia en tus tareas y notas")
                        .setNegativeButton("Ok", DialogInterface.OnClickListener {
                                dialogInterface, i ->
                        })
                        .setPositiveButton("Solicitar permiso de nuevo",
                            DialogInterface.OnClickListener { dialogInterface, i ->
                                requestPermissions(
                                    arrayOf("android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"),
                                    REQUEST_TAKE_VIDEO)
                            })
                    create()
                }
                dialog.show()
            }
            else -> {
                // You can directly ask for the permission.
                requestPermissions(
                    arrayOf("android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"),
                    REQUEST_TAKE_VIDEO)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_TAKE_VIDEO -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    tomarVideo()
                } else {

                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

}