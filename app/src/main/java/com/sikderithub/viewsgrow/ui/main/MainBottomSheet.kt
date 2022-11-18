package com.sikderithub.viewsgrow.ui.main

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sikderithub.viewsgrow.R
import com.sikderithub.viewsgrow.databinding.LayoutNavBottomSheetBinding
import com.sikderithub.viewsgrow.ui.login.LoginActivity
import com.sikderithub.viewsgrow.utils.CommonMethod
import com.sikderithub.viewsgrow.utils.LocalDB
import com.sikderithub.viewsgrow.utils.MyApp
import de.hdodenhof.circleimageview.CircleImageView


class MainBottomSheet : BottomSheetDialogFragment() {

    companion object{
        fun newInstance(): MainBottomSheet {
            return MainBottomSheet()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog =  super.onCreateDialog(savedInstanceState)

        val behavior = (dialog as BottomSheetDialog).behavior
        behavior.isDraggable = true
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        return dialog
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        val binding = LayoutNavBottomSheetBinding.inflate(layoutInflater)
        dialog.setContentView(R.layout.layout_nav_bottom_sheet)

        val profileLayout = dialog.findViewById<LinearLayout>(R.id.layoutProfile)
        val txtName = dialog.findViewById<TextView>(R.id.txtName)
        val txtEmail = dialog.findViewById<TextView>(R.id.txtEmail)
        val imgProfile = dialog.findViewById<CircleImageView>(R.id.imgUser)
        val btnLogin = dialog.findViewById<Button>(R.id.btnLogin)

        val privacyPolicy = dialog.findViewById<LinearLayout>(R.id.layoutPrivacyPolicy)
        val allLink = dialog.findViewById<LinearLayout>(R.id.layoutAllLink)

        dialog.setOnShowListener { myDialog->
            if(MyApp.isLogged()){
                profileLayout.visibility = View.VISIBLE
                btnLogin.visibility = View.GONE

                LocalDB.getProfile()?.let {
                    txtName.text = it.fullName
                    txtEmail.text = it.email

                    Glide.with(this)
                        .load(it.profilePic)
                        .into(imgProfile)
                }

            }else{
                profileLayout.visibility = View.GONE
                btnLogin.visibility = View.VISIBLE

                btnLogin.setOnClickListener {
                    myDialog.cancel()
                    startActivity(Intent(context, LoginActivity::class.java))
                }


            }
        }

        privacyPolicy.setOnClickListener {
            CommonMethod.openPrivacyPolicy(requireContext())
        }

        privacyPolicy.setOnClickListener {
            CommonMethod.openPrivacyPolicy(requireContext())
        }

        allLink.setOnClickListener {
            CommonMethod.openAllLinkActivity(requireContext())
        }


        //dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialogTheme
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
    }


}