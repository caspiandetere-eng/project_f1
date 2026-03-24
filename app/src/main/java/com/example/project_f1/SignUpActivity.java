package com.example.project_f1;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project_f1.models.Driver;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {

    private static final int PROFILE_TYPE_DEFAULT = 0;
    private static final int PROFILE_TYPE_DRIVER  = 1;
    private static final int PROFILE_TYPE_GALLERY = 2;

    private ImageView ivProfilePic;
    private MaterialButton btnPickDefault, btnPickDriver, btnPickGallery;

    // Current selection state
    private int profileType = PROFILE_TYPE_DEFAULT;
    private int selectedDriverResId = 0;   // for driver pick
    private Uri galleryUri = null;          // for gallery pick

    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.applyTheme(this);
        setContentView(R.layout.activity_signup);
        ThemeManager.applyStatusBar(this);

        ivProfilePic    = findViewById(R.id.ivProfilePic);
        btnPickDefault  = findViewById(R.id.btnPickDefault);
        btnPickDriver   = findViewById(R.id.btnPickDriver);
        btnPickGallery  = findViewById(R.id.btnPickGallery);

        TextInputEditText etName     = findViewById(R.id.etName);
        TextInputEditText etEmail    = findViewById(R.id.etEmail);
        TextInputEditText etPassword = findViewById(R.id.etPassword);
        MaterialButton    btnContinue = findViewById(R.id.btnContinue);

        // Style avatar circle
        ivProfilePic.setBackground(new GradientDrawable() {{
            setShape(OVAL);
            setColor(0xFF1A1A1A);
            setStroke(dpToPx(2), 0xFFE10600);
        }});
        setAvatarDefault();

        // Gallery launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            galleryUri = uri;
                            profileType = PROFILE_TYPE_GALLERY;
                            try {
                                Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                                ivProfilePic.setImageBitmap(circleCrop(bmp));
                            } catch (IOException e) {
                                setAvatarDefault();
                            }
                            highlightButton(btnPickGallery);
                        }
                    }
                });

        btnPickDefault.setOnClickListener(v -> {
            profileType = PROFILE_TYPE_DEFAULT;
            selectedDriverResId = 0;
            galleryUri = null;
            setAvatarDefault();
            highlightButton(btnPickDefault);
        });

        btnPickDriver.setOnClickListener(v -> showDriverPicker());

        btnPickGallery.setOnClickListener(v -> {
            Intent pick = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(pick);
        });

        // Default is pre-selected
        highlightButton(btnPickDefault);

        btnContinue.setOnClickListener(v -> {
            String name     = etName.getText().toString().trim();
            String email    = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            long userId = UserRepository.register(this, name, email, password);
            if (userId == -1) {
                Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save profile pic
            String savedPicPath = saveProfilePic(userId);
            UserProfileRepository.save(this, profileType, selectedDriverResId, savedPicPath);

            getSharedPreferences("F1Prefs", MODE_PRIVATE).edit()
                    .putInt("user_id", (int) userId)
                    .putString("user_email", email)
                    .putString("user_name", name)
                    .apply();

            Intent intent = new Intent(this, KnowledgeLevelActivity.class);
            intent.putExtra("user_id", (int) userId);
            startActivity(intent);
        });
    }

    private void showDriverPicker() {
        BottomSheetDialog sheet = new BottomSheetDialog(this, R.style.BottomSheetTheme);
        View sheetView = LayoutInflater.from(this).inflate(R.layout.dialog_driver_picker, null);
        sheet.setContentView(sheetView);

        // Style the sheet background
        View container = (View) sheetView.getParent();
        if (container != null) container.setBackgroundColor(0x00000000);

        RecyclerView rv = sheetView.findViewById(R.id.rvDriverPicker);
        rv.setLayoutManager(new GridLayoutManager(this, 4));

        List<Driver> drivers = Driver.getAllDrivers();
        rv.setAdapter(new RecyclerView.Adapter<DriverAvatarHolder>() {
            @NonNull
            @Override
            public DriverAvatarHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_driver_avatar, parent, false);
                return new DriverAvatarHolder(v);
            }

            @Override
            public void onBindViewHolder(@NonNull DriverAvatarHolder h, int pos) {
                Driver d = drivers.get(pos);
                h.name.setText(d.lastName);
                if (d.photoResId != 0) h.photo.setImageResource(d.photoResId);

                // Clip image to oval background shape
                h.photo.setOutlineProvider(android.view.ViewOutlineProvider.BACKGROUND);
                h.photo.setClipToOutline(true);

                // Scale image to fill width, align to top so face is visible
                h.photo.post(() -> {
                    if (h.photo.getDrawable() == null) return;
                    int vw = h.photo.getWidth(), vh = h.photo.getHeight();
                    int dw = h.photo.getDrawable().getIntrinsicWidth();
                    int dh = h.photo.getDrawable().getIntrinsicHeight();
                    if (vw == 0 || dw == 0) return;
                    float scale = (float) vw / dw;
                    android.graphics.Matrix m = new android.graphics.Matrix();
                    m.setScale(scale, scale);
                    float dx = 0;
                    float dy = 0; // top-aligned
                    m.postTranslate(dx, dy);
                    h.photo.setScaleType(android.widget.ImageView.ScaleType.MATRIX);
                    h.photo.setImageMatrix(m);
                });

                boolean sel = (profileType == PROFILE_TYPE_DRIVER && selectedDriverResId == d.photoResId);
                GradientDrawable border = new GradientDrawable();
                border.setShape(GradientDrawable.OVAL);
                border.setColor(0xFF1A1A1A);
                border.setStroke(dpToPx(sel ? 3 : 1), sel ? 0xFFE10600 : 0x44FFFFFF);
                h.photo.setBackground(border);

                h.itemView.setOnClickListener(v -> {
                    profileType = PROFILE_TYPE_DRIVER;
                    selectedDriverResId = d.photoResId;
                    galleryUri = null;
                    // Show selected pic with top-aligned face crop
                    ivProfilePic.setImageResource(d.photoResId);
                    ivProfilePic.setOutlineProvider(android.view.ViewOutlineProvider.BACKGROUND);
                    ivProfilePic.setClipToOutline(true);
                    ivProfilePic.post(() -> {
                        if (ivProfilePic.getDrawable() == null) return;
                        int vw = ivProfilePic.getWidth();
                        int dw = ivProfilePic.getDrawable().getIntrinsicWidth();
                        if (vw == 0 || dw == 0) return;
                        float scale = (float) vw / dw;
                        android.graphics.Matrix m = new android.graphics.Matrix();
                        m.setScale(scale, scale);
                        ivProfilePic.setScaleType(android.widget.ImageView.ScaleType.MATRIX);
                        ivProfilePic.setImageMatrix(m);
                    });
                    highlightButton(btnPickDriver);
                    sheet.dismiss();
                });
            }

            @Override
            public int getItemCount() { return drivers.size(); }
        });

        sheet.show();
    }

    /** Saves the chosen profile pic as a file and returns its path (or null for default/driver res). */
    private String saveProfilePic(long userId) {
        if (profileType == PROFILE_TYPE_GALLERY && galleryUri != null) {
            try {
                Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), galleryUri);
                Bitmap cropped = circleCrop(bmp);
                File file = new File(getFilesDir(), "profile_" + userId + ".png");
                FileOutputStream fos = new FileOutputStream(file);
                cropped.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.close();
                return file.getAbsolutePath();
            } catch (IOException e) {
                return null;
            }
        }
        return null; // default or driver res — loaded by res id
    }

    private void setAvatarDefault() {
        ivProfilePic.setImageResource(R.drawable.ic_launcher_foreground);
    }

    private void highlightButton(MaterialButton active) {
        for (MaterialButton btn : new MaterialButton[]{btnPickDefault, btnPickDriver, btnPickGallery}) {
            boolean isActive = btn == active;
            btn.setBackgroundTintList(ColorStateList.valueOf(isActive ? 0xFFE10600 : 0xFF1A1A1A));
            btn.setStrokeColor(ColorStateList.valueOf(isActive ? 0xFFE10600 : 0x66E10600));
            btn.setStrokeWidth(isActive ? 0 : dpToPx(1));
        }
    }

    private Bitmap circleCrop(Bitmap src) {
        int size = Math.min(src.getWidth(), src.getHeight());
        int x = (src.getWidth() - size) / 2;
        int y = (src.getHeight() - size) / 2;
        Bitmap squared = Bitmap.createBitmap(src, x, y, size, size);
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(new BitmapShader(squared, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);
        return output;
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    static class DriverAvatarHolder extends RecyclerView.ViewHolder {
        ImageView photo;
        TextView name;
        DriverAvatarHolder(@NonNull View v) {
            super(v);
            photo = v.findViewById(R.id.ivDriverAvatar);
            name  = v.findViewById(R.id.tvDriverLastName);
        }
    }
}
