package com.bersyte.noteapp.fragmentos

import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bersyte.noteapp.databinding.FragmentAudioBinding
import com.bersyte.noteapp.db.NoteDatabase
import com.bersyte.noteapp.model.Multimedia
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


class Audio : Fragment(){

    private var comenzargrabar: Boolean = true
    private var NombreArchivo: String = ""
    private var grabar: MediaRecorder? = null
    private var play: MediaPlayer? = null
    private lateinit var miContexto: Context

    // Solicitar permisos AUDIO
    override fun onAttach(context: Context) {
        super.onAttach(context)
        miContexto = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAudioBinding.inflate(layoutInflater)

        binding.btnStart.setOnClickListener {
            revisarPermisos()
        }

        binding.btnStop.setOnClickListener {
            stopRecording()
            Play(mStartPlaying)
            mStartPlaying = !mStartPlaying
        }

       binding.btnGuardar.setOnClickListener{
           val file = Multimedia (
               arguments?.getString("id")!!.toInt(),
               "audio",
               NombreArchivo,
               binding.txtDescripcionNota.text.toString()
           )
           //Insertamos
           NoteDatabase.getInstance(requireActivity().applicationContext).MultimediaDao().insert(file)

           binding.btnStart.visibility = View.INVISIBLE
           binding.btnStop.visibility = View.INVISIBLE
           binding.btnGuardar.visibility = View.INVISIBLE
           binding.txtDescripcionNota.isEnabled = false
       }
        return binding.root
    }

    private fun Play(start: Boolean) = if (start) {
        startPlaying()
    } else {
        stopPlay()
    }

    private fun stopPlay() {
        play?.release()
        play = null
    }

    private fun startPlaying() {
        play = MediaPlayer().apply {
            try {
                setDataSource(NombreArchivo)
                prepare()
                start()
            } catch (e: IOException) {
                //Log.e(LOG_TAG, "prepare() failed")
            }
        }
    }

    private var mStartPlaying = true

    private fun onRecord(start: Boolean) = if (start) {
        iniciarGrabacion()
    } else {
        stopRecording()
    }

    private fun revisarPermisos() {
        when {
            ContextCompat.checkSelfPermission(
                miContexto,
                "android.permission.RECORD_AUDIO"
            ) == PackageManager.PERMISSION_GRANTED -> {
                onRecord(comenzargrabar)
                comenzargrabar = !comenzargrabar
            }
            shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO") -> {
                MaterialAlertDialogBuilder( miContexto
                )
                    .setTitle("Title")
                    .setMessage("Debes dar perimso para grabar audios")
                    .setNegativeButton("Cancel") { _, _ ->
                    }
                    .setPositiveButton("OK") { _, _ ->

                        requestPermissions(
                            arrayOf("android.permission.RECORD_AUDIO",
                                "android.permission.WRITE_EXTERNAL_STORAGE"),
                            1001)
                    }
                    .show()
            }
            else -> {
                requestPermissions(
                    arrayOf("android.permission.RECORD_AUDIO",
                        "android.permission.WRITE_EXTERNAL_STORAGE"),
                    1001)
            }
        }
    }

    override fun onRequestPermissionsResult(
        solicitarcodigo: Int,
        permisos: Array<out String>,
        OtorgarResultados: IntArray

    ) {
        super.onRequestPermissionsResult(solicitarcodigo, permisos, OtorgarResultados)
        when (solicitarcodigo) {
            1001 -> {
                if ((OtorgarResultados.isNotEmpty() &&
                            OtorgarResultados[0] == PackageManager.PERMISSION_GRANTED &&
                            OtorgarResultados[1] == PackageManager.PERMISSION_GRANTED
                            )) {
                    onRecord(comenzargrabar)
                    comenzargrabar = !comenzargrabar
                } else {

                }
                return
            }
            else -> {
            }
        }
    }

    private fun iniciarGrabacion() {
        grabar = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            createAudioFile()
            setOutputFile(NombreArchivo)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            try {
                prepare()
            } catch (e: IOException) {

            }
            start()
        }
    }

    private fun stopRecording() {
        grabar?.apply {
            stop()
            release()
        }
        grabar = null
    }

    @Throws(IOException::class)
    fun createAudioFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = activity?.getExternalFilesDir(null)
        return File.createTempFile(
            "AUDIO_${timeStamp}_",
            ".mp3",
            storageDir
        ).apply {
            NombreArchivo = absolutePath
        }
    }
}