package com.sikderithub.ytmaster.Model

data class LinkGenPageResponse(
    val data: LinkGenPage = LinkGenPage(),
    val error: Boolean = false,
    val msg: String = ""
)