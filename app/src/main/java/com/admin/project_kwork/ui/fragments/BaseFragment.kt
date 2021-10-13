package com.admin.project_kwork.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.admin.project_kwork.R

open class BaseFragment(@LayoutRes val layoutRes:Int) : Fragment(layoutRes) {

    //ХЗ НАДО ЛИ ЭТО СЕЙЧАС
    //НО ХОТЕЛ РЕАЛИЗОВАТЬ ОБНОВЛЕНИЕ ИМЕНИ И ФОТО ПРОФИЛЯ ПО НАЖАТИЮ НА ИТЕМ МЕНЮ "Edit"
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setHasOptionsMenu(true)
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.edit_menu,menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when(item.itemId) {
//            R.id.edit -> {
//                //TODO edit data in firebase database
//            } else -> return super.onOptionsItemSelected(item)
//        }
//        return true
//    }


}

