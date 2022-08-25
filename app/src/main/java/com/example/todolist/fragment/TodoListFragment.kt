package com.example.todolist.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.adapter.TodoListAdapter
import com.example.todolist.databinding.FragmentTodoListBinding
import com.example.todolist.viewmodel.TodoItemsViewModel

class TodoListFragment: Fragment() {

     private var binding: FragmentTodoListBinding? = null

     private val viewModel: TodoItemsViewModel by activityViewModels()

     override fun onCreateView(
          inflater: LayoutInflater,
          container: ViewGroup?,
          savedInstanceState: Bundle?
     ): View? {
          val fragmentBinding = FragmentTodoListBinding.inflate(inflater, container, false)
          binding = fragmentBinding
          return fragmentBinding.root
     }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
          super.onViewCreated(view, savedInstanceState)

          val adapter = TodoListAdapter {
               val action = TodoListFragmentDirections.actionTodoListFragmentToEditItemFragment(it.id)
               this.findNavController().navigate(action)
          }

          adapter.submitList(viewModel.allItems)
          binding?.recyclerView?.adapter = adapter

          binding?.recyclerView?.layoutManager = LinearLayoutManager(this.context)
          binding?.addNewTask?.setOnClickListener {
               val action = TodoListFragmentDirections.actionTodoListFragmentToEditItemFragment(
                    "03"
               )
               this.findNavController().navigate(action)
          }

          binding?.showDoneTasks?.setOnClickListener {
               Toast.makeText(requireContext(), "Hide done tasks", Toast.LENGTH_SHORT).show()
          }

//          binding?.doneTasks?.visibility = View.INVISIBLE
          binding?.appBarLayout?.addOnOffsetChangedListener { appBarLayout, verticalOffset ->

               binding?.doneTasks?.alpha = (appBarLayout.totalScrollRange + verticalOffset).toFloat() / appBarLayout.totalScrollRange

          }
     }


}