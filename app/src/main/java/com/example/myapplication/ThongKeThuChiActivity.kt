package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

class ThongKeThuChiActivity : AppCompatActivity() {

    private lateinit var btnBackThongKe: TextView
    private lateinit var spThangThongKe: Spinner
    private lateinit var tvTongThu: TextView
    private lateinit var tvTongChi: TextView
    private lateinit var tvChenhLech: TextView
    private lateinit var btnXemChiTheoDanhMuc: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thong_ke_thu_chi)

        setControl()
        khoiTaoSpinner()
        setEvent()
    }

    override fun onResume() {
        super.onResume()
        val viTri = DuLieuChung.dsThang.indexOf(DuLieuChung.thangDangChon).coerceAtLeast(0)
        spThangThongKe.setSelection(viTri)
        capNhatThongKe(DuLieuChung.thangDangChon)
        DuLieuChung.ganSuKienBottomNav(this, "THONG_KE")
    }

    private fun setControl() {
        btnBackThongKe = findViewById(R.id.btnBackThongKe)
        spThangThongKe = findViewById(R.id.spThangThongKe)
        tvTongThu = findViewById(R.id.tvTongThu)
        tvTongChi = findViewById(R.id.tvTongChi)
        tvChenhLech = findViewById(R.id.tvChenhLech)
        btnXemChiTheoDanhMuc = findViewById(R.id.btnXemChiTheoDanhMuc)
    }

    private fun khoiTaoSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, DuLieuChung.dsThang)
        spThangThongKe.adapter = adapter
    }

    private fun setEvent() {
        btnBackThongKe.setOnClickListener { finish() }

        spThangThongKe.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                DuLieuChung.thangDangChon = DuLieuChung.dsThang[position]
                capNhatThongKe(DuLieuChung.thangDangChon)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnXemChiTheoDanhMuc.setOnClickListener {
            startActivity(Intent(this, ChiTheoDanhMucActivity::class.java))
        }
    }

    private fun capNhatThongKe(thangNam: String) {
        val formatter = DecimalFormat("#,###")
        val tongThu = DuLieuChung.tinhTongThu(thangNam)
        val tongChi = DuLieuChung.tinhTongChi(thangNam)
        val chenhLech = DuLieuChung.tinhSoDu(thangNam)

        tvTongThu.text = "${formatter.format(tongThu).replace(",", ".")} đ"
        tvTongChi.text = "${formatter.format(tongChi).replace(",", ".")} đ"
        tvChenhLech.text = "${formatter.format(chenhLech).replace(",", ".")} đ"
    }
}