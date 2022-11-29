package com.sikderithub.viewsgrow.Model

data class LinkGenPage(
    val domains: List<Domain> = listOf(),
    val uniqueRef: LinkRef = LinkRef(),
    val channelName:String = "",
    val domainSuggestions : List<String> = listOf(),
    val subDomainSuggestions : List<String> = listOf(),
    val subdomainRes: SubdomainRes = SubdomainRes()
)