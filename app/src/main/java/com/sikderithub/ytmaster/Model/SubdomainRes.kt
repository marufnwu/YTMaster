package com.sikderithub.ytmaster.Model

data class SubdomainRes (
    val error: Boolean = true,
    val msg: String = "",
    val subdomains: MutableList<Subdomain> = mutableListOf()
)