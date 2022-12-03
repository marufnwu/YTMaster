package com.sikderithub.viewsgrow.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import com.sikderithub.viewsgrow.Model.Login
import com.sikderithub.viewsgrow.Model.Profile
import com.sikderithub.viewsgrow.R
import com.sikderithub.viewsgrow.ui.all_link.AllLinkActivity
import com.sikderithub.viewsgrow.ui.login.LoginActivity
import com.sikderithub.viewsgrow.ui.main.MainActivity
import com.sikderithub.viewsgrow.ui.splash.SplashActivity
import com.sikderithub.viewsgrow.ui.userdetails.UserDetailsActivity
import com.sikderithub.viewsgrow.ui.webview.GenericWebViewActivity
import com.sikderithub.viewsgrow.utils.MyExtensions.shortToast
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


object CommonMethod {

    var ACTIVITY_CREATED_BY_NOTI: Boolean = false
    private const val YOUTUBE_REGEX = "((?:https?:)?\\/\\/)?((?:www|m)\\.)?((?:youtube\\.com|youtu.be))(\\/(?:[\\w\\-]+\\?v=|embed\\/|v\\/)?)([\\w\\-]+)(\\S+)?"
    private const val INSTAGRAM_REGEX = "(?:(?:http|https):\\/\\/)?(?:www.)?(?:instagram.com|instagr.am|instagr.com)\\/(.+)?"
    private const val FACEBOOK_REGEX = "(?:https?://)?(?:www.)?(mbasic.facebook|m.facebook|facebook|fb).(com|me)/?(?:(?:\\w\\.)*#!/)?(?:pages/)?(?:[\\w\\-.]*/)*([\\w-.]*)"


    var profile: Profile? =null

    private val linkMap = mapOf(
        LinkType.FACEBOOK to FACEBOOK_REGEX,
        LinkType.YOUTUBE to YOUTUBE_REGEX,
        LinkType.INSTAGRAM to INSTAGRAM_REGEX,
    )


    fun getYtVideoIdFromLink(ytUrl: String): String? {
        var videoId: String? = null
        val regex =
            "^((?:https?:)?//)?((?:www|m)\\.)?((?:youtube\\.com|youtu.be|youtube-nocookie.com))(/(?:[\\w\\-]+\\?v=|feature=|watch\\?|e/|embed/|v/)?)([\\w\\-]+)(\\S+)?\$"
        val pattern: Pattern = Pattern.compile(
            regex ,
            Pattern.CASE_INSENSITIVE
        )
        val matcher: Matcher = pattern.matcher(ytUrl)
        if (matcher.matches()) {
            videoId = matcher.group(5)
        }
        return videoId
    }


    fun isValidYtLink(ytUrl: String): Boolean {
        val regex =
            "^((?:https?:)?//)?((?:www|m)\\.)?((?:youtube\\.com|youtu.be|youtube-nocookie.com))(/(?:[\\w\\-]+\\?v=|feature=|watch\\?|e/|embed/|v/)?)([\\w\\-]+)(\\S+)?\$"
        val pattern: Pattern = Pattern.compile(
            regex ,
            Pattern.CASE_INSENSITIVE
        )

        val matcher: Matcher = pattern.matcher(ytUrl)
        return matcher.matches()

    }
    fun isValidLink(link: String): Boolean {
        val regex =
            "^[-a-zA-Z0-9@:%._/\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)\$"
        val pattern: Pattern = Pattern.compile(
            regex ,
            Pattern.CASE_INSENSITIVE
        )

        val matcher: Matcher = pattern.matcher(link)
        return matcher.matches()
    }

    fun isLinkPermitted(link: String) : Boolean{
        for (map in linkMap){
            val pattern: Pattern = Pattern.compile(
                map.value ,
                Pattern.CASE_INSENSITIVE
            )

            println(map.key)

            val matcher: Matcher = pattern.matcher(link)

            if(matcher.matches()){
                return true
            }
        }

        return false
    }

    fun getLinkPlatform(link: String) : LinkType?{
        for (map in linkMap){
            val pattern: Pattern = Pattern.compile(
                map.value ,
                Pattern.CASE_INSENSITIVE
            )

            println(map.key)

            val matcher: Matcher = pattern.matcher(link)

            if(matcher.matches()){
                return map.key
            }
        }

        return null
    }

    fun isYoutubeChanelLinkValid(link :String):String?{
        val chRegex = "[https?://?(www.)]+?youtube.com/(channel|c|user)+?/([\\w-]+)?+"
        val pattern: Pattern = Pattern.compile(
            chRegex ,
            Pattern.CASE_INSENSITIVE
        )

        val matcher: Matcher = pattern.matcher(link)
        return if (matcher.matches()){
            return matcher.group(1)+"/"+matcher.group(2)
        }else{
            null
        }
    }

    fun getIntentLink(link: String): String? {
        val reg = "(.+?)(.com|.be)/(.+)"

        val pattern: Pattern = Pattern.compile(
            reg ,
            Pattern.CASE_INSENSITIVE
        )

        val matcher: Matcher = pattern.matcher(link)
        return if (matcher.matches()){
            matcher.group(3);
        }else{
            null
        }
    }

    fun valueToUnit(value: Long): String {

        val suffixes: NavigableMap<Long, String> = TreeMap()

        suffixes[1_000L] = "k"
        suffixes[1_000_000L] = "M"
        suffixes[1_000_000_000L] = "B"
        suffixes[1_000_000_000_000L] = "T"
        suffixes[1_000_000_000_000_000L] = "P"
        suffixes[1_000_000_000_000_000_000L] = "E"


        if (value == Long.MIN_VALUE) return valueToUnit(Long.MIN_VALUE + 1)
        if (value < 0) return "-" + valueToUnit(-value)
        if (value < 1000) return java.lang.Long.toString(value) //deal with easy case
        val (divideBy, suffix) = suffixes.floorEntry(value)
        val truncated = value / (divideBy / 10) //the number part of the output times 10
        val hasDecimal = truncated < 100 && truncated / 10.0 != (truncated / 10).toDouble()
        return if (hasDecimal) (truncated / 10.0).toString() + suffix else (truncated / 10).toString() + suffix
    }

    fun openLink(link:String, context:Context){

        val linkHost = Uri.parse(link).host
        val uri = Uri.parse(link)
        if (linkHost == null) {
            return
        }

        try {

            if(linkHost.equals("www.youtube.com") || linkHost.equals("youtube.com") || linkHost.equals("youtu.be") || linkHost.equals("youtu.be")){
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.setPackage("com.google.android.youtube")
                context.startActivity(intent)
            }else if (link != null && (link.startsWith("http://") || link.startsWith("https://"))) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }


        }catch (e : Exception){
           context.shortToast("Can't open")
        }
    }

    fun getScreenWidth(): Int {
        return Resources.getSystem().getDisplayMetrics().widthPixels
    }

    fun getScreenHeight(): Int {
        return Resources.getSystem().getDisplayMetrics().heightPixels
    }

    fun openChannelLinkById(context: Context, it: String) {
        val channelLink = "https://youtube.com/channel/${it}"
        openLink(channelLink, context)
    }

    fun loginResponse(login: Login?, context: Context) {
        login?.let {

            if(login.state<=1){
                //account ok
                if(login.account){
                    //account exits

                    login.profile?.let {
                        LocalDB.saveAccessTokenAndUserId(login.token, login.profile.id!!)
                        LocalDB.saveProfile(it)

                        if(context is SplashActivity){
                            context.startActivity(Intent(context, MainActivity::class.java))
                        }

                        (context as Activity).finish()
                    }


                }else{
                    //account not exits. open sign up activity
                    if(context is SplashActivity){
                        MyApp.logout()
                        context.startActivity(Intent(context, MainActivity::class.java))
                        (context as Activity).finish()

                        return
                    }else{
                        context.startActivity(Intent(context, UserDetailsActivity::class.java))
                        (context as Activity).finish()
                    }


                }
            }else{
                //something wrong with account
                GenericDialog.make(context)
                    .setBodyText(login.msg)
                    .setCancelable(false)
                    .setIconType(GenericDialog.IconType.ERROR)
                    .setShowNegativeButton(false)
                    .setShowPositiveButton(true)
                    .setPositiveButtonText("Exit")
                    .setOnGenericDialogListener(object : GenericDialog.OnGenericDialogListener {
                        override fun onPositiveButtonClick(dialog: GenericDialog?) {

                        }

                        override fun onNegativeButtonClick(dialog: GenericDialog?) {

                        }

                        override fun onToast(message: String?) {

                        }

                    })
                    .build()
                    .showDialog()
            }


        }

    }

    fun openPrivacyPolicy(context: Context){
        val intent = Intent(context, GenericWebViewActivity::class.java)
        intent.putExtra(Constant.ACTIVITY_NAME, context.resources.getString(R.string.privacy_title))
        intent.putExtra(Constant.WEB_URL, context.resources.getString(R.string.privacy_url))

        context.startActivity(intent)
    }

    fun openReturnPolicy(context: Context){
        val intent = Intent(context, GenericWebViewActivity::class.java)
        intent.putExtra(Constant.ACTIVITY_NAME, context.resources.getString(R.string.return_title))
        intent.putExtra(Constant.WEB_URL, context.resources.getString(R.string.return_policy_url))

        context.startActivity(intent)
    }
    fun openWebView(context: Context, link: String, title:String){
        val intent = Intent(context, GenericWebViewActivity::class.java)
        intent.putExtra(Constant.ACTIVITY_NAME, title)
        intent.putExtra(Constant.WEB_URL, link)

        context.startActivity(intent)
    }

    fun openAllLinkActivity(context: Context){
        val intent = Intent(context, AllLinkActivity::class.java)

        context.startActivity(intent)
    }

    fun showLoginDialog(context: Context, bodyText:String="Are you want to create custom premium domain?") {
        GenericDialog.make(context)
            .setCancelable(true)
            .setBodyText(bodyText)
            .setIconType(GenericDialog.IconType.WARNING)
            .setPositiveButton("Login Now") {
                it?.hideDialog()
                context.startActivity(Intent(context, LoginActivity::class.java))
            }.setNegativeButton("Cancel") {
                it?.hideDialog()
            }
            .build()
            .showDialog()
    }

}


public enum class LinkType{
    YOUTUBE,
    FACEBOOK,
    INSTAGRAM,
    SPOTIFY,
}