package com.yamato.myflashcard_for_sqlite

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yamato.myflashcard_for_sqlite.databinding.MainFragmentBinding

class MainFragment:Fragment(R.layout.main_fragment) {

    private val vm: MainViewModel by viewModels()
    private var _binding: MainFragmentBinding? = null
    private val binding: MainFragmentBinding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this._binding = MainFragmentBinding.bind(view)

        binding.buttonAddwordMain.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_wordAddFragment)
//            Toast.makeText(context, "Move Successfully",Toast.LENGTH_SHORT).show()
        }

        binding.buttonListMain.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_wordListFragment)
        }

        binding.buttonLessonMain.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_wordLessonFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this._binding = null
    }
}