 package com.devsyncit.qrscan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;

 public class MainActivity extends AppCompatActivity {
    CardView barcode_scan, image_to_text_body;
    ImageButton generate_code, image_to_text;
    DrawerLayout drawer;
    MaterialToolbar toolbar;
    NavigationView drawer_nav;
    LinearLayout banner_ad;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        generate_code = findViewById(R.id.generate_code);
        barcode_scan = findViewById(R.id.barcode_scan);
        image_to_text_body = findViewById(R.id.image_to_text_body);
        image_to_text = findViewById(R.id.image_to_text);
        drawer = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        drawer_nav = findViewById(R.id.drawer_nav);

        banner_ad = findViewById(R.id.banner_ad);

        Animation slide_in = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_in);
        Animation slide_out = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_out);


        //ad section

        new Thread(
                () -> {
                    // Initialize the Google Mobile Ads SDK on a background thread.
                    MobileAds.initialize(this, initializationStatus -> {});
                })
                .start();

        //=======================================

//        AdView adView = new AdView(this);
//        adView.setAdUnitId("ca-app-pub-6538546097765410/4579893970");
//        adView.setAdSize(AdSize.BANNER);
//
//        banner_ad.removeAllViews();
//        banner_ad.addView(adView);
//
//        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
//        adView.loadAd(adRequest);

//drawer listener
        
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawer, toolbar, R.string.close_drawer, R.string.open_drawer);

        drawer.addDrawerListener(toggle);

        
        //drawer navigation control

        drawer_nav.setItemIconTintList(null);
        
        drawer_nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                
                if (item.getItemId()==R.id.generate_qr_code) {

                    startActivity(new Intent(MainActivity.this, generate_qr.class));

                    drawer.closeDrawer(GravityCompat.START);

                } else if (item.getItemId()==R.id.barcode_scan) {
                    
                    startActivity(new Intent(MainActivity.this, barcode_activity.class));
                    drawer.closeDrawer(GravityCompat.START);
                    
                } else if (item.getItemId()==R.id.image_to_text_convert) {
                    
                    startActivity(new Intent(MainActivity.this, text_recognize_activity.class));
                    drawer.closeDrawer(GravityCompat.START);

                } else if (item.getItemId()==R.id.privacy_policy) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/privacy-policy-qrscan"));
                    startActivity(browserIntent);
                    drawer.closeDrawer(GravityCompat.START);

                } else if (item.getItemId()==R.id.rating) {

                    rateApp(MainActivity.this);
                    drawer.closeDrawer(GravityCompat.START);

                } else if (item.getItemId()==R.id.share_app) {

                    shareApp(MainActivity.this);
                    drawer.closeDrawer(GravityCompat.START);

                }

                return true;
            }
        });
        

        //document scan

        generate_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, generate_qr.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        //==================================


        //barcode scan

        barcode_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, barcode_activity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });


        //text recognize

        image_to_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, text_recognize_activity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        //===================================



    }

//     GmsDocumentScannerOptions options = new GmsDocumentScannerOptions.Builder()
//             .setGalleryImportAllowed(true)
//             .setPageLimit(2)
//             .setResultFormats(RESULT_FORMAT_JPEG, RESULT_FORMAT_PDF)
//             .setScannerMode(SCANNER_MODE_FULL)
//             .build();
//
//     GmsDocumentScanner scanner = GmsDocumentScanning.getClient(options);
//
//     ActivityResultLauncher<IntentSenderRequest> scannerLauncher =
//             registerForActivityResult(
//                     new ActivityResultContracts.StartIntentSenderForResult(),
//                     result -> {
//
//                         if (result.getResultCode() == RESULT_OK) {
//                             GmsDocumentScanningResult scan_result = GmsDocumentScanningResult.fromActivityResultIntent(result.getData());
//                             for (GmsDocumentScanningResult.Page pages : scan_result.getPages()) {
//                                 Uri imageUri = pages.getImageUri();
//                                    document_scan.image_uri = imageUri;
//
//                                 document_scan.page_count++;
//                             }
//
//                             GmsDocumentScanningResult.Pdf pdf = scan_result.getPdf();
//                             Uri pdfUri = pdf.getUri();
//                             int pageCount = pdf.getPageCount();
//                         }
//                     });


     //this method is for share app feature

     public void shareApp(Context context){
         final String appPackageName = context.getPackageName();
         try {
             Intent sendIntent = new Intent();
             sendIntent.setAction(Intent.ACTION_SEND);
             sendIntent.putExtra(Intent.EXTRA_TEXT, "Download : https://play.google.com/store/apps/details?id=" + appPackageName);
             sendIntent.setType("text/plain");
             context.startActivity(sendIntent);
         }catch (Exception e){
             Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_SHORT).show();
         }
     }

     //this method is for rate app feature

     public void rateApp(Context context){
         try {
             Intent rateIntent = new Intent(Intent.ACTION_VIEW);
             rateIntent.setData(Uri.parse("https://play.google.com/store/apps/details?id="+context.getPackageName()));
             rateIntent.setPackage("com.android.vending");
             startActivity(rateIntent);
         }catch (Exception e){
             Toast.makeText(getApplicationContext(),""+e, Toast.LENGTH_SHORT).show();
         }
     }


     @SuppressLint("MissingSuperCall")
     @Override
     public void onBackPressed() {
//         super.onBackPressed();

         LayoutInflater inflater = getLayoutInflater();
         View exit_view = inflater.inflate(R.layout.exit_dialog, null);

         AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);


         MaterialButton yes = exit_view.findViewById(R.id.yes);
         MaterialButton no = exit_view.findViewById(R.id.no);

         builder.setView(exit_view);

         AlertDialog dialog = builder.create();

         yes.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 dialog.dismiss();
                 finish();
             }
         });

         no.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 dialog.dismiss();
             }
         });

         dialog.setCancelable(false);

         dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

         WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
         layoutParams.dimAmount = 0.6f;  // Adjust this value to increase or decrease the dim
         dialog.getWindow().setAttributes(layoutParams);

         dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

         dialog.show();

     }
 }