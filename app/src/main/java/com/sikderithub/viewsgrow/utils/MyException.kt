package com.sikderithub.viewsgrow.utils

import com.sikderithub.viewsgrow.utils.MyExtensions.longToast

class MyException(){

    companion object{
        fun showDialog(message :String?){

            MyApp.applicationContext().longToast(message!!)

//            GenericDialog.make(context)
//                .setShowNegativeButton(false)
//                .setShowPositiveButton(true)
//                .setCancelable(true)
//                .setIconType(GenericDialog.IconType.ERROR)
//                .setBodyText(message)
//                .setPositiveButtonText("Ok")
//                .setOnGenericDialogListener(object : GenericDialog.OnGenericDialogListener {
//                    override fun onPositiveButtonClick(dialog: GenericDialog?) {
//                        dialog?.hideDialog()
//                    }
//
//                    override fun onNegativeButtonClick(dialog: GenericDialog?) {
//
//                    }
//
//                    override fun onToast(message: String?) {
//
//                    }
//
//                })
//                .build()
//                .showDialog()

        }
    }
}