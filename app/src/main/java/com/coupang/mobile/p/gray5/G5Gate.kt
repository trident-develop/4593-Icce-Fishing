package com.coupang.mobile.p.gray5

internal fun evaluate(isStub: Boolean, adGate: Boolean, hasNet: Boolean): Int =
    (if (isStub)  GATE_STUB else 0) or
    (if (adGate)  GATE_AD   else 0) or
    (if (!hasNet) GATE_NET  else 0)

internal fun Int.routeKey(): String? = when {
    this and (GATE_STUB or GATE_AD) != 0 -> ROUTE_STUB
    this and GATE_NET != 0               -> ROUTE_NET
    else                                 -> null
}
