package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.widget.TextView
import android.widget.Toast

data class GiaoDichModel(
    var id: Int,
    var tenGiaoDich: String,
    var soTien: Long,
    var loaiGiaoDich: String,
    var danhMuc: String,
    var thangNam: String,
    var ngay: String
)

data class NganSachModel(
    var thangNam: String,
    var hanMuc: Long,
    var batCanhBao: Boolean
)

object DuLieuChung {
    // Danh sách tháng dùng chung toàn App
    val dsThang = arrayOf("Tháng 10/2026", "Tháng 11/2026", "Tháng 12/2026")
    var thangDangChon: String = "Tháng 11/2026" // Đồng bộ tháng đang xem giữa các màn hình

    val danhSachGiaoDich = ArrayList<GiaoDichModel>()
    val danhSachNganSach = ArrayList<NganSachModel>()
    private var autoId = 1

    init {
        if (danhSachGiaoDich.isEmpty()) {
            // Dữ liệu mẫu Tháng 11/2026 (Thu: 8.000.000 - Chi: 2.000.000 -> Số dư: 6.000.000)
            themGiaoDich("Lương tháng 11", 8000000, "THU", "Lương", "Tháng 11/2026", "05/11/2026")
            themGiaoDich("Ăn trưa & Cà phê", 800000, "CHI", "Ăn uống", "Tháng 11/2026", "10/11/2026")
            themGiaoDich("Đổ xăng & Grab", 600000, "CHI", "Di chuyển", "Tháng 11/2026", "12/11/2026")
            themGiaoDich("Mua áo quần", 400000, "CHI", "Mua sắm", "Tháng 11/2026", "18/11/2026")
            themGiaoDich("Đóng tiền điện nước", 200000, "CHI", "Khác", "Tháng 11/2026", "20/11/2026")

            // Dữ liệu mẫu Tháng 10/2026
            themGiaoDich("Lương tháng 10", 7500000, "THU", "Lương", "Tháng 10/2026", "05/10/2026")
            themGiaoDich("Ăn uống tháng 10", 1500000, "CHI", "Ăn uống", "Tháng 10/2026", "15/10/2026")
            themGiaoDich("Di chuyển tháng 10", 500000, "CHI", "Di chuyển", "Tháng 10/2026", "20/10/2026")
        }

        if (danhSachNganSach.isEmpty()) {
            danhSachNganSach.add(NganSachModel("Tháng 10/2026", 4000000, true))
            danhSachNganSach.add(NganSachModel("Tháng 11/2026", 5000000, true))
            danhSachNganSach.add(NganSachModel("Tháng 12/2026", 6000000, true))
        }
    }

    // --- XỬ LÝ GIAO DỊCH & NẠP TIỀN ---
    fun themGiaoDich(ten: String, tien: Long, loai: String, danhMuc: String, thangNam: String, ngay: String) {
        danhSachGiaoDich.add(GiaoDichModel(autoId++, ten, tien, loai, danhMuc, thangNam, ngay))
    }

    fun napTienVaoSoDu(soTienNap: Long, thangNam: String, ghiChu: String) {
        val tenGD = if (ghiChu.isEmpty()) "Nạp tiền vào số dư" else "Nạp tiền: $ghiChu"
        val soThang = thangNam.removePrefix("Tháng ").trim() // VD: "11/2026"
        themGiaoDich(tenGD, soTienNap, "THU", "Nạp ví", thangNam, "26/$soThang")
    }

    fun xoaGiaoDich(id: Int) {
        danhSachGiaoDich.removeAll { it.id == id }
    }

    fun suaGiaoDich(gdMoi: GiaoDichModel) {
        val index = danhSachGiaoDich.indexOfFirst { it.id == gdMoi.id }
        if (index != -1) {
            danhSachGiaoDich[index] = gdMoi
        }
    }

    // --- TÍNH TOÁN ĐỒNG BỘ ---
    fun tinhTongThu(thangNam: String): Long {
        return danhSachGiaoDich
            .filter { it.thangNam == thangNam && it.loaiGiaoDich == "THU" }
            .sumOf { it.soTien }
    }

    fun tinhTongChi(thangNam: String): Long {
        return danhSachGiaoDich
            .filter { it.thangNam == thangNam && it.loaiGiaoDich == "CHI" }
            .sumOf { it.soTien }
    }

    fun tinhSoDu(thangNam: String): Long {
        return tinhTongThu(thangNam) - tinhTongChi(thangNam)
    }

    fun layNganSach(thangNam: String): NganSachModel {
        var ns = danhSachNganSach.find { it.thangNam == thangNam }
        if (ns == null) {
            ns = NganSachModel(thangNam, 5000000, true)
            danhSachNganSach.add(ns)
        }
        return ns
    }

    fun luuNganSach(thangNam: String, hanMucMoi: Long, batCanhBao: Boolean) {
        val ns = layNganSach(thangNam)
        ns.hanMuc = hanMucMoi
        ns.batCanhBao = batCanhBao
    }

    fun thongKeChiTheoDanhMuc(thangNam: String): ArrayList<DanhMucChiModel> {
        val ketQua = ArrayList<DanhMucChiModel>()
        val tongChiThang = tinhTongChi(thangNam)
        val cacDanhMucChinh = listOf("Ăn uống", "Di chuyển", "Mua sắm")

        // 1. Tính tiền cho 3 danh mục chính
        for (tenDM in cacDanhMucChinh) {
            val tienDM = danhSachGiaoDich
                .filter {
                    it.thangNam.trim().equals(thangNam.trim(), ignoreCase = true) &&
                            it.loaiGiaoDich.trim().equals("CHI", ignoreCase = true) &&
                            it.danhMuc.trim().equals(tenDM, ignoreCase = true)
                }
                .sumOf { it.soTien }

            val phanTram = if (tongChiThang > 0) ((tienDM * 100) / tongChiThang).toInt() else 0
            ketQua.add(DanhMucChiModel(tenDM, tienDM, phanTram))
        }

        // 2. Mục "Khác": Tự động cộng tất cả khoản CHI không thuộc 3 mục trên
        val tienKhac = danhSachGiaoDich
            .filter { gd ->
                gd.thangNam.trim().equals(thangNam.trim(), ignoreCase = true) &&
                        gd.loaiGiaoDich.trim().equals("CHI", ignoreCase = true) &&
                        cacDanhMucChinh.none { dm -> gd.danhMuc.trim().equals(dm, ignoreCase = true) }
            }
            .sumOf { it.soTien }

        val phanTramKhac = if (tongChiThang > 0) ((tienKhac * 100) / tongChiThang).toInt() else 0
        ketQua.add(DanhMucChiModel("Khác", tienKhac, phanTramKhac))

        return ketQua
    }

    // --- KÍCH HOẠT THANH ĐIỀU HƯỚNG DƯỚI CÙNG CHO MỌI MÀN HÌNH ---
    fun ganSuKienBottomNav(activity: Activity, tabDangChon: String) {
        val navTrangChu = activity.findViewById<TextView>(R.id.navTrangChu) ?: return
        val navGiaoDich = activity.findViewById<TextView>(R.id.navGiaoDich)
        val navThemMoi = activity.findViewById<TextView>(R.id.navThemMoi)
        val navThongKe = activity.findViewById<TextView>(R.id.navThongKe)
        val navCaNhan = activity.findViewById<TextView>(R.id.navCaNhan)

        // Reset màu tất cả tab về xám
        val mauXam = 0xFF64748B.toInt()
        val mauXanh = 0xFF2563EB.toInt()
        navTrangChu.setTextColor(mauXam)
        navGiaoDich.setTextColor(mauXam)
        navThemMoi.setTextColor(mauXam)
        navThongKe.setTextColor(mauXam)
        navCaNhan.setTextColor(mauXam)

        // Tô xanh tab hiện tại
        when (tabDangChon) {
            "TRANG_CHU" -> navTrangChu.setTextColor(mauXanh)
            "GIAO_DICH" -> navGiaoDich.setTextColor(mauXanh)
            "THONG_KE" -> navThongKe.setTextColor(mauXanh)
            "CA_NHAN" -> navCaNhan.setTextColor(mauXanh)
        }

        navTrangChu.setOnClickListener {
            if (activity !is TrangChuActivity) {
                val intent = Intent(activity, TrangChuActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                activity.startActivity(intent)
            }
        }

        navGiaoDich.setOnClickListener {
            if (activity !is GiaoDichActivity) {
                activity.startActivity(Intent(activity, GiaoDichActivity::class.java))
            }
        }

        navThemMoi.setOnClickListener {
            if (activity !is GiaoDichActivity) {
                val intent = Intent(activity, GiaoDichActivity::class.java)
                intent.putExtra("MO_FORM_THEM", true)
                activity.startActivity(intent)
            } else {
                Toast.makeText(activity, "Nhập thông tin giao dịch ở khung phía trên!", Toast.LENGTH_SHORT).show()
            }
        }

        navThongKe.setOnClickListener {
            if (activity !is ThongKeThuChiActivity) {
                activity.startActivity(Intent(activity, ThongKeThuChiActivity::class.java))
            }
        }

        navCaNhan.setOnClickListener {
            Toast.makeText(activity, "Xin chào admin", Toast.LENGTH_SHORT).show()
        }
    }
}