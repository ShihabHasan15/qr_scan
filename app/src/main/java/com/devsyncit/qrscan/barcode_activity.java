package com.devsyncit.qrscan;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.util.Linkify;
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

import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.io.IOException;
import java.util.List;

public class barcode_activity extends AppCompatActivity {
    ImageView back_arrow, img_result;
    Button button1, button2, button3;
    AppCompatButton camera, gallery;
    LinearLayout first_line, second_line;
    public static final int CAMERA_REQUEST_CODE = 1;
    public static final int READ_STORAGE_CODE = 2;
    public static final int WRITE_STORAGE_CODE = 3;
    ActivityResultLauncher<Intent> camera_launcher;
    ActivityResultLauncher<Intent> gallery_launcher;
    InputImage image;
    BarcodeScannerOptions options;
    BarcodeScanner scanner;
    Animation button_anim;
    TextView first_result, second_result, first_result_type, second_result_type, first_colon;
    LinearLayout banner_ad;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        back_arrow = findViewById(R.id.back_arrow);

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        camera = findViewById(R.id.camera);
        gallery = findViewById(R.id.gallery);
        img_result = findViewById(R.id.img_result);
        first_result = findViewById(R.id.first_result);
        second_result = findViewById(R.id.second_result);
        first_result_type = findViewById(R.id.first_title_type);
        second_result_type = findViewById(R.id.second_title_type);
        first_colon = findViewById(R.id.first_colon);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
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


        button_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_anim);

        first_line = findViewById(R.id.first_line);
        second_line = findViewById(R.id.second_line);


        options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                Barcode.FORMAT_QR_CODE,
                                Barcode.FORMAT_AZTEC)
                        .enableAllPotentialBarcodes()
                        .build();

//
//
        scanner = BarcodeScanning.getClient(options);




        gallery_launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if (result.getResultCode() == Activity.RESULT_OK){
                            Uri image_uri = result.getData().getData();
                            img_result.setImageURI(image_uri);
                            try {
                                image = InputImage.fromFilePath(barcode_activity.this, image_uri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            process_image();
                        }
                    }
                }
        );


        camera_launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if (result.getResultCode() == Activity.RESULT_OK){

                            Bundle bundle = result.getData().getExtras();
                            Bitmap bitmap = (Bitmap) bundle.get("data");
                            img_result.setImageBitmap(bitmap);

                            image = InputImage.fromBitmap(bitmap, 0);

                            process_image();

                        }

                    }
                }
        );


        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                v.startAnimation(button_anim);

                if (ContextCompat.checkSelfPermission(barcode_activity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(barcode_activity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // Permission was denied but "Don't ask again" was not selected
                        // Request the permission again
                        ActivityCompat.requestPermissions(barcode_activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                    } else {
                        // Permission was denied and "Don't ask again" was selected
                        // Show an explanation dialog
                        showSettingsDialog();
                    }

                }else{

                    Intent gallery_intent = new Intent();
                    gallery_intent.setType("image/*");
                    gallery_intent.setAction(Intent.ACTION_GET_CONTENT);
                    gallery_launcher.launch(gallery_intent);

                }



            }
        });


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                v.startAnimation(button_anim);


                if (checkPermission(Manifest.permission.CAMERA, CAMERA_REQUEST_CODE)==false) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(barcode_activity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // Permission was denied but "Don't ask again" was not selected
                        // Request the permission again
                        checkPermission(Manifest.permission.CAMERA, CAMERA_REQUEST_CODE);
                    } else {
                        // Permission was denied and "Don't ask again" was selected
                        // Show an explanation dialog
                        showSettingsDialog();
                    }

                }else{

                    Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    camera_launcher.launch(camera_intent);

                }



            }
        });



    }

    private void process_image() {

        Task<List<Barcode>> result = scanner.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {

                        button1.setEnabled(true);
                        button2.setEnabled(true);
                        button3.setEnabled(true);

                        for (Barcode barcode: barcodes) {

                            String rawValue = barcode.getRawValue();

                            int valueType = barcode.getValueType();
                            // See API reference for complete list of supported types

                            if (barcode.getValueType()==Barcode.TYPE_TEXT || barcode.getValueType()==Barcode.TYPE_EMAIL ||
                                    barcode.getValueType()==Barcode.TYPE_URL || barcode.getValueType()==Barcode.TYPE_WIFI) {

                                switch (valueType) {

                                    case Barcode.TYPE_WIFI:
                                        String ssid = barcode.getWifi().getSsid();
                                        String password = barcode.getWifi().getPassword();
                                        int type = barcode.getWifi().getEncryptionType();

                                        first_line.setVisibility(View.VISIBLE);
                                        second_line.setVisibility(View.VISIBLE);

                                        first_result_type.setText("SSID");
                                        first_result.setText(ssid);
                                        second_result.setText(password);

                                        button1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                v.startAnimation(button_anim);

                                                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                                ClipData clipData = ClipData.newPlainText("Data", password);
                                                clipboardManager.setPrimaryClip(clipData);
                                                Toast.makeText(getApplicationContext(), "Password Copied", Toast.LENGTH_SHORT).show();

                                            }
                                        });

                                        button2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                v.startAnimation(button_anim);

                                                Intent sendIntent = new Intent();
                                                sendIntent.setAction(Intent.ACTION_SEND);
                                                sendIntent.putExtra(Intent.EXTRA_TEXT, password);
                                                sendIntent.setType("text/plain");
                                                startActivity(sendIntent);

                                            }
                                        });

                                        button3.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                v.startAnimation(button_anim);

                                            }
                                        });

                                        break;
                                    case Barcode.TYPE_URL:

                                        String url = barcode.getUrl().getUrl();

                                        first_line.setVisibility(View.GONE);

                                        second_line.setVisibility(View.VISIBLE);

                                        second_result_type.setText("Url");
                                        second_result.setText(url);
                                        Linkify.addLinks(second_result, Linkify.WEB_URLS);
                                        second_result.setLinkTextColor(Color.DKGRAY);

                                        button3.setText("Go");

                                        button1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                v.startAnimation(button_anim);

                                                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                                ClipData clipData = ClipData.newPlainText("Data", url);
                                                clipboardManager.setPrimaryClip(clipData);
                                                Toast.makeText(getApplicationContext(), "URL Copied", Toast.LENGTH_SHORT).show();

                                            }
                                        });

                                        button2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                v.startAnimation(button_anim);

                                                Intent sendIntent = new Intent();
                                                sendIntent.setAction(Intent.ACTION_SEND);
                                                sendIntent.putExtra(Intent.EXTRA_TEXT, url);
                                                sendIntent.setType("text/plain");
                                                startActivity(sendIntent);

                                            }
                                        });

                                        button3.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                v.startAnimation(button_anim);

                                                try {
                                                    Uri uri = Uri.parse(url);
                                                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        });

                                        break;
                                    case Barcode.TYPE_EMAIL:

                                        Barcode.Email email = barcode.getEmail();

                                        String address = email.getAddress();
                                        String body = email.getBody();
                                        String subject = email.getSubject();

                                        second_line.setVisibility(View.GONE);

                                        first_line.setVisibility(View.VISIBLE);
                                        first_result_type.setText("Email");
                                        first_result.setText(address);

                                        Linkify.addLinks(first_result, Linkify.EMAIL_ADDRESSES);
                                        first_result.setLinkTextColor(Color.DKGRAY);

                                        button1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                v.startAnimation(button_anim);

                                                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                                ClipData clipData = ClipData.newPlainText("Data", address);
                                                clipboardManager.setPrimaryClip(clipData);
                                                Toast.makeText(getApplicationContext(), "Mail Address Copied", Toast.LENGTH_SHORT).show();

                                            }
                                        });

                                        button2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                v.startAnimation(button_anim);

                                                Intent sendIntent = new Intent();
                                                sendIntent.setAction(Intent.ACTION_SEND);
                                                sendIntent.putExtra(Intent.EXTRA_TEXT, address);
                                                sendIntent.setType("text/plain");
                                                startActivity(sendIntent);

                                            }
                                        });

                                        button3.setText("Mail");

                                        button3.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                v.startAnimation(button_anim);

                                                String[] targetEmail = {"" + address};

                                                Intent intent = new Intent(Intent.ACTION_SEND);
                                                intent.setData(Uri.parse("mailto:"));
                                                intent.setType("text/plain");
                                                intent.putExtra(Intent.EXTRA_EMAIL, targetEmail);
                                                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                                                intent.putExtra(Intent.EXTRA_TEXT, body);

                                                try {
                                                    startActivity(Intent.createChooser(intent, "Send Email....."));
                                                } catch (Exception e) {
                                                    Toast.makeText(barcode_activity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });

                                        break;

                                    case Barcode.TYPE_CONTACT_INFO:

                                        Barcode.ContactInfo contactInfo = barcode.getContactInfo();

                                        String title = contactInfo.getTitle();
                                        String organizer = contactInfo.getOrganization();
                                        String name = contactInfo.getName().getFirst() + " " + contactInfo.getName().getLast();
                                        String phone_number = barcode.getPhone().getNumber();

                                        first_line.setVisibility(View.VISIBLE);
                                        second_line.setVisibility(View.VISIBLE);

                                        first_result_type.setText("Contact Name");
                                        first_result.setText(name);

                                        second_result_type.setText("Phone");
                                        second_result.setText(phone_number);


                                        button1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                v.startAnimation(button_anim);

                                                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                                ClipData clipData = ClipData.newPlainText("Data", phone_number);
                                                clipboardManager.setPrimaryClip(clipData);
                                                Toast.makeText(getApplicationContext(), "Phone Number Copied", Toast.LENGTH_SHORT).show();

                                            }
                                        });

                                        button2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                v.startAnimation(button_anim);

                                                Intent sendIntent = new Intent();
                                                sendIntent.setAction(Intent.ACTION_SEND);
                                                sendIntent.putExtra(Intent.EXTRA_TEXT, phone_number);
                                                sendIntent.setType("text/plain");
                                                startActivity(sendIntent);

                                            }
                                        });

                                        button3.setText("Call");

                                        button3.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                v.startAnimation(button_anim);

                                                if (ContextCompat.checkSelfPermission(barcode_activity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                    ActivityCompat.requestPermissions(barcode_activity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                                                } else {
                                                    Intent intent = new Intent(Intent.ACTION_CALL);
                                                    intent.setData(Uri.parse("tel: " + phone_number));
                                                    startActivity(intent);
                                                }
                                            }
                                        });

                                        break;
                                    case Barcode.TYPE_TEXT:

                                        String text = barcode.getDisplayValue();

                                        first_line.setVisibility(View.VISIBLE);
                                        first_colon.setVisibility(View.GONE);
                                        first_result.setText(text);

                                        button1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                v.startAnimation(button_anim);

                                                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                                ClipData clipData = ClipData.newPlainText("Data", text);
                                                clipboardManager.setPrimaryClip(clipData);
                                                Toast.makeText(getApplicationContext(), "Text Copied", Toast.LENGTH_SHORT).show();

                                            }
                                        });

                                        button2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                v.startAnimation(button_anim);

                                                Intent sendIntent = new Intent();
                                                sendIntent.setAction(Intent.ACTION_SEND);
                                                sendIntent.putExtra(Intent.EXTRA_TEXT, text);
                                                sendIntent.setType("text/plain");
                                                startActivity(sendIntent);

                                            }
                                        });

                                        button3.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                v.startAnimation(button_anim);

                                            }
                                        });

                                        break;

                                    default:

                                        second_line.setVisibility(View.GONE);

                                        button1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                v.startAnimation(button_anim);

                                                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                                ClipData clipData = ClipData.newPlainText("Data", rawValue);
                                                clipboardManager.setPrimaryClip(clipData);
                                                Toast.makeText(getApplicationContext(), "Copied", Toast.LENGTH_SHORT).show();

                                            }
                                        });

                                        button2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                v.startAnimation(button_anim);

                                                Intent sendIntent = new Intent();
                                                sendIntent.setAction(Intent.ACTION_SEND);
                                                sendIntent.putExtra(Intent.EXTRA_TEXT, rawValue);
                                                sendIntent.setType("text/plain");
                                                startActivity(sendIntent);

                                            }
                                        });

                                        button3.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                v.startAnimation(button_anim);

                                            }
                                        });

                                        first_line.setVisibility(View.VISIBLE);
                                        first_result.setText(rawValue);


                                }
                            }else {
                                first_line.setVisibility(View.VISIBLE);
                                first_result.setText("No QR Found");
                            }
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    first_result.setText("Nothing Detected");
                    Toast.makeText(barcode_activity.this, "Nothing Generated", Toast.LENGTH_LONG).show();

                    }
                });

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        checkPermission(Manifest.permission.CAMERA, CAMERA_REQUEST_CODE);
//
//    }



//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == CAMERA_REQUEST_CODE){
//            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
//                Toast.makeText(getApplicationContext(), "Camera Permission Denied", Toast.LENGTH_SHORT).show();
//            }else {
//                checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_STORAGE_CODE);
//            }
//        } else if (requestCode == READ_STORAGE_CODE) {
//            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
//                Toast.makeText(getApplicationContext(), "Storage Permission Denied", Toast.LENGTH_SHORT).show();
//            }else {
//                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_STORAGE_CODE);
//            }
//        } else if (requestCode == WRITE_STORAGE_CODE) {
//            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
//                Toast.makeText(getApplicationContext(), "Storage Permission Denied", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }


    public boolean checkPermission(String permission, int requestCode){

        if(ContextCompat.checkSelfPermission(barcode_activity.this, permission) == PackageManager.PERMISSION_DENIED){

            ActivityCompat.requestPermissions(barcode_activity.this, new String[]{permission}, requestCode);

            return false;
        }
        return true;
    }

    private void showSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage("This app needs permission to scan QR codes. Please enable it in the app settings.")
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