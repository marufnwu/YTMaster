package com.sikderithub.viewsgrow.Model

import com.sikderithub.viewsgrow.utils.LinkType

data class NewFullLink(var domain: String?, var subdomain: String?, var suffix : String?, var type: LinkType?, ){

    override fun toString(): String {
        return if(subdomain==null){
            "https://${domain}/${suffix}"
        }else{
            "http://${subdomain}.${domain}/${suffix}"
        }
    }


}

