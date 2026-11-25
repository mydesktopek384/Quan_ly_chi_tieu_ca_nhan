package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ThietLapNganSachActivity : AppCompatActivity() {

    private lateinit var btnBackThietLap: TextView
    private lateinit var spThangApDung: Spinner
    private lateinit var edtHanMucNganSach: EditText
    private lateinit var swCanhBaoChiTieu: Switch
    private lateinit var btnLuuNganSach: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thiet_lap_ngan_sach)

        setControl()
        khoiTaoDuLieu()
        setEvent()
    }

    private fun setControl() {
        btnBackThietLap = findViewById(R.id.btnBackThietLap)
        spThangApDung = findViewById(R.id.spThangApDung)
        edtHanMucNganSach = findViewById(R.id.edtHanMucNganSach)
        swCanhBaoChiTieu = findViewById(R.id.swCanhBaoChiTieu)
        btnLuuNganSach = findViewById(R.id.btnLuuNganSach)
    }

    private fun khoiTaoDuLieu() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, DuLieuChung.dsThang)
        spThangApDung.adapter = adapter

        val viTri = DuLieuChung.dsThang.indexOf(DuLieuChung.thangDangChon).coerceAtLeast(0)
        spThangApDung.setSelection(viTri)
        taiThongTinNganSachTheoThang(DuLieuChung.dsThang[viTri])
    }

    private fun taiThongTinNganSachTheoThang(thang: String) {
        val ns = DuLieuChung.layNganSach(thang)
        edtHanMucNganSach.setText(ns.hanMuc.toString())
        swCanhBaoChiTieu.isChecked = ns.batCanhBao
        swCanhBaoChiTieu.text = if (ns.batCanhBao) "Bật " else "Tắt "
    }

    private fun setEvent() {
        btnBackThietLap.setOnClickListener { finish() }

        spThangApDung.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                taiThongTinNganSachTheoThang(DuLieuChung.dsThang[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        swCanhBaoChiTieu.setOnCheckedChangeListener { _, isChecked ->
            swCanhBaoChiTieu.text = if (isChecked) "Bật " else "Tắt "
        }

        btnLuuNganSach.setOnClickListener {
            val chuoiTien = edtHanMucNganSach.text.toString().trim()
            if (chuoiTien.isEmpty()) {
                edtHanMucNganSach.error = "Vui lòng nhập hạn mức ngân sách"
                edtHanMucNganSach.requestFocus()
                return@setOnClickListener
            }

            val hanMucMoi = chuoiTien.toLongOrNull() ?: 0L
            if (hanMucMoi <= 0) {
                edtHanMucNganSach.error = "Hạn mức phải lớn hơn 0"
                return@setOnClickListener
            }

            val thangChon = spThangApDung.selectedItem.toString()
            DuLieuChung.thangDangChon = thangChon
            DuLieuChung.luuNganSach(thangChon, hanMucMoi, swCanhBaoChiTieu.isChecked)

            Toast.makeText(this, "Đã lưu ngân sách $thangChon!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}