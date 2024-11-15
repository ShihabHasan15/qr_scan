package com.devsyncit.qrscan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.vision.text.Text;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

public class generate_qr extends AppCompatActivity {
    Button generate_btn, download_btn, cancel_button;
    TextInputEditText input1, input2;
    TextInputLayout textField1, textField2;
    BottomSheetDialog dialog;
    ImageView generated_qr, back_arrow;
    LinearLayout banner_ad;
    MaterialToolbar toolbar;
    ColorStateList colorStateList;
    TextView item_email, item_contact, item_url, selected;
    int item_selected = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr);

        toolbar = findViewById(R.id.toolbar);
        input1 = findViewById(R.id.input1);
        input2 = findViewById(R.id.input2);
        textField1 = findViewById(R.id.textField1);
        textField2 = findViewById(R.id.textField2);
        back_arrow = findViewById(R.id.back_arrow);
        banner_ad = findViewById(R.id.banner_ad);
        generate_btn = findViewById(R.id.generate_btn);

        item_email = findViewById(R.id.item_email);
        item_url = findViewById(R.id.item_url);
        selected = findViewById(R.id.selected);

        Animation button_anim = AnimationUtils.loadAnimation(generate_qr.this, R.anim.button_anim);

        colorStateList = item_url.getTextColors();

        item_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                item_selected = 1;

                input1.setText("");
                input1.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

                textField2.setVisibility(View.GONE);
                textField1.setHint("Email Address");

                selected.animate().x(0).setDuration(100);
                item_email.setTextColor(Color.WHITE);
                item_url.setTextColor(colorStateList);

            }
        });

        item_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                item_selected = 2;

                input1.setText("");
                input1.setInputType(InputType.TYPE_CLASS_TEXT);

                textField2.setVisibility(View.GONE);
                textField1.setHint("Enter URL");

                item_email.setTextColor(colorStateList);
                item_url.setTextColor(Color.WHITE);

                int size = item_email.getWidth();
                selected.animate().x(size).setDuration(100);


            }
        });

        //==================================================

        AdView adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-6538546097765410/4579893970");
        adView.setAdSize(AdSize.BANNER);

        banner_ad.removeAllViews();
        banner_ad.addView(adView);

        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
        adView.loadAd(adRequest);

        //====================================================


        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });


        generate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                v.startAnimation(button_anim);

                if (item_selected==0 || item_selected==1) {

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(input1.getWindowToken(), 0);

                    input1.clearFocus();

                    if (!input1.getText().toString().isEmpty()) {

                        String qr_code_txt = input1.getText().toString();

                        if (qr_code_txt.contains("@") && qr_code_txt.contains(".")) {

                            MultiFormatWriter writer = new MultiFormatWriter();

                            try {
                                BitMatrix matrix = writer.encode(qr_code_txt, BarcodeFormat.QR_CODE, 200, 200);
                                BarcodeEncoder encoder = new BarcodeEncoder();
                                Bitmap bitmap = encoder.createBitmap(matrix);
                                customDialog();
                                generated_qr.setImageBitmap(bitmap);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else {
                            Toast.makeText(generate_qr.this, "Please Enter Email Address", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(generate_qr.this, "Please Give Input", Toast.LENGTH_SHORT).show();
                    }

                }else if (item_selected==2) {

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(input1.getWindowToken(), 0);

                    input1.clearFocus();

                    if (!input1.getText().toString().isEmpty()) {

                        String url = input1.getText().toString();

                        if (url.contains(".") && !url.contains("$") && !url.contains("%") && !url.contains("#")
                        && !url.contains("@") && !url.contains("*") && !url.contains("(") && !url.contains(")")){
                            MultiFormatWriter writer = new MultiFormatWriter();
                            try {
                                BitMatrix matrix = writer.encode(url, BarcodeFormat.QR_CODE, 200, 200);
                                BarcodeEncoder encoder = new BarcodeEncoder();
                                Bitmap bitmap = encoder.createBitmap(matrix);
                                customDialog();
                                generated_qr.setImageBitmap(bitmap);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else {
                            Toast.makeText(generate_qr.this, "Not valid", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(generate_qr.this, "Please Give Input", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void customDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.bottom_sheet_design, null, false);

        download_btn = view.findViewById(R.id.download_btn);
        generated_qr = view.findViewById(R.id.generated_qr);
        cancel_button = view.findViewById(R.id.cancel_btn);

        Animation button_anim = AnimationUtils.loadAnimation(generate_qr.this, R.anim.button_anim);

        download_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                generated_qr.setDrawingCacheEnabled(true);
                Bitmap bitmap = generated_qr.getDrawingCache();
                v.startAnimation(button_anim);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){

                if (ContextCompat.checkSelfPermission(generate_qr.this, Manifest.permission.READ_MEDIA_IMAGES)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(generate_qr.this, Manifest.permission.READ_MEDIA_IMAGES)) {
                        // Permission was denied but "Don't ask again" was not selected
                        // Request the permission again
                        ActivityCompat.requestPermissions(generate_qr.this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 1);
                    } else {
                        // Permission was denied and "Don't ask again" was selected
                        // Show an explanation dialog
                        showSettingsDialog();
                    }
                }else {

                    saveImageToGallery(bitmap);
                }

            } else {
                    if (ContextCompat.checkSelfPermission(generate_qr.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(generate_qr.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            // Permission was denied but "Don't ask again" was not selected
                            // Request the permission again
                            ActivityCompat.requestPermissions(generate_qr.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        } else {
                            // Permission was denied and "Don't ask again" was selected
                            // Show an explanation dialog
                            showSettingsDialog();
                        }
                    }else {

                        saveImageToGallery(bitmap);
                    }
                }


            }
        });

        builder.setView(view);

        AlertDialog qr_dialog = builder.create();

        qr_dialog.setCancelable(false);

        qr_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams layoutParams = qr_dialog.getWindow().getAttributes();
        layoutParams.dimAmount = 0.6f;  // Adjust this value to increase or decrease the dim
        qr_dialog.getWindow().setAttributes(layoutParams);

        qr_dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        qr_dialog.show();

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(button_anim);
                qr_dialog.dismiss();
            }
        });


    }


    private void saveImageToGallery(Bitmap bitmap) {
        // Create a filename with the current timestamp
        String filename = "IMG_" + System.currentTimeMillis() + ".png";
        OutputStream fos;

        try {
            // Check if the Android version is Android 10 (Q) or above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Use the MediaStore API for Android Q and above
                ContentResolver resolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

                // Insert the content into MediaStore and get the URI
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                // Open an output stream using the URI
                if (imageUri != null) {
                    fos = resolver.openOutputStream(imageUri);
                    if (fos != null) {
                        // Compress and write the Bitmap to the output stream
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.close();
                        Toast.makeText(this, "QR Code saved to gallery!", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                // For devices below Android Q, save to external storage manually
                String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                File imageFile = new File(imagesDir, filename);

                fos = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();

                // Notify the media scanner about the new file (important for it to appear in the gallery)
                MediaScannerConnection.scanFile(this, new String[]{imageFile.toString()}, null, null);

                Toast.makeText(this, "QR saved to gallery!", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save QR", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                generated_qr.setDrawingCacheEnabled(true);
                Bitmap bitmap = generated_qr.getDrawingCache();

                saveImageToGallery(bitmap);

            } else {
                // Permission denied, notify the user
                Toast.makeText(this, "Permission is required to download the QR code", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void showSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage("This app needs storage permission to download QR codes. Please enable it in the app settings.")
                .setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Open app settings
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

}