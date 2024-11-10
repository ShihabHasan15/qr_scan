package com.devsyncit.qrscan;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.mlkit.vision.common.InputImage;

import java.io.IOException;

public class text_recognize_activity extends AppCompatActivity {

    MaterialButton choose_button;
    Button copy_button, share_button, clear_button;
    ImageView display_image, back_arrow;
    TextView recognized_text;
    Animation button_anim;
    LinearLayout banner_ad;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_recognize);

        //buttons

        choose_button = findViewById(R.id.choose_button);
        copy_button = findViewById(R.id.copy_button);
        share_button = findViewById(R.id.share_button);
        clear_button = findViewById(R.id.clear_button);
        back_arrow = findViewById(R.id.back_arrow);

        //



        display_image = findViewById(R.id.displayImage);
        recognized_text = findViewById(R.id.recognized_text);
        banner_ad = findViewById(R.id.banner_ad);



        //==================================================

        AdView adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-6538546097765410/4579893970");
        adView.setAdSize(AdSize.BANNER);

        banner_ad.removeAllViews();
        banner_ad.addView(adView);

        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
        adView.loadAd(adRequest);

        //====================================================



        button_anim = AnimationUtils.loadAnimation(text_recognize_activity.this, R.anim.button_anim);


        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });


        ActivityResultLauncher<Intent> gallery_launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {

                 Uri selectedImage = result.getData().getData();
                 display_image.setImageURI(selectedImage);

                FirebaseVisionImage image;
                try {
                    image = FirebaseVisionImage.fromFilePath(getApplicationContext(), result.getData().getData());

                    FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                            .getOnDeviceTextRecognizer();

                    Task<FirebaseVisionText> vision_result =
                            detector.processImage(image)
                                    .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                        @Override
                                        public void onSuccess(FirebaseVisionText firebaseVisionText) {

                                            copy_button.setEnabled(true);
                                            share_button.setEnabled(true);
                                            clear_button.setEnabled(true);


                                            String output_text = firebaseVisionText.getText().toString();
                                            recognized_text.setText(output_text);

                                            copy_button.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    v.startAnimation(button_anim);

                                                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                                    ClipData clipData = ClipData.newPlainText("Data", output_text);
                                                    clipboardManager.setPrimaryClip(clipData);
                                                    Toast.makeText(text_recognize_activity.this, "Copied", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                            share_button.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    v.startAnimation(button_anim);

                                                    Intent sendIntent = new Intent();
                                                    sendIntent.setAction(Intent.ACTION_SEND);
                                                    sendIntent.putExtra(Intent.EXTRA_TEXT, output_text);
                                                    sendIntent.setType("text/plain");
                                                    startActivity(sendIntent);

                                                }
                                            });

                                            clear_button.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    v.startAnimation(button_anim);

                                                    recognized_text.setText("");
                                                }
                                            });
                                        }
                                    })
                                    .addOnFailureListener(
                                            new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                    Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_SHORT).show();

                                                }
                                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                        } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                            // Handle back button press
                            Toast.makeText(text_recognize_activity.this, "No image selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );


        choose_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(button_anim);

                if (ContextCompat.checkSelfPermission(text_recognize_activity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(text_recognize_activity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // Permission was denied but "Don't ask again" was not selected
                        // Request the permission again
                        ActivityCompat.requestPermissions(text_recognize_activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                    } else {
                        // Permission was denied and "Don't ask again" was selected
                        // Show an explanation dialog
                        showSettingsDialog();
                    }

                }else{

                    Intent i = new Intent();
                    i.setType("image/*");
                    i.setAction(Intent.ACTION_GET_CONTENT);
                    gallery_launcher.launch(Intent.createChooser(i, "Select Image"));

                }
            }
        });

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//            if (requestCode == 121){
//                display_image.setImageURI(data.getData());
//
//                FirebaseVisionImage image;
//                try {
//                    image = FirebaseVisionImage.fromFilePath(getApplicationContext(), data.getData());
//
//                    FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
//                            .getOnDeviceTextRecognizer();
//
//                    Task<FirebaseVisionText> result =
//                            detector.processImage(image)
//                                    .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
//                                        @Override
//                                        public void onSuccess(FirebaseVisionText firebaseVisionText) {
//
//                                            copy_button.setEnabled(true);
//                                            share_button.setEnabled(true);
//                                            clear_button.setEnabled(true);
//
//
//                                            String output_text = firebaseVisionText.getText().toString();
//                                            recognized_text.setText(output_text);
//
//                                            copy_button.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//
//                                                    v.startAnimation(button_anim);
//
//                                                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//                                                    ClipData clipData = ClipData.newPlainText("Data", output_text);
//                                                    clipboardManager.setPrimaryClip(clipData);
//                                                    Toast.makeText(text_recognize_activity.this, "Copied", Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//
//                                            share_button.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//
//                                                    v.startAnimation(button_anim);
//
//                                                    Intent sendIntent = new Intent();
//                                                    sendIntent.setAction(Intent.ACTION_SEND);
//                                                    sendIntent.putExtra(Intent.EXTRA_TEXT, output_text);
//                                                    sendIntent.setType("text/plain");
//                                                    startActivity(sendIntent);
//
//                                                }
//                                            });
//
//                                            clear_button.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//
//                                                    v.startAnimation(button_anim);
//
//                                                    recognized_text.setText("");
//                                                }
//                                            });
//                                        }
//                                    })
//                                    .addOnFailureListener(
//                                            new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(@NonNull Exception e) {
//
//                                                    Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_SHORT).show();
//
//                                                }
//                                            });
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    private void showSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage("This app needs permission to recognize texts. Please enable it in the app settings.")
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