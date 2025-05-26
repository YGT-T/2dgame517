package com.example.projectc2dgame;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
// import android.widget.Toast; // Toast kaldırıldı

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Sections extends AppCompatActivity {
    // Oyuncu 2 (Mirrored) için seçilen karakterin Bitmap değişkenleri
    public static Bitmap characterRunMirrorSprite;
    public static Bitmap characterJumpMirrorSprite;
    public static Bitmap characterIdleMirrorSprite;
    public static Bitmap characterStartingMirrorSprite;
    public static Bitmap characterAttackMirrorSprite;
    public static Bitmap characterHurtMirrorSprite;
    public static Bitmap characterDeadMirrorSprite;
    public static Bitmap characterShieldMirrorSprite;

    // Oyuncu 1 (Normal) için seçilen karakterin Bitmap değişkenleri
    public static Bitmap characterRunSprite;
    public static Bitmap characterJumpSprite;
    public static Bitmap characterIdleSprite;
    public static Bitmap characterStartingSprite;
    public static Bitmap characterAttackSprite;
    public static Bitmap characterHurtSprite;
    public static Bitmap characterDeadSprite;
    public static Bitmap characterShieldSprite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sections);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void goBack(View view) {
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);

    }

    public void goGamePanel(View view) {
        // Karakterlerin seçilip seçilmediğini kontrol edebilirsiniz.
        if (characterRunSprite == null || characterRunMirrorSprite == null) {
            // Toast.makeText(this, "Lütfen her iki oyuncu için de karakter seçin!", Toast.LENGTH_LONG).show(); // Toast kaldırıldı
            // Burada karakter seçilmediğine dair farklı bir uyarı verilebilir veya buton pasif edilebilir.
            return;
        }
        Intent intent = new Intent(this, GamePanel.class);
        startActivity(intent);
    }


    public void character1(View view) {
        characterRunMirrorSprite = BitmapFactory.decodeResource(getResources(), R.drawable.shinobi_run_mirror_sprite);
        characterJumpMirrorSprite = BitmapFactory.decodeResource(getResources(), R.drawable.shinobi_jump_mirror_sprite);
        characterIdleMirrorSprite = BitmapFactory.decodeResource(getResources(), R.drawable.shinobi_idle_mirror_sprite);
        characterStartingMirrorSprite = BitmapFactory.decodeResource(getResources(), R.drawable.shinobi_starting_mirror_sprite);
        characterAttackMirrorSprite = BitmapFactory.decodeResource(getResources(), R.drawable.shinobi_attack_mirror_sprite);
        characterHurtMirrorSprite = BitmapFactory.decodeResource(getResources(), R.drawable.shinobi_hurt_mirror_sprite);
        characterDeadMirrorSprite = BitmapFactory.decodeResource(getResources(), R.drawable.shinobi_dead_mirror_sprite);
        characterShieldMirrorSprite = BitmapFactory.decodeResource(getResources(), R.drawable.shinobi_shield_mirror_sprite);
        Toast.makeText(this, "Oyuncu 2: Shinobi Seçildi", Toast.LENGTH_SHORT).show();
    }


    public void character_mirror1(View view) {
        characterRunSprite = BitmapFactory.decodeResource(getResources(), R.drawable.shinobi_run_sprite);
        characterJumpSprite = BitmapFactory.decodeResource(getResources(), R.drawable.shinobi_jump_sprite);
        characterIdleSprite = BitmapFactory.decodeResource(getResources(), R.drawable.shinobi_idle_sprite);
        characterStartingSprite = BitmapFactory.decodeResource(getResources(), R.drawable.shinobi_starting_sprite);
        characterAttackSprite = BitmapFactory.decodeResource(getResources(), R.drawable.shinobi_attack_sprite);
        characterHurtSprite = BitmapFactory.decodeResource(getResources(), R.drawable.shinobi_hurt_sprite);
        characterDeadSprite = BitmapFactory.decodeResource(getResources(), R.drawable.shinobi_dead_sprite);
        characterShieldSprite = BitmapFactory.decodeResource(getResources(), R.drawable.shinobi_shield_sprite);
         Toast.makeText(this, "Oyuncu 1: Shinobi Seçildi", Toast.LENGTH_SHORT).show();
    }


    public void character_mirror2(View view) {
        characterRunMirrorSprite = BitmapFactory.decodeResource(getResources(), R.drawable.fighter_run_mirror_sprite);
        characterJumpMirrorSprite = BitmapFactory.decodeResource(getResources(), R.drawable.fighter_jump_mirror_sprite);
        characterIdleMirrorSprite = BitmapFactory.decodeResource(getResources(), R.drawable.fighter_idle_mirror_sprite);
        characterStartingMirrorSprite = BitmapFactory.decodeResource(getResources(), R.drawable.fighter_attack_mirror_sprite);
        characterAttackMirrorSprite = BitmapFactory.decodeResource(getResources(), R.drawable.fighter_attack_mirror_sprite);
        characterHurtMirrorSprite = BitmapFactory.decodeResource(getResources(), R.drawable.fighter_hurt_mirror_sprite);
        characterDeadMirrorSprite = BitmapFactory.decodeResource(getResources(), R.drawable.fighter_dead_mirror_sprite);
        characterShieldMirrorSprite = BitmapFactory.decodeResource(getResources(), R.drawable.fighter_shield_mirror_sprite);
      Toast.makeText(this, "Oyuncu 2: Fighter Seçildi", Toast.LENGTH_SHORT).show();
    }

    // Bu metod XML'de @+id/character2 (fighter.png) tarafından çağrılıyor.
    // Oyuncu 1 için Fighter (normal) sprite'larını yükler.
    public void character2(View view) {
        characterRunSprite = BitmapFactory.decodeResource(getResources(), R.drawable.fighter_run_sprite);
        characterJumpSprite = BitmapFactory.decodeResource(getResources(), R.drawable.fighter_jump_sprite);
        characterIdleSprite = BitmapFactory.decodeResource(getResources(), R.drawable.fighter_idle_sprite);
        characterStartingSprite = BitmapFactory.decodeResource(getResources(), R.drawable.fighter_attack_sprite);
        characterAttackSprite = BitmapFactory.decodeResource(getResources(), R.drawable.fighter_attack_sprite);
        characterHurtSprite = BitmapFactory.decodeResource(getResources(), R.drawable.fighter_hurt_sprite);
        characterDeadSprite = BitmapFactory.decodeResource(getResources(), R.drawable.fighter_dead_sprite);
        characterShieldSprite = BitmapFactory.decodeResource(getResources(), R.drawable.fighter_shield_sprite);
        Toast.makeText(this, "Oyuncu 1: Fighter Seçildi", Toast.LENGTH_SHORT).show();
    }
}
