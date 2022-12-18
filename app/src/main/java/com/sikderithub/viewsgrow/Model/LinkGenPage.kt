package com.sikderithub.viewsgrow.Model

data class LinkGenPage(
    val domains: MutableList<Domain> = mutableListOf(),
    val buyDomain: MutableList<Domain> = mutableListOf(),
    val uniqueRef: LinkRef = LinkRef(),
    val channelName:String = "",
    val domainSuggestions : MutableList<Domain> = mutableListOf(),
    val subDomainSuggestions : MutableList<Domain> = mutableListOf(),
    val subdomainRes: SubdomainRes = SubdomainRes(),
    val domainPurchasePlan: DomainPlan? = null,
    val domainSubscriptionPlan: DomainPlan? = null,
    val subDomainPlan: DomainPlan? = null,
)