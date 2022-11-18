package com.sikderithub.viewsgrow.Model

data class LinkGenPageResponse(
    val data: LinkGenPage = LinkGenPage(),
    val error: Boolean = false,
    val msg: String = ""
)