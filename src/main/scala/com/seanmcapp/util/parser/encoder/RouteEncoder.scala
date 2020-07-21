package com.seanmcapp.util.parser.encoder

import com.seanmcapp.util.parser.{BroadcasterCommon, WalletCommon}

trait RouteEncoder extends TelegramOutputEncoder with DotaOutputEncoder with WalletCommon with BroadcasterCommon
