package com.example.todolist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.todolist.databinding.FragmentTodoListBinding

class TodoListFragment: Fragment() {

     private var binding: FragmentTodoListBinding? = null

     override fun onCreateView(
          inflater: LayoutInflater,
          container: ViewGroup?,
          savedInstanceState: Bundle?
     ): View? {
          val fragmentBinding = FragmentTodoListBinding.inflate(inflater, container, false)
          binding = fragmentBinding
          return fragmentBinding.root
     }
}