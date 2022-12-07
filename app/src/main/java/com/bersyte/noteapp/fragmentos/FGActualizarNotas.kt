package com.bersyte.noteapp.fragmentos

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bersyte.noteapp.MainActivity
import com.bersyte.noteapp.R
import com.bersyte.noteapp.databinding.FgActualizarNotaBinding
import com.bersyte.noteapp.model.Note
import com.bersyte.noteapp.toast
import com.bersyte.noteapp.viewmodel.NoteViewModel
import java.text.SimpleDateFormat
import java.util.*


class FGActualizarNotas : Fragment(R.layout.fg_actualizar_nota) {

    private var _binding: FgActualizarNotaBinding? = null
    private val binding get() = _binding!!

    private val args: FGActualizarNotasArgs by navArgs()
    private lateinit var notaactual: Note
    private lateinit var vistadelmodelonota: NoteViewModel

    var fotoident: Uri? = null
    var videoident: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FgActualizarNotaBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vistadelmodelonota = (activity as MainActivity).noteViewModel
        notaactual = args.note!!

        var diactual: String? = null
        val formato = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        diactual = formato.format(Date())

        binding.etNoteBodyUpdate.setText(notaactual.noteBody)
        binding.tvNoteDateUpdate.setText(diactual)
        binding.etNoteSubTitleUpdate.setText(notaactual.noteSubTitle)
        binding.etNoteTitleUpdate.setText(notaactual.noteTitle)

        val bundle = Bundle()
        bundle.putString("id", notaactual.id.toString())

        binding.btnFotoT.setOnClickListener {
            it.findNavController().navigate(R.id.action_updateNoteFragment_to_photoFragment, bundle)
        }

        binding.btnVideoT.setOnClickListener {
            it.findNavController().navigate(R.id.action_updateNoteFragment_to_videoFragment, bundle)
        }

        binding.btnAudioT.setOnClickListener {
            it.findNavController().navigate(R.id.action_updateNoteFragment_to_audio, bundle)
        }

        binding.multimediaTask.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_updateNoteFragment_to_viewMultimediaFragment, bundle)
        }
    }

    private fun deleteNote() {
        AlertDialog.Builder(activity).apply {
            setTitle("Borrar nota")
            setMessage("¿Seguro que deseas eliminar la nota?")
            setPositiveButton("Eliminar") { _, _ ->
                vistadelmodelonota.borrarNota(notaactual)
                view?.findNavController()?.navigate(
                    R.id.action_updateNoteFragment_to_homeFragment
                )
            }
            setNegativeButton("Cancelar", null)
        }.create().show()
    }

    private fun saveNote() {
        val title = binding.etNoteTitleUpdate.text.toString().trim()
        val subTitle = binding.etNoteSubTitleUpdate.text.toString().trim()
        val date = binding.tvNoteDateUpdate.text.toString().trim()
        val body = binding.etNoteBodyUpdate.text.toString().trim()

        var imagen = ""
        var video = ""

        if (fotoident != null) {
            imagen = fotoident.toString()
        }

        if (videoident != null) {
            video = videoident.toString()
        }

        if (title.isNotEmpty()) {
            val note = Note(notaactual.id, title, subTitle, date, body)
            vistadelmodelonota.actualizarNota(note)

            view?.findNavController()?.navigate(R.id.action_updateNoteFragment_to_homeFragment)

        } else {
            activity?.toast("Ingresa un Titulo")
        }

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_actualizar_nota, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete -> {
                deleteNote()
            }
            R.id.menu_save -> {
                saveNote()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}