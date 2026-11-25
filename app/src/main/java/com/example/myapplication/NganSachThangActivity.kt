package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

class NganSachThangActivity : AppCompatActivity() {

    private lateinit var btnBackNganSach: TextView
    private lateinit var tvThangNganSach: TextView
    private lateinit var tvPhanTramDaDung: TextView
    private lateinit var tvHanMucTong: TextView
    private lateinit var pbNganSach: ProgressBar
    private lateinit var tvDaChi: TextView
    private lateinit var tvConLai: TextView
    private lateinit var tvTrangThaiCanhBao: TextView
    private lateinit var btnMoThietLapNganSach: Button
    private lateinit var btnMoThongKeThuChi: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ngan_sach_thang)

        setControl()
        setEvent()
    }

    override fun onResume() {
        super.onResume()
        capNhatGiaoDien()
        DuLieuChung.ganSuKienBottomNav(this, "THONG_KE")
    }

    private fun setControl() {
        btnBackNganSach = findViewById(R.id.btnBackNganSach)
        tvThangNganSach = findViewById(R.id.tvThangNganSach)
        tvPhanTramDaDung = findViewById(R.id.tvPhanTramDaDung)
        tvHanMucTong = findViewById(R.id.tvHanMucTong)
        pbNganSach = findViewById(R.id.pbNganSach)
        tvDaChi = findViewById(R.id.tvDaChi)
        tvConLai = findViewById(R.id.tvConLai)
        tvTrangThaiCanhBao = findViewById(R.id.tvTrangThaiCanhBao)
        btnMoThietLapNganSach = findViewById(R.id.btnMoThietLapNganSach)
        btnMoThongKeThuChi = findViewById(R.id.btnMoThongKeThuChi)
    }

    private fun setEvent() {
        btnBackNganSach.setOnClickListener { finish() }

        btnMoThietLapNganSach.setOnClickListener {
            startActivity(Intent(this, ThietLapNganSachActivity::class.java))
        }

        btnMoThongKeThuChi.setOnClickListener {
            startActivity(Intent(this, ThongKeThuChiActivity::class.java))
        }
    }

    private fun capNhatGiaoDien() {
        val thangHienTai = DuLieuChung.thangDangChon
        val formatter = DecimalFormat("#,###")
        val nganSach = DuLieuChung.layNganSach(thangHienTai)
        val daChi = DuLieuChung.tinhTongChi(thangHienTai)
        val hanMuc = nganSach.hanMuc

        val conLai = if (hanMuc - daChi > 0) hanMuc - daChi else 0
        val phanTram = if (hanMuc > 0) ((daChi * 100) / hanMuc).toInt() else 0

        tvThangNganSach.text = thangHienTai.uppercase()
        tvHanMucTong.text = "${formatter.format(hanMuc).replace(",", ".")} đ"
        tvDaChi.text = "${formatter.format(daChi).replace(",", ".")} đ"
        tvConLai.text = "${formatter.format(conLai).replace(",", ".")} đ"
        tvPhanTramDaDung.text = "$phanTram% đã dùng"
        pbNganSach.progress = phanTram.coerceIn(0, 100)

        if (nganSach.batCanhBao) {
            if (phanTram >= 80) {
                tvTrangThaiCanhBao.text = "CẢNH BÁO: Bạn đã dùng $phanTram% ngân sách tháng này!"
                tvTrangThaiCanhBao.setTextColor(0xFFEF4444.toInt())
                Toast.makeText(this, "Cảnh báo: Chi tiêu đã đạt $phanTram% ngân sách!", Toast.LENGTH_SHORT).show()
            } else {
                tvTrangThaiCanhBao.text = "Cảnh báo khi chi tiêu đạt 80% ngân sách."
                tvTrangThaiCanhBao.setTextColor(0xFF64748B.toInt())
            }
        } else {
            tvTrangThaiCanhBao.text = "Đang tắt cảnh báo vượt ngân sách."
            tvTrangThaiCanhBao.setTextColor(0xFF64748B.toInt())
        }
    }
}