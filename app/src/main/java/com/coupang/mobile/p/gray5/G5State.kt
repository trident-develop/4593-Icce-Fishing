package com.coupang.mobile.p.gray5

internal sealed class G5State {
    object Boot       : G5State()
    object Restoring  : G5State()
    object Collecting : G5State()
    data class Resolved(val url: String) : G5State()
    data class Dead(val route: String)   : G5State()
}
