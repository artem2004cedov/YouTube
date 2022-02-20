package com.example.yot.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.yot.Fragment.ChannelDashboardFragment;
import com.example.yot.Fragment.HomeFragment;
import com.example.yot.Fragment.LibraryFragment;
import com.example.yot.Fragment.SearchFragment;
import com.example.yot.Fragment.ShortsFragment;
import com.example.yot.Fragment.SubscriptionsFragment;
import com.example.yot.R;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.IntentPickerSheetView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private LinearLayout linearHome, linearShorts, linearAdd, linearSubscribe, linearLibrary;
    private CircleImageView profileMain;
    private ImageView searchMain, notificationMain;
    private FrameLayout frameLayoutMain;
    private Fragment fragment;
    private AppBarLayout Appbar;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private Uri videoUri;
    private GoogleSignInClient mGoogleSingInClient;
    private BottomSheetBehavior bottomSheetBehavior;

    private static final int RC_SING_IN = 100;
    private static final int PERMISSION = 101;
    private static final int PICK_VIDEO = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        bottomMenu();

        profileMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    startActivity(new Intent(getApplicationContext(), AccountActivity.class));
                } else {
                    showDialog();
                }
            }
        });
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);

        ViewGroup viewGroup = findViewById(R.id.content);
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_singnin_dilogue, viewGroup, false);

        builder.setView(view);

        TextView singInText = view.findViewById(R.id.singInText);
        singInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singIn();
            }
        });

        builder.create().show();


    }

    private void singIn() {
        Intent intent = mGoogleSingInClient.getSignInIntent();
        startActivityForResult(intent, RC_SING_IN);
    }

    private void bottomMenu() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutMain, new HomeFragment()).commit();

        linearHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Appbar.setVisibility(View.VISIBLE);
                setStatusBarColor("#FFFFFF");
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutMain, new HomeFragment()).commit();
            }
        });

//        if (Build.VERSION.SDK_INT >= 21) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//        }

//                RelativeLayout bottomSheetLayout = findViewById(R.id.relativeLay);
//        LinearLayout linearLL = findViewById(R.id.linearLL);
//        bottomSheetBehavior = BottomSheetBehavior.from(linearLL);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//
//
//        bottomSheetBehavior.setPeekHeight(200);
//        bottomSheetBehavior.setHideable(false);
//
//        bottomSheetBehavior.setPeekHeight(200);
//        bottomSheetBehavior.setHideable(false);

        linearShorts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Appbar.setVisibility(View.VISIBLE);
                setStatusBarColor("#FFFFFF");
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutMain, new ShortsFragment()).commit();

//                bottomSheetBehavior.setPeekHeight(200);
//                bottomSheetBehavior.setHideable(false);

//                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutMain, new ShortsFragment()).commit();
//                if (Build.VERSION.SDK_INT >= 21) {
//                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//                    Window window = getWindow();
//                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//                    window.setStatusBarColor(Color.TRANSPARENT);
//                }
//
////                RelativeLayout bottomSheetLayout = findViewById(R.id.relativeLay);
//                LinearLayout linearLL = findViewById(R.id.linearLL);
//                bottomSheetBehavior = BottomSheetBehavior.from(linearLL);
//                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//
//                bottomSheetBehavior.setPeekHeight(200);
//                bottomSheetBehavior.setHideable(false);

//
//                bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//                    @Override
//                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                        if (newState == BottomSheetBehavior.STATE_COLLAPSED)
//                        bottomSheetBehavior.setPeekHeight(0);
//                        bottomSheet.setVisibility(View.VISIBLE);
//                    }
//
//                    @Override
//                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//                        bottomSheet.setVisibility(View.VISIBLE);
//                    }
//                });

//                bottomSheetBehavior.setHideable(true);
//                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


            }
        });

        linearSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Appbar.setVisibility(View.VISIBLE);
                setStatusBarColor("#FFFFFF");
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutMain, new SubscriptionsFragment()).commit();
            }
        });

        searchMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Appbar.setVisibility(View.GONE);
                setStatusBarColor("#FFFFFF");
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutMain, new SearchFragment()).commit();
            }
        });

        showFragment();


        linearLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Appbar.setVisibility(View.VISIBLE);
                setStatusBarColor("#FFFFFF");
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutMain, new LibraryFragment()).commit();
            }
        });

        linearAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Appbar.setVisibility(View.VISIBLE);
                setStatusBarColor("#FFFFFF");
                if (user != null) {
                    showPublishConteDialogue();
                }
            }
        });
    }

    private void showPublishConteDialogue() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        bottomSheetDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setContentView(R.layout.add_video_dialog);

        LinearLayout linearAddVideo = bottomSheetDialog.findViewById(R.id.linearAddVideo);
        ImageView back = bottomSheetDialog.findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        linearAddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(Intent.createChooser(intent, "Select video"), PICK_VIDEO);
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }

    private void init() {
        Appbar = findViewById(R.id.bar);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSingInClient = GoogleSignIn.getClient(this, gso);

        setStatusBarColor("#FFFFFF");
        checkPermission();
        Appbar.setVisibility(View.VISIBLE);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        linearHome = findViewById(R.id.linearHome);
        linearShorts = findViewById(R.id.linearShorts);
        linearAdd = findViewById(R.id.linearAdd);
        linearSubscribe = findViewById(R.id.linearSubscribe);
        linearLibrary = findViewById(R.id.linearLibrary);
        profileMain = findViewById(R.id.profileMain);
        searchMain = findViewById(R.id.searchMain);
        notificationMain = findViewById(R.id.notificationMain);

        if (user != null) {
            linearAdd.setVisibility(View.VISIBLE);
            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String profile = snapshot.child("profile").getValue().toString();
                        Glide.with(getApplication())
                                .load(profile)
                                .into(profileMain);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            linearAdd.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_SING_IN:
//                Ошибка
                if (requestCode == RC_SING_IN /*&& data != null*/) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("username", account.getDisplayName());
                                    hashMap.put("email", account.getEmail());
                                    hashMap.put("profile", String.valueOf(account.getPhotoUrl()));
                                    hashMap.put("uid", firebaseUser.getUid());
                                    hashMap.put("search", account.getDisplayName().toLowerCase());

                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().
                                            child("Users");
                                    databaseReference.child(firebaseUser.getUid()).setValue(hashMap);
                                    Toast.makeText(MainActivity.this, "Успешный вход", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(MainActivity.this, AccountActivity.class));
                                } else {
                                    Toast.makeText(MainActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (Exception e) {

                    }
                }
            case PICK_VIDEO:
                if (resultCode == RESULT_OK && data != null) {
                    videoUri = data.getData();
                    Intent intent = new Intent(MainActivity.this, PublishContentActivity.class);
                    intent.putExtra("type", "video");
                    intent.setData(videoUri);
                    startActivity(intent);
                }
        }
    }

    private void showFragment() {
        String type = getIntent().getStringExtra("type");
        if (type != null) {
            switch (type) {
                case "channel":
                    Appbar.setVisibility(View.GONE);
                    setStatusBarColor("#99FF0000");
                    fragment = ChannelDashboardFragment.newInstance();
                    break;

            }
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frameLayoutMain, fragment).commit();
        }
    }

    private void setStatusBarColor(String color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor(color));
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION);
        } else {
        }

    }
}
