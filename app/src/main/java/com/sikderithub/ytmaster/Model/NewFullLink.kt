package com.sikderithub.ytmaster.Model

import com.sikderithub.ytmaster.utils.CommonMethod
import com.sikderithub.ytmaster.utils.LinkType

data class NewFullLink(var domain: String?, var subdomain: String?, var suffix : String?, var type: LinkType?, ){

    override fun toString(): String {
        return if(subdomain==null){
            "https://${domain}/${suffix}"
        }else{
            "http://${subdomain}.${domain}/${suffix}"
        }
    }


}

