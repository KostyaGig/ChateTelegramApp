package com.admin.project_kwork.utils.extensions

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.admin.project_kwork.MainActivity
import com.admin.project_kwork.R
import com.admin.project_kwork.models.User
import com.admin.project_kwork.utils.BundleConstans
import com.bumptech.glide.Glide
import java.io.Serializable

fun Fragment.replaceFragment(fragment: Fragment,addToBackStack: Boolean  = false) {
    val manager = fragmentManager
        ?.beginTransaction()
        ?.replace(R.id.main_container,fragment)

    if (addToBackStack) {
        manager?.addToBackStack(null)
    }
    manager?.commit()
}

fun <T: Serializable> Fragment.replaceWithData(fragment: Fragment,addToBackStack: Boolean  = false,data: T) {
    val bundle = Bundle()
    bundle.putSerializable(BundleConstans.USER,data)
    fragment.arguments = bundle
    val manager = fragmentManager
        ?.beginTransaction()
        ?.replace(R.id.main_container,fragment)

    if (addToBackStack) {
        manager?.addToBackStack(null)
    }

    manager?.commit()
}

fun Fragment.popBackStack(){
    fragmentManager?.popBackStack()
}

fun Fragment.showMessage(message: String,context: Context) {
    Toast.makeText(
        context,
        message,
        Toast.LENGTH_SHORT
    ).show()
}

fun Fragment.setImage(imageUrl: String,image: ImageView) {
    Glide
        .with(requireContext())
        .load(imageUrl)
        .placeholder(R.drawable.ic_person)
        .into(image)
}

fun Fragment.changeTitleToolbar(title: String) {
    (activity as MainActivity).supportActionBar?.title = title
}

