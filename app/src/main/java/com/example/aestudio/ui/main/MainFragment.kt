package com.example.aestudio.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.aestudio.R
import kotlinx.coroutines.flow.collect
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val service by lazy { retrofit.create(GithubService::class.java) }
    private val repository by lazy { UserRepository(service) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = MainViewModel(repository) //it's not the right way to inject into a viewModel,
        val editText = view.findViewById<EditText>(R.id.editTextUserName)

        view.findViewById<Button>(R.id.buttonSearch).setOnClickListener {
            viewModel.onNextButtonClick(editText.text.toString())
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.user.collect {
                Toast.makeText(requireContext(), "Name of this user : ${it.name}", Toast.LENGTH_SHORT).show()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.errorMessage.collect {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}