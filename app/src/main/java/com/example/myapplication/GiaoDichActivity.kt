package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ListView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class GiaoDichActivity : AppCompatActivity() {

    private lateinit var btnBackGiaoDich: TextView
    private lateinit var btnDiDenThongKe: Button
    private lateinit var rgLoaiGD: RadioGroup
    private lateinit var radChi: RadioButton
    private lateinit var radThu: RadioButton
    private lateinit var edtTenGD: EditText
    private lateinit var edtSoTienGD: EditText
    private lateinit var spDanhMucGD: Spinner
    private lateinit var spThangGD: Spinner
    private lateinit var edtNgayGD: EditText
    private lateinit var cbXacNhanGD: CheckBox

    private lateinit var btnThemGD: Button
    private lateinit var btnSuaGD: Button
    private lateinit var btnXoaGD: Button
    private lateinit var btnLamMoiGD: Button

    private lateinit var edtTimKiemGD: EditText
    private lateinit var spLocLoaiGD: Spinner
    private lateinit var lvGiaoDich: ListView

    private val dsDanhMuc = arrayOf("Ăn uống", "Di chuyển", "Mua sắm", "Lương", "Nạp ví", "Khác")
    private val dsBoLoc = arrayOf("Tất cả", "Khoản Chi", "Khoản Thu", "Ăn uống", "Di chuyển", "Mua sắm", "Lương", "Nạp ví", "Khác")
    private val dsThang = arrayOf("Tháng 10/2026", "Tháng 11/2026", "Tháng 12/2026")

    private val dsHienThi = ArrayList<GiaoDichModel>()
    private lateinit var adapter: GiaoDichAdapter
    private var giaoDichDangChon: GiaoDichModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_giao_dich)

        setControl()
        khoiTaoDuLieu()
        setEvent()
    }
    override fun onResume() {
        super.onResume()
        DuLieuChung.ganSuKienBottomNav(this, "GIAO_DICH")
    }

    private fun setControl() {
        btnBackGiaoDich = findViewById(R.id.btnBackGiaoDich)
        btnDiDenThongKe = findViewById(R.id.btnDiDenThongKe)
        rgLoaiGD = findViewById(R.id.rgLoaiGD)
        radChi = findViewById(R.id.radChi)
        radThu = findViewById(R.id.radThu)
        edtTenGD = findViewById(R.id.edtTenGD)
        edtSoTienGD = findViewById(R.id.edtSoTienGD)
        spDanhMucGD = findViewById(R.id.spDanhMucGD)
        spThangGD = findViewById(R.id.spThangGD)
        edtNgayGD = findViewById(R.id.edtNgayGD)
        cbXacNhanGD = findViewById(R.id.cbXacNhanGD)

        btnThemGD = findViewById(R.id.btnThemGD)
        btnSuaGD = findViewById(R.id.btnSuaGD)
        btnXoaGD = findViewById(R.id.btnXoaGD)
        btnLamMoiGD = findViewById(R.id.btnLamMoiGD)

        edtTimKiemGD = findViewById(R.id.edtTimKiemGD)
        spLocLoaiGD = findViewById(R.id.spLocLoaiGD)
        lvGiaoDich = findViewById(R.id.lvGiaoDich)
    }

    private fun khoiTaoDuLieu() {
        spDanhMucGD.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dsDanhMuc)
        spThangGD.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dsThang)
        spThangGD.setSelection(1) // Mặc định Tháng 11/2026

        spLocLoaiGD.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dsBoLoc)

        // Nếu được mở từ màn hình Chi theo danh mục thì tự động lọc đúng danh mục đó
        val danhMucLocTuThongKe = intent.getStringExtra("LOC_DANH_MUC")
        if (danhMucLocTuThongKe != null) {
            val pos = dsBoLoc.indexOf(danhMucLocTuThongKe)
            if (pos >= 0) spLocLoaiGD.setSelection(pos)
        }

        adapter = GiaoDichAdapter(this, dsHienThi)
        lvGiaoDich.adapter = adapter
        locVaHienThiDanhSach()
    }

    private fun setEvent() {
        btnBackGiaoDich.setOnClickListener { finish() }

        btnDiDenThongKe.setOnClickListener {
            startActivity(Intent(this, NganSachThangActivity::class.java))
        }

        // Tự động đổi danh mục gợi ý khi chọn Thu hoặc Chi
        rgLoaiGD.setOnCheckedChangeListener { _, checkedId ->
            val dmHienTai = spDanhMucGD.selectedItem?.toString() ?: ""
            if (checkedId == R.id.radThu) {
                if (dmHienTai != "Lương" && dmHienTai != "Nạp ví") {
                    spDanhMucGD.setSelection(dsDanhMuc.indexOf("Lương"))
                }
            } else {
                // Nếu đang ở danh mục của Khoản Thu thì mới chuyển về Ăn uống, còn đang chọn Di chuyển/Mua sắm thì giữ nguyên
                if (dmHienTai == "Lương" || dmHienTai == "Nạp ví") {
                    spDanhMucGD.setSelection(0)
                }
            }
        }

        // 1. CHỨC NĂNG THÊM
        btnThemGD.setOnClickListener {
            if (!kiemTraHopLe()) return@setOnClickListener

            val ten = edtTenGD.text.toString().trim()
            val tien = edtSoTienGD.text.toString().trim().toLong()
            val loai = if (radThu.isChecked) "THU" else "CHI"
            val danhMuc = spDanhMucGD.selectedItem.toString()
            val thang = spThangGD.selectedItem.toString()
            val ngay = edtNgayGD.text.toString().trim()

            DuLieuChung.themGiaoDich(ten, tien, loai, danhMuc, thang, ngay)
            locVaHienThiDanhSach()
            lamMoiForm()
            Toast.makeText(this, "Đã thêm giao dịch!", Toast.LENGTH_SHORT).show()
        }

        // Bấm vào 1 dòng trong ListView để chọn sửa/xóa
        lvGiaoDich.setOnItemClickListener { _, _, position, _ ->
            val gd = dsHienThi[position]
            giaoDichDangChon = gd

            edtTenGD.setText(gd.tenGiaoDich)
            edtSoTienGD.setText(gd.soTien.toString())
            edtNgayGD.setText(gd.ngay)
            if (gd.loaiGiaoDich == "THU") radThu.isChecked = true else radChi.isChecked = true

            val posDM = dsDanhMuc.indexOf(gd.danhMuc)
            if (posDM >= 0) spDanhMucGD.setSelection(posDM)

            val posThang = dsThang.indexOf(gd.thangNam)
            if (posThang >= 0) spThangGD.setSelection(posThang)
        }

        // 2. CHỨC NĂNG SỬA
        btnSuaGD.setOnClickListener {
            val gd = giaoDichDangChon
            if (gd == null) {
                Toast.makeText(this, "Vui lòng chọn 1 giao dịch bên dưới để sửa!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!kiemTraHopLe()) return@setOnClickListener

            gd.tenGiaoDich = edtTenGD.text.toString().trim()
            gd.soTien = edtSoTienGD.text.toString().trim().toLong()
            gd.loaiGiaoDich = if (radThu.isChecked) "THU" else "CHI"
            gd.danhMuc = spDanhMucGD.selectedItem.toString()
            gd.thangNam = spThangGD.selectedItem.toString()
            gd.ngay = edtNgayGD.text.toString().trim()

            DuLieuChung.suaGiaoDich(gd)
            locVaHienThiDanhSach()
            lamMoiForm()
            Toast.makeText(this, "Đã cập nhật giao dịch!", Toast.LENGTH_SHORT).show()
        }

        // 3. CHỨC NĂNG XÓA (Có hộp thoại xác nhận)
        btnXoaGD.setOnClickListener {
            val gd = giaoDichDangChon
            if (gd == null) {
                Toast.makeText(this, "Vui lòng chọn 1 giao dịch bên dưới để xóa!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa giao dịch '${gd.tenGiaoDich}' không?")
                .setPositiveButton("Xóa") { _, _ ->
                    DuLieuChung.xoaGiaoDich(gd.id)
                    locVaHienThiDanhSach()
                    lamMoiForm()
                    Toast.makeText(this, "Đã xóa giao dịch!", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Hủy", null)
                .show()
        }

        // 4. LÀM MỚI FORM
        btnLamMoiGD.setOnClickListener {
            lamMoiForm()
        }

        // 5. TÌM KIẾM THEO TỪ KHÓA
        edtTimKiemGD.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                locVaHienThiDanhSach()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 6. LỌC PHÂN LOẠI THEO SPINNER
        spLocLoaiGD.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                locVaHienThiDanhSach()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun kiemTraHopLe(): Boolean {
        if (!cbXacNhanGD.isChecked) {
            Toast.makeText(this, "Vui lòng tick 'Tính vào thống kê'!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (edtTenGD.text.toString().trim().isEmpty()) {
            edtTenGD.error = "Nhập tên giao dịch"
            edtTenGD.requestFocus()
            return false
        }
        val tien = edtSoTienGD.text.toString().trim().toLongOrNull() ?: 0L
        if (tien <= 0) {
            edtSoTienGD.error = "Số tiền phải lớn hơn 0"
            edtSoTienGD.requestFocus()
            return false
        }
        return true
    }

    private fun lamMoiForm() {
        giaoDichDangChon = null
        edtTenGD.text.clear()
        edtSoTienGD.text.clear()
        radChi.isChecked = true
        spDanhMucGD.setSelection(0)
        edtTenGD.requestFocus()
    }

    // Hàm kết hợp cả Tìm kiếm từ khóa + Phân loại Spinner
    private fun locVaHienThiDanhSach() {
        val tuKhoa = edtTimKiemGD.text.toString().trim().lowercase()
        val boLoc = spLocLoaiGD.selectedItem.toString()

        dsHienThi.clear()
        for (gd in DuLieuChung.danhSachGiaoDich) {
            val khopTuKhoa = gd.tenGiaoDich.lowercase().contains(tuKhoa) ||
                    gd.danhMuc.lowercase().contains(tuKhoa)

            val khopPhanLoai = when (boLoc) {
                "Tất cả" -> true
                "Khoản Chi" -> gd.loaiGiaoDich == "CHI"
                "Khoản Thu" -> gd.loaiGiaoDich == "THU"
                else -> gd.danhMuc == boLoc
            }

            if (khopTuKhoa && khopPhanLoai) {
                dsHienThi.add(gd)
            }
        }
        // Đảo ngược để giao dịch mới thêm hiện lên trên cùng
        dsHienThi.reverse()
        adapter.notifyDataSetChanged()
    }
}