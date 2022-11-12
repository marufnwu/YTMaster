package com.sikderithub.ytmaster.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.bumptech.glide.Glide;
import com.sikderithub.ytmaster.R;


public class GenericDialog {

    public enum IconType{
        SUCCESS,
        WARNING,
        ERROR
    }



    private String TAG = "GenericDialog";
    private String negativeButtonText, positiveButtonText, bodyText;
    @SuppressLint("StaticFieldLeak")
    public static Context context;
    private Dialog dialog;
    public boolean showPositiveButton = true;
    public boolean showNegativeButton = true;
    private TextView txtBody;
    private boolean cancelable = false;
    private Image image;
    private IconType iconType;



    @SuppressLint("StaticFieldLeak")
    private static GenericDialog genericDialog;

    private GenericDialog(Context context) {
        GenericDialog.context = context;
        setupDialog();
    }

    private GenericDialog instance(){
        return this;
    }



    public static  GenericDialog make(Context context){
        Log.d("GenericDialog", "make called");
        if(genericDialog == null){
            Log.d("GenericDialog", "new dialog object");

            genericDialog = new GenericDialog(context);
            //setLifeCycle(context);
        }

        return genericDialog;
    }

    private  void setupDialog() {
        if(dialog==null){
            dialog = new Dialog(context);
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(cancelable);
        dialog.setContentView(R.layout.dialog_generic);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    }

    private  static void setLifeCycle(Context context) {
        LifecycleOwner lifeCycle = MyExtensions.INSTANCE.lifecycleOwner(context);
        if(lifeCycle!=null){

            lifeCycle.getLifecycle()
                    .addObserver(new LifecycleEventObserver() {

                        @Override
                        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {

                            if(Lifecycle.Event.ON_DESTROY == event){
                               destroyDialogObject();
                           }
                        }
                    });
        }else{
        }
    }

    private static void destroyDialogObject() {
        genericDialog.hideDialog();
        genericDialog = null;
    }

    public GenericDialog setImage(Image image) {
        this.image = image;
        return this;
    }

    public GenericDialog setNegativeButtonText(String negativeButtonText) {
        this.negativeButtonText = negativeButtonText;
        return this;
    }

    public GenericDialog setPositiveButtonText(String positiveButtonText) {
        this.positiveButtonText = positiveButtonText;
        return this;
    }

    public GenericDialog setBodyText(String bodyText) {
        this.bodyText = bodyText;
        return this;
    }

    public GenericDialog setCancelable(boolean cancelable){
        this.cancelable = cancelable;
        return this;
    }

    public GenericDialog setIconType(IconType iconType){
        this.iconType = iconType;
        return this;
    }



    public boolean isShowNegativeButton() {
        return showNegativeButton;
    }

    public GenericDialog setShowNegativeButton(boolean showNegativeButton) {
        this.showNegativeButton = showNegativeButton;
        return this;
    }

    public boolean isShowPositiveButton() {
        return showPositiveButton;
    }

    public GenericDialog setShowPositiveButton(boolean showPositiveButton) {
        this.showPositiveButton = showPositiveButton;
        return this;
    }

    public OnGenericDialogListener onGenericDialogListener;

    public  interface OnGenericDialogListener {
        void onPositiveButtonClick(GenericDialog dialog);
        void onNegativeButtonClick(GenericDialog dialog);
        void onToast(String message);
    }

    public OnGenericDialogListener getOnGenericDialogListener() {
        return onGenericDialogListener;
    }

    public GenericDialog setOnGenericDialogListener(OnGenericDialogListener onGenericDialogListener){
        this.onGenericDialogListener = onGenericDialogListener;
        return this;
    }

    public TextView getTxtBody() {
        return txtBody;
    }


    public GenericDialog build(){
        Button btnPositive = dialog.findViewById(R.id.btnPositive);
        Button btnNegative = dialog.findViewById(R.id.btnNegative);
        ImageView imageView = dialog.findViewById(R.id.img);
        ImageView icon = dialog.findViewById(R.id.imgIcon);

        FrameLayout imgContainer = dialog.findViewById(R.id.imgContainer);

        txtBody = dialog.findViewById(R.id.txtBody);

        if(iconType==null){
            icon.setVisibility(View.GONE);
        }else{
            switch (iconType){
                case SUCCESS:
                    icon.setImageResource(R.drawable.success);
                    break;
                case WARNING:
                    icon.setImageResource(R.drawable.warning);
                    break;
                case ERROR:
                    icon.setImageResource(R.drawable.error);
                    break;
            }
        }

        if(image!=null){
            if(image.imagePath!=null){
                imageView.setVisibility(View.VISIBLE);
                imgContainer.setVisibility(View.VISIBLE);

                Glide.with(context)
                        .load(image.imagePath)
                        .into(imageView);


                String link = image.link;
                if(link!=null){
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    });
                }
            }
        }else{
            imageView.setVisibility(View.GONE);
            imgContainer.setVisibility(View.GONE);
        }

        if (bodyText!=null){
            txtBody.setText(bodyText);
        }else {
            txtBody.setVisibility(View.GONE);
        }

        if (negativeButtonText!=null){
            btnNegative.setText(negativeButtonText);
        }else {
            btnNegative.setVisibility(View.GONE);
        }

        if (positiveButtonText!=null){
            btnPositive.setText(positiveButtonText);
        }else {
            btnNegative.setVisibility(View.GONE);
        }

        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGenericDialogListener.onPositiveButtonClick(instance());
            }
        });

        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGenericDialogListener.onNegativeButtonClick(instance());
            }
        });

        if (showNegativeButton){
            btnNegative.setVisibility(View.VISIBLE);
        }else{
            btnNegative.setVisibility(View.GONE);
        }

        if (showPositiveButton){
            btnPositive.setVisibility(View.VISIBLE);
        }else{
            btnPositive.setVisibility(View.GONE);
        }


        setImage(null);


        return this;


    }

    public void showDialog(){

        Activity acc = (Activity) context;


        if (dialog!=null && !acc.isFinishing() && !acc.isDestroyed()){
            if(dialog.isShowing()){
                hideDialog();
            }
            dialog.show();
        }
    }

    public void hideDialog(){
        Activity acc = (Activity) context;

        if (dialog!=null && !acc.isFinishing() && !acc.isDestroyed()){
            dialog.dismiss();
        }
    }

    static class Image{
        private String imagePath;
        private String link;

        public Image(String imagePath, String link) {
            this.imagePath = imagePath;
            this.link = link;
        }
    }

}


