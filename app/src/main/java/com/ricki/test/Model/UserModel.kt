package com.ricki.test.Model

import java.io.Serializable

data class UserModel(
    var total_count: Int,
    var incomplete_results: Boolean,
    var items: ArrayList<UserItemModel>
) : Serializable

data class UserItemModel(
    var login : String,
    var id : Int,
    var node_id : String,
    var avatar_url: String
) : Serializable