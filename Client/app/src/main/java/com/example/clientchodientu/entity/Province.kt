package com.example.clientchodientu.entity

import com.google.gson.annotations.SerializedName

data class Province(
    val name: String,
    val code: Int,

    // SỬA Ở ĐÂY: Key trong JSON là "wards" nên phải mapping đúng tên
    @SerializedName("wards")
    val ward: List<Ward> = listOf()
) {
    override fun toString(): String {
        return name
    }
}