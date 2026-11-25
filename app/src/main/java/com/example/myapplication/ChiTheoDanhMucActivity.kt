package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

class ChiTheoDanhMucActivity : AppCompatActivity() {

    private lateinit var btnBackDanhMuc: TextView
    private lateinit var spThangDanhMuc: Spinner
    private lateinit var tvTongChiDanhMuc: TextView
    private lateinit var lvChiTheoDanhMuc: ListView

    private val dsDanhMuc = ArrayList<DanhMucChiModel>()
    private lateinit var adapter: DanhMucChiAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chi_theo_danh_muc)

        setControl()
        khoiTaoDuLieu()
        setEvent()
    }

    override fun onResume() {
        super.onResume()
        val viTri = DuLieuChung.dsThang.indexOf(DuLieuChung.thangDangChon).coerceAtLeast(0)
        spThangDanhMuc.setSelection(viTri)
        capNhatDanhMuc(DuLieuChung.thangDangChon)
        DuLieuChung.ganSuKienBottomNav(this, "THONG_KE")
    }

    private fun setControl() {
        btnBackDanhMuc = findViewById(R.id.btnBackDanhMuc)
        spThangDanhMuc = findViewById(R.id.spThangDanhMuc)
        tvTongChiDanhMuc = findViewById(R.id.tvTongChiDanhMuc)
        lvChiTheoDanhMuc = findViewById(R.id.lvChiTheoDanhMuc)
    }

    private fun khoiTaoDuLieu() {
        val spAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, DuLieuChung.dsThang)
        spThangDanhMuc.adapter = spAdapter

        adapter = DanhMucChiAdapter(this, dsDanhMuc)
        lvChiTheoDanhMuc.adapter = adapter
    }

    private fun setEvent() {
        btnBackDanhMuc.setOnClickListener { finish() }

        spThangDanhMuc.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                DuLieuChung.thangDangChon = DuLieuChung.dsThang[position]
                capNhatDanhMuc(DuLieuChung.thangDangChon)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        lvChiTheoDanhMuc.setOnItemClickListener { _, _, position, _ ->
            val danhMucDuocChon = dsDanhMuc[position]
            val intent = Intent(this, GiaoDichActivity::class.java)
            intent.putExtra("LOC_DANH_MUC", danhMucDuocChon.tenDanhMuc)
            startActivity(intent)
        }
    }

    private fun capNhatDanhMuc(thangNam: String) {
        val formatter = DecimalFormat("#,###")
        val tongChi = DuLieuChung.tinhTongChi(thangNam)
        tvTongChiDanhMuc.text = "${formatter.format(tongChi).replace(",", ".")} đ"

        dsDanhMuc.clear()
        dsDanhMuc.addAll(DuLieuChung.thongKeChiTheoDanhMuc(thangNam))
        adapter.notifyDataSetChanged()
    }
}