package com.sikderithub.ytmaster.Model

data class LinkGenPage(
    val domains: List<Domain> = listOf(),
    val uniqueRef: LinkRef = LinkRef(),
    val subdomainRes: SubdomainRes = SubdomainRes()
)