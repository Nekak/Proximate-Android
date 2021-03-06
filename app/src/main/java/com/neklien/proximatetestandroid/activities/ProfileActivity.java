package com.neklien.proximatetestandroid.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.neklien.proximatetestandroid.AppDelegate;
import com.neklien.proximatetestandroid.R;
import com.neklien.proximatetestandroid.helpers.GenericFileProvider;
import com.neklien.proximatetestandroid.helpers.database.DBElement;
import com.neklien.proximatetestandroid.helpers.database.DBQueryManager;
import com.neklien.proximatetestandroid.helpers.database.DBQueryManagerListener;
import com.neklien.proximatetestandroid.helpers.database.Section;
import com.neklien.proximatetestandroid.helpers.database.User;
import com.neklien.proximatetestandroid.helpers.database.UserSection;
import com.neklien.proximatetestandroid.helpers.retrofit.RestApi;
import com.neklien.proximatetestandroid.helpers.retrofit.ServerResponse;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private ImageButton imageButton;

    private TextView tvImage;
    private TextView tvProfile;

    private ProgressBar progressBar;

    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    private Location currentLocation;

    private Uri imageUri;

    private User user;

    private Call<ServerResponse> call;

    private AlertDialog alertDialog;

    private Boolean isAttached = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        progressBar = findViewById(R.id.pb_ap);

        imageButton = findViewById(R.id.ib_ap);
        tvImage = findViewById(R.id.tv_ap);
        tvProfile = findViewById(R.id.tv_profile_ap);

        mLocationRequest = LocationRequest.create();

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(60000);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        user = DBQueryManager.getUser();

        if (savedInstanceState != null) {
            imageUri = savedInstanceState.getParcelable("ImageUri");
            currentLocation = savedInstanceState.getParcelable("CurrentLocation");
        }

        if (user == null) {
            downloadProfile();
        } else {
            showUserData();
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttached = true;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttached = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("ImageUri", imageUri);
        outState.putParcelable("CurrentLocation", currentLocation);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        imageUri = savedInstanceState.getParcelable("ImageUri");
        currentLocation = savedInstanceState.getParcelable("CurrentLocation");
    }

    private void downloadProfile() {
        showActivityIndicator();

        final RestApi restApi = ((AppDelegate) getApplication()).getRestApi();

        SharedPreferences sharedPref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        String token = sharedPref.getString("token_preferences", "");

        call = restApi.getProfile(token);

        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                hideActivityIndicator();

                if (response.isSuccessful()) {
                    if (response.body().getError()) {
                        showAlertMessage(response.body().getMessage());
                    } else {
                        if (response.body().getArrUsers().size() > 0) {
                            final User auxUser = response.body().getArrUsers().get(0);

                            DBQueryManager.saveUser(ProfileActivity.this, auxUser, new DBQueryManagerListener() {
                                @Override
                                public void onQueryResult(long resultCode, DBElement dbElement) {
                                    saveSections(auxUser);
                                }
                            });
                        } else {
                            showAlertMessage("No se encontró información para este usuario.");
                        }
                    }
                } else {
                    showAlertMessage(response.message());
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                if (!call.isCanceled()) {
                    hideActivityIndicator();
                    showAlertMessage("Ocurrió un error, por favor reintente más tarde.");
                }
            }
        });
    }

    private void saveSections(final User auxUser) {
        for (final Section s : auxUser.getArrSections()) {
            DBQueryManager.saveSection(this, s, new DBQueryManagerListener() {
                @Override
                public void onQueryResult(long resultCode, DBElement dbElement) {
                    long idUS = DBQueryManager.findIdForUserSection(auxUser.getIdUser(), s.getIdSection());

                    if (idUS == -1) {
                        idUS = DBQueryManager.getLastUserSectionIndex() + 1;
                    }

                    UserSection userSection = new UserSection();

                    userSection.setIdUserSection(idUS);
                    userSection.setIdUser(auxUser.getIdUser());
                    userSection.setIdSection(s.getIdSection());

                    DBQueryManager.saveUserSection(ProfileActivity.this, userSection, new DBQueryManagerListener() {
                        @Override
                        public void onQueryResult(long resultCode, DBElement dbElement) {

                        }
                    });
                }
            });
        }

        user = DBQueryManager.getUser();

        if (user != null) {
            showUserData();
        } else {
            Toast.makeText(ProfileActivity.this, "Ocurrió un error al obtener el usuario.", Toast.LENGTH_LONG).show();
        }
    }

    private void showUserData() {
        if (!user.getPathPicture().equals("")) {
            showImage();
        } else {
            tvImage.setText("Para tomar una fotografía toque el botón de cámara.");
        }

        SpannableStringBuilder stringFinal = new SpannableStringBuilder();

        SpannableStringBuilder stringName = new SpannableStringBuilder(user.getNames() + " " + user.getLastnames());

        stringName.setSpan(new RelativeSizeSpan(2f), 0, stringName.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        stringName.setSpan(new StyleSpan(Typeface.BOLD), 0, stringName.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        stringName.append("\n\n");

        stringFinal.append(stringName);

        SpannableStringBuilder stringEmail = new SpannableStringBuilder(String.valueOf(user.getEmail()));

        stringEmail.setSpan(new RelativeSizeSpan(0.85f), 0, stringEmail.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        stringEmail.setSpan(new StyleSpan(Typeface.ITALIC), 0, stringEmail.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        stringEmail.append("\n\n");

        stringFinal.append(stringEmail);

        /*
        * let formatter = DateFormatter()
            formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"

            if let dateLL = formatter.date(from: userData!.lastLogin){
                let formatterLL = DateFormatter()
                formatterLL.dateFormat = "dd-MM-yyyy 'a las' HH:mm:ss"
                formatterLL.timeZone = NSTimeZone(name: "UTC")! as TimeZone
                let dateStr = formatterLL.string(from: dateLL)
                */

        String strLL = "";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);
            TemporalAccessor date = fmt.parse(user.getLastLogin());
            Instant time = Instant.from(date);

            DateTimeFormatter fmtOut = DateTimeFormatter.ofPattern("dd-MM-yyyy 'a las' HH:mm:ss").withZone(ZoneOffset.UTC);
            strLL = fmtOut.format(time);
        } else {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

            try {
                Date date = fmt.parse(user.getLastLogin());

                SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy 'a las' HH:mm:ss");
                strLL = fmtOut.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        SpannableStringBuilder stringLL = new SpannableStringBuilder("Última inicio de sesión: " + strLL);

        stringLL.setSpan(new RelativeSizeSpan(0.9f), 0, stringLL.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        stringLL.setSpan(new ForegroundColorSpan(Color.GRAY), 0, stringLL.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        stringLL.append("\n\n");

        stringFinal.append(stringLL);

        SpannableStringBuilder stringSections = new SpannableStringBuilder("Secciones:\n\n");

        stringSections.setSpan(new RelativeSizeSpan(0.8f), 0, stringSections.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        stringFinal.append(stringSections);

        for (Section s : user.getArrSections()) {
            stringFinal.append(s.getSectionName());
            stringFinal.append("\n\n");
        }

        tvProfile.setText(stringFinal);
    }

    private void showImage() {
        try {
            final String PROXIMATE_FILE_AUTHORITY = String.format(Locale.getDefault(), "%s.provider", "com.neklien.proximatetestandroid");
            final String PROXIMATE_DIR = String.format(Locale.getDefault(), "%s/%s", Environment.DIRECTORY_PICTURES, "Proximate");

            File path = Environment.getExternalStoragePublicDirectory(PROXIMATE_DIR);

            if (!path.exists()) {
                boolean result = path.mkdirs();
                if (!result) {
                    throw new IllegalStateException("can't create dir " + path.toString());
                }
            }

            File photo = new File(path, user.getPathPicture());

            Uri uriAux = GenericFileProvider.getUriForFile(this, PROXIMATE_FILE_AUTHORITY, photo);

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriAux);
            imageButton.setImageBitmap(bitmap);

            Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(this.user.getLatitude(), this.user.getLongitude(), 2);
            if (addresses.isEmpty()) {
                tvImage.setText("Fotografía tomada en las coordenadas: " + user.getLatitude() + "," + user.getLongitude());
            } else {
                if (addresses.size() > 0) {
                    String address = "";
                    for (int i = 0; i <= addresses.get(0).getMaxAddressLineIndex(); i++) {
                        address += addresses.get(0).getAddressLine(i) + " ";
                    }

                    tvImage.setText("Fotografía tomada en: " + address);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();

            tvImage.setText("Fotografía tomada en las coordenadas: " + user.getLatitude() + "," + user.getLongitude());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (call != null && !call.isCanceled()) {
            call.cancel();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    public void takePhoto(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            try {
                ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Active los permisos de geolocalización.", Toast.LENGTH_LONG).show();
                return;
            }

            return;
        }

        initRequestLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initRequestLocation();
            } else {
                Toast.makeText(this, "Active los permisos de geolocalización.", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == 1001) {
            if (grantResults.length > 0) {
                boolean granted = false;

                for (int grantResult : grantResults) {
                    granted = grantResult == PackageManager.PERMISSION_GRANTED;

                    if (!granted) {
                        break;
                    }
                }

                if (granted) {
                    startCamera();
                } else {
                    Toast.makeText(this, "Active los permisos de la cámara y almacenamiento.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Active los permisos de la cámara y almacenamiento.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void initRequestLocation() {
        if (locationCallback == null) {
            locationCallback = new LocationCallback() {

                @Override
                public void onLocationResult(LocationResult locationResult) {
                    currentLocation = locationResult.getLastLocation();

                    if (fusedLocationProviderClient != null && locationCallback != null)
                        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

                    openCamera();
                }

                @Override
                public void onLocationAvailability(LocationAvailability locationAvailability) {

                    super.onLocationAvailability(locationAvailability);

                    if (locationAvailability != null && locationAvailability.isLocationAvailable()) {
                        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(ProfileActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(final Location location) {
                                if (fusedLocationProviderClient != null && locationCallback != null)
                                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (location != null) {
                                            currentLocation = location;

                                            openCamera();
                                        } else {
                                            Toast.makeText(ProfileActivity.this, "No pudo encontrarse su ubicación.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        }).addOnFailureListener(ProfileActivity.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull final Exception e) {
                                if (fusedLocationProviderClient != null && locationCallback != null)
                                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ProfileActivity.this, "No pudo encontrarse su ubicación.", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
                    } else {
                        if (fusedLocationProviderClient != null && locationCallback != null)
                            fusedLocationProviderClient.removeLocationUpdates(locationCallback);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ProfileActivity.this, "No pudo encontrarse su ubicación.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            };
        }

        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback, null);
    }

    private void openCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            try {
                ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Active los permisos de la cámara y almacenamiento.", Toast.LENGTH_LONG).show();
                return;
            }

            return;
        }

        startCamera();
    }

    private void startCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        final String PROXIMATE_FILE_AUTHORITY = String.format(Locale.getDefault(), "%s.provider", "com.neklien.proximatetestandroid");
        final String PROXIMATE_DIR = String.format(Locale.getDefault(), "%s/%s", Environment.DIRECTORY_PICTURES, "Proximate");

        File path = Environment.getExternalStoragePublicDirectory(PROXIMATE_DIR);

        if (!path.exists()) {
            boolean result = path.mkdirs();
            if (!result) {
                throw new IllegalStateException("can't create dir " + path.toString());
            }
        }

        String imgName = UUID.randomUUID().toString() + ".png";
        File photo = new File(path, imgName);

        imageUri = GenericFileProvider.getUriForFile(this, PROXIMATE_FILE_AUTHORITY, photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    user.setPathPicture(imageUri.getLastPathSegment());
                    user.setLatitude((float) currentLocation.getLatitude());
                    user.setLongitude((float) currentLocation.getLongitude());

                    DBQueryManager.saveUser(ProfileActivity.this, user, new DBQueryManagerListener() {
                        @Override
                        public void onQueryResult(long resultCode, DBElement dbElement) {
                            showImage();
                        }
                    });
                }
                break;
        }
    }

    public void closeSession(View view) {
        if (alertDialog == null || (alertDialog != null && !alertDialog.isShowing())) {
            AlertDialog.Builder builderDialog = new AlertDialog.Builder(this)
                    .setTitle("Aviso")
                    .setMessage("¿Cerrar sesión?")
                    .setCancelable(false)
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedPreferences sharedPref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();

                            editor.putString("email_preferences", "");
                            editor.putString("password_preferences", "");
                            editor.putString("token_preferences", "");

                            editor.clear();
                            editor.apply();

                            DBQueryManager.deleteUser(new DBQueryManagerListener() {
                                @Override
                                public void onQueryResult(long resultCode, DBElement dbElement) {
                                    DBQueryManager.deleteSection(new DBQueryManagerListener() {
                                        @Override
                                        public void onQueryResult(long resultCode, DBElement dbElement) {
                                            DBQueryManager.deleteUserSection(new DBQueryManagerListener() {
                                                @Override
                                                public void onQueryResult(long resultCode, DBElement dbElement) {
                                                    Intent intent = new Intent(ProfileActivity.this, FirstActivity.class);

                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                        }
                                    });
                                }
                            });

                            final String PROXIMATE_DIR = String.format(Locale.getDefault(), "%s/%s", Environment.DIRECTORY_PICTURES, "Proximate");

                            File path = Environment.getExternalStoragePublicDirectory(PROXIMATE_DIR);

                            if (!path.exists()) {
                                boolean result = path.mkdirs();
                                if (!result) {
                                    throw new IllegalStateException("can't create dir " + path.toString());
                                }
                            }

                            File[] files = path.listFiles();

                            if (files != null) {
                                for (File f : files) {
                                    f.delete();
                                }
                            }
                        }
                    });

            if (isAttached) {
                alertDialog = builderDialog.show();
            }
        }
    }

    private void showActivityIndicator() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideActivityIndicator() {
        progressBar.setVisibility(View.INVISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void showAlertMessage(String message) {
        if (alertDialog == null || (alertDialog != null && !alertDialog.isShowing())) {
            AlertDialog.Builder builderDialog = new AlertDialog.Builder(this)
                    .setTitle("Aviso")
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

            if (isAttached) {
                alertDialog = builderDialog.show();
            }
        }
    }
}
