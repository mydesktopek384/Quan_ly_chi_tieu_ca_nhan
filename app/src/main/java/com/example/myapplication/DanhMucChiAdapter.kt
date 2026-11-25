package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ProgressBar
import android.widget.TextView
import java.text.DecimalFormat

class DanhMucChiAdapter(
    private val context: Context,
    private val listDanhMuc: List<DanhMucChiModel>
) : BaseAdapter() {

    override fun getCount(): Int = listDanhMuc.size

    override fun getItem(position: Int): Any = listDanhMuc[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_danh_muc_chi, parent, false)

        val tvTen = view.findViewById<TextView>(R.id.tvItemTenDanhMuc)
        val tvTienPhanTram = view.findViewById<TextView>(R.id.tvItemTienVaPhanTram)
        val pbDanhMuc = view.findViewById<ProgressBar>(R.id.pbItemDanhMuc)

        val item = listDanhMuc[position]
        val formatter = DecimalFormat("#,###")
        val tienFormatted = formatter.format(item.soTien).replace(",", ".")

        tvTen.text = item.tenDanhMuc
        tvTienPhanTram.text = "$tienFormatted đ · ${item.phanTram}%"
        pbDanhMuc.progress = item.phanTram

        return view
    }
}