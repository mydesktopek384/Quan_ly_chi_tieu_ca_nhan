package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

class TrangChuActivity : AppCompatActivity() {

    private lateinit var spThangTrangChu: Spinner
    private lateinit var tvNapTienTrangChu: TextView
    private lateinit var tvAnHienSoDu: TextView
    private lateinit var tvSoDuTrangChu: TextView
    private lateinit var tvThuTrangChu: TextView
    private lateinit var tvChiTrangChu: TextView

    private lateinit var btnQuickGiaoDich: Button
    private lateinit var btnQuickNganSach: Button
    private lateinit var btnQuickThongKe: Button
    private lateinit var btnQuickDanhMuc: Button

    private lateinit var cardNganSachTrangChu: LinearLayout
    private lateinit var tvTiLeNganSachTC: TextView
    private lateinit var pbNganSachTC: ProgressBar
    private lateinit var tvChiTietNganSachTC: TextView

    private lateinit var tvXemTatCaGD: TextView
    private lateinit var lvGiaoDichGanDay: ListView

    private val dsGanDay = ArrayList<GiaoDichModel>()
    private lateinit var adapter: GiaoDichAdapter

    private var dangAnSoDu = false
    private var chuoiSoDuThat = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trang_chu)

        setControl()
        khoiTaoDuLieu()
        setEvent()
    }

    override fun onResume() {
        super.onResume()
        val viTriThang = DuLieuChung.dsThang.indexOf(DuLieuChung.thangDangChon).coerceAtLeast(0)
        spThangTrangChu.setSelection(viTriThang)
        capNhatTongQuan(DuLieuChung.thangDangChon)
        DuLieuChung.ganSuKienBottomNav(this, "TRANG_CHU")
    }

    private fun setControl() {
        spThangTrangChu = findViewById(R.id.spThangTrangChu)
        tvNapTienTrangChu = findViewById(R.id.tvNapTienTrangChu)
        tvAnHienSoDu = findViewById(R.id.tvAnHienSoDu)
        tvSoDuTrangChu = findViewById(R.id.tvSoDuTrangChu)
        tvThuTrangChu = findViewById(R.id.tvThuTrangChu)
        tvChiTrangChu = findViewById(R.id.tvChiTrangChu)

        btnQuickGiaoDich = findViewById(R.id.btnQuickGiaoDich)
        btnQuickNganSach = findViewById(R.id.btnQuickNganSach)
        btnQuickThongKe = findViewById(R.id.btnQuickThongKe)
        btnQuickDanhMuc = findViewById(R.id.btnQuickDanhMuc)

        cardNganSachTrangChu = findViewById(R.id.cardNganSachTrangChu)
        tvTiLeNganSachTC = findViewById(R.id.tvTiLeNganSachTC)
        pbNganSachTC = findViewById(R.id.pbNganSachTC)
        tvChiTietNganSachTC = findViewById(R.id.tvChiTietNganSachTC)

        tvXemTatCaGD = findViewById(R.id.tvXemTatCaGD)
        lvGiaoDichGanDay = findViewById(R.id.lvGiaoDichGanDay)
    }

    private fun khoiTaoDuLieu() {
        val spAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, DuLieuChung.dsThang)
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spThangTrangChu.adapter = spAdapter

        val viTri = DuLieuChung.dsThang.indexOf(DuLieuChung.thangDangChon).coerceAtLeast(0)
        spThangTrangChu.setSelection(viTri)

        adapter = GiaoDichAdapter(this, dsGanDay)
        lvGiaoDichGanDay.adapter = adapter
    }

    private fun setEvent() {
        spThangTrangChu.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                DuLieuChung.thangDangChon = DuLieuChung.dsThang[position]
                capNhatTongQuan(DuLieuChung.thangDangChon)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // CHỨC NĂNG NẠP TIỀN VÀO SỐ DƯ HIỆN TẠI
        tvNapTienTrangChu.setOnClickListener {
            hienDialogNapTien()
        }

        tvAnHienSoDu.setOnClickListener {
            dangAnSoDu = !dangAnSoDu
            if (dangAnSoDu) {
                tvSoDuTrangChu.text = "•••••••• đ"
                tvAnHienSoDu.text = "Hiện số dư"
            } else {
                tvSoDuTrangChu.text = chuoiSoDuThat
                tvAnHienSoDu.text = "Ẩn số dư"
            }
        }

        btnQuickGiaoDich.setOnClickListener {
            startActivity(Intent(this, GiaoDichActivity::class.java))
        }
        btnQuickNganSach.setOnClickListener {
            startActivity(Intent(this, NganSachThangActivity::class.java))
        }
        btnQuickThongKe.setOnClickListener {
            startActivity(Intent(this, ThongKeThuChiActivity::class.java))
        }
        btnQuickDanhMuc.setOnClickListener {
            startActivity(Intent(this, ChiTheoDanhMucActivity::class.java))
        }

        cardNganSachTrangChu.setOnClickListener {
            startActivity(Intent(this, NganSachThangActivity::class.java))
        }

        tvXemTatCaGD.setOnClickListener {
            startActivity(Intent(this, GiaoDichActivity::class.java))
        }
    }

    private fun hienDialogNapTien() {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 30, 50, 10)

        val edtSoTienNap = EditText(this)
        edtSoTienNap.hint = "Nhập số tiền cần nạp (VNĐ)"
        edtSoTienNap.inputType = InputType.TYPE_CLASS_NUMBER
        layout.addView(edtSoTienNap)

        val edtGhiChuNap = EditText(this)
        edtGhiChuNap.hint = "Nguồn tiền / Ghi chú (VD: Tiền thưởng)"
        layout.addView(edtGhiChuNap)

        AlertDialog.Builder(this)
            .setTitle("Nạp tiền vào số dư (${DuLieuChung.thangDangChon})")
            .setView(layout)
            .setPositiveButton("Nạp ngay") { _, _ ->
                val soTien = edtSoTienNap.text.toString().trim().toLongOrNull() ?: 0L
                val ghiChu = edtGhiChuNap.text.toString().trim()
                if (soTien > 0) {
                    DuLieuChung.napTienVaoSoDu(soTien, DuLieuChung.thangDangChon, ghiChu)
                    capNhatTongQuan(DuLieuChung.thangDangChon)
                    Toast.makeText(this, "Đã nạp thành công!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Số tiền nạp phải lớn hơn 0!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun capNhatTongQuan(thangNam: String) {
        val formatter = DecimalFormat("#,###")

        val tongThu = DuLieuChung.tinhTongThu(thangNam)
        val tongChi = DuLieuChung.tinhTongChi(thangNam)
        val soDu = DuLieuChung.tinhSoDu(thangNam)

        chuoiSoDuThat = "${formatter.format(soDu).replace(",", ".")} đ"
        tvSoDuTrangChu.text = if (dangAnSoDu) "•••••••• đ" else chuoiSoDuThat
        tvThuTrangChu.text = "+${formatter.format(tongThu).replace(",", ".")} đ"
        tvChiTrangChu.text = "-${formatter.format(tongChi).replace(",", ".")} đ"

        val ns = DuLieuChung.layNganSach(thangNam)
        val phanTram = if (ns.hanMuc > 0) ((tongChi * 100) / ns.hanMuc).toInt() else 0
        val daChiFmt = formatter.format(tongChi).replace(",", ".")
        val hanMucFmt = formatter.format(ns.hanMuc).replace(",", ".")

        tvTiLeNganSachTC.text = "$phanTram%"
        pbNganSachTC.progress = phanTram.coerceIn(0, 100)
        tvChiTietNganSachTC.text = "Đã chi $daChiFmt đ / $hanMucFmt đ"

        dsGanDay.clear()
        val locTheoThang = DuLieuChung.danhSachGiaoDich
            .filter { it.thangNam == thangNam }
            .reversed()
            .take(3)
        dsGanDay.addAll(locTheoThang)
        adapter.notifyDataSetChanged()
    }
}