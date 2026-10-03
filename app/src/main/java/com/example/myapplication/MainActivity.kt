package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    companion object {
        const val NAMA_PREFS = "profil_prefs"
        const val KEY_NAMA = "nama"
        const val KEY_EMAIL = "email"
        const val KEY_KOTA = "kota"
        const val KEY_BIO = "bio"
        const val KEY_AVATAR = "avatar"
        const val KEY_MODE_GELAP = "mode_gelap"
        const val KEY_JUMLAH_BUKA = "jumlah_buka"

        val DAFTAR_AVATAR = intArrayOf(
            R.drawable.avatar_1,
            R.drawable.avatar_2,
            R.drawable.avatar_3,
        )
    }

    private val prefs by lazy {
        getSharedPreferences(NAMA_PREFS, MODE_PRIVATE)
    }

    private var indeksAvatar: Int = 0

    private lateinit var layoutUtama: ScrollView
    private lateinit var cardProfil: MaterialCardView
    private lateinit var ivAvatar: ImageView
    private lateinit var tvNamaKartu: TextView
    private lateinit var tvEmailKartu: TextView
    private lateinit var tvKotaKartu: TextView
    private lateinit var dividerKartu: View
    private lateinit var tvJumlahBuka: TextView

    private lateinit var etNama: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etKota: TextInputEditText
    private lateinit var etBio: TextInputEditText

    private lateinit var btnGantiAvatar: MaterialButton
    private lateinit var swGelap: MaterialSwitch
    private lateinit var btnSimpan: MaterialButton
    private lateinit var btnReset: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layoutUtama)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        inialisasiView()

        // Incremental penghitung pembukaan aplikasi (setiap kali aplikasi dibuka)
        val jumlahBuka = prefs.getInt(KEY_JUMLAH_BUKA, 0) + 1
        prefs.edit {
            putInt(KEY_JUMLAH_BUKA, jumlahBuka)
        }

        hubungkanListener()
        muatProfil()
    }

    private fun inialisasiView() {
        layoutUtama = findViewById(R.id.layoutUtama)
        cardProfil = findViewById(R.id.cardProfil)
        ivAvatar = findViewById(R.id.ivAvatar)
        tvNamaKartu = findViewById(R.id.tvNamaKartu)
        tvEmailKartu = findViewById(R.id.tvEmailKartu)
        tvKotaKartu = findViewById(R.id.tvKotaKartu)
        dividerKartu = findViewById(R.id.dividerKartu)
        tvJumlahBuka = findViewById(R.id.tvJumlahBuka)

        etNama = findViewById(R.id.etNama)
        etEmail = findViewById(R.id.etEmail)
        etKota = findViewById(R.id.etKota)
        etBio = findViewById(R.id.etBio)

        btnGantiAvatar = findViewById(R.id.btnGantiAvatar)
        swGelap = findViewById(R.id.swGelap)
        btnSimpan = findViewById(R.id.btnSimpan)
        btnReset = findViewById(R.id.btnReset)
    }

    private fun hubungkanListener() {
        etNama.doOnTextChanged { _, _, _, _ -> perbaruiKartu() }
        etEmail.doOnTextChanged { _, _, _, _ -> perbaruiKartu() }
        etKota.doOnTextChanged { _, _, _, _ -> perbaruiKartu() }

        btnGantiAvatar.setOnClickListener { gantiAvatar() }
        swGelap.setOnCheckedChangeListener { _, isChecked -> terapkanModeGelap(isChecked) }
        btnSimpan.setOnClickListener { simpanProfil() }
        btnReset.setOnClickListener { hapusSemuaData() }
    }

    private fun simpanProfil() {
        val nama = etNama.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val kota = etKota.text.toString().trim()
        val bio = etBio.text.toString().trim()
        val modeGelap = swGelap.isChecked

        prefs.edit {
            putString(KEY_NAMA, nama)
            putString(KEY_EMAIL, email)
            putString(KEY_KOTA, kota)
            putString(KEY_BIO, bio)
            putInt(KEY_AVATAR, indeksAvatar)
            putBoolean(KEY_MODE_GELAP, modeGelap)
        }

        Toast.makeText(this, getString(R.string.msg_tersimpan), Toast.LENGTH_SHORT).show()
        perbaruiKartu()
    }

    private fun muatProfil() {
        val nama = prefs.getString(KEY_NAMA, "") ?: ""
        val email = prefs.getString(KEY_EMAIL, "") ?: ""
        val kota = prefs.getString(KEY_KOTA, "") ?: ""
        val bio = prefs.getString(KEY_BIO, "") ?: ""
        indeksAvatar = prefs.getInt(KEY_AVATAR, 0)
        val modeGelap = prefs.getBoolean(KEY_MODE_GELAP, false)
        val jumlahBuka = prefs.getInt(KEY_JUMLAH_BUKA, 1)

        etNama.setText(nama)
        etEmail.setText(email)
        etKota.setText(kota)
        etBio.setText(bio)

        swGelap.isChecked = modeGelap
        tvJumlahBuka.text = getString(R.string.jumlah_buka_format, jumlahBuka)

        terapkanModeGelap(modeGelap)
        perbaruiKartu()
    }

    private fun hapusSemuaData() {
        prefs.edit { clear() }
        Toast.makeText(this, getString(R.string.msg_dihapus), Toast.LENGTH_SHORT).show()
        muatProfil()
    }

    private fun gantiAvatar() {
        indeksAvatar = (indeksAvatar + 1) % DAFTAR_AVATAR.size
        if (indeksAvatar in DAFTAR_AVATAR.indices) {
            ivAvatar.setImageResource(DAFTAR_AVATAR[indeksAvatar])
        }
    }

    private fun perbaruiKartu() {
        val namaText = etNama.text.toString().trim()
        val emailText = etEmail.text.toString().trim()
        val kotaText = etKota.text.toString().trim()

        tvNamaKartu.text = namaText.ifEmpty { getString(R.string.nama_default) }
        tvEmailKartu.text = emailText.ifEmpty { getString(R.string.email_default) }
        tvKotaKartu.text = kotaText.ifEmpty { getString(R.string.kota_default) }

        if (indeksAvatar in DAFTAR_AVATAR.indices) {
            ivAvatar.setImageResource(DAFTAR_AVATAR[indeksAvatar])
        }
    }

    private fun terapkanModeGelap(aktif: Boolean) {
        if (aktif) {
            layoutUtama.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_dark))
            cardProfil.setCardBackgroundColor(ContextCompat.getColor(this, R.color.card_bg_dark))
            tvNamaKartu.setTextColor(ContextCompat.getColor(this, R.color.text_primary_dark))
            tvEmailKartu.setTextColor(ContextCompat.getColor(this, R.color.text_secondary_dark))
            tvKotaKartu.setTextColor(ContextCompat.getColor(this, R.color.text_secondary_dark))
            tvJumlahBuka.setTextColor(ContextCompat.getColor(this, R.color.text_primary_dark))
            swGelap.setTextColor(ContextCompat.getColor(this, R.color.text_primary_dark))
            dividerKartu.setBackgroundColor("#33FFFFFF".toColorInt())
        } else {
            layoutUtama.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_light))
            cardProfil.setCardBackgroundColor(ContextCompat.getColor(this, R.color.card_bg_light))
            tvNamaKartu.setTextColor(ContextCompat.getColor(this, R.color.text_primary_light))
            tvEmailKartu.setTextColor(ContextCompat.getColor(this, R.color.text_secondary_light))
            tvKotaKartu.setTextColor(ContextCompat.getColor(this, R.color.text_secondary_light))
            tvJumlahBuka.setTextColor(ContextCompat.getColor(this, R.color.text_primary_light))
            swGelap.setTextColor(ContextCompat.getColor(this, R.color.text_primary_light))
            dividerKartu.setBackgroundColor("#1F000000".toColorInt())
        }
    }
}
