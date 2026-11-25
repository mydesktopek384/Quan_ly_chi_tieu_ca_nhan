package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.text.DecimalFormat

class GiaoDichAdapter(
    private val context: Context,
    private val listGiaoDich: List<GiaoDichModel>
) : BaseAdapter() {

    override fun getCount(): Int = listGiaoDich.size

    override fun getItem(position: Int): Any = listGiaoDich[position]

    override fun getItemId(position: Int): Long = listGiaoDich[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_giao_dich, parent, false)

        val tvTen = view.findViewById<TextView>(R.id.tvItemTenGD)
        val tvChiTiet = view.findViewById<TextView>(R.id.tvItemChiTietGD)
        val tvSoTien = view.findViewById<TextView>(R.id.tvItemSoTienGD)

        val gd = listGiaoDich[position]
        val formatter = DecimalFormat("#,###")
        val tienFormatted = formatter.format(gd.soTien).replace(",", ".")

        tvTen.text = gd.tenGiaoDich
        tvChiTiet.text = "${gd.danhMuc} · ${gd.ngay} · ${gd.thangNam}"

        if (gd.loaiGiaoDich == "THU") {
            tvSoTien.text = "+$tienFormatted đ"
            tvSoTien.setTextColor(0xFF16A34A.toInt()) // Màu xanh lá
        } else {
            tvSoTien.text = "-$tienFormatted đ"
            tvSoTien.setTextColor(0xFFEF4444.toInt()) // Màu đỏ
        }

        return view
    }
}