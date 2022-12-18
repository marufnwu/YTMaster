package com.sikderithub.viewsgrow.repo.network

import android.util.Log
import com.google.gson.GsonBuilder
import com.sikderithub.viewsgrow.BuildConfig
import com.sikderithub.viewsgrow.Model.*
import com.sikderithub.viewsgrow.Model.Youtube.ChanelInfo
import com.sikderithub.viewsgrow.utils.LocalDB
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface MyApi {


    @GET("api/user.isUserRegistered.php")
    suspend fun isUserRegistered(
    ):Response<GenericResponse<Profile>>

    @GET("api/link.generateUniqueRef.php")
    suspend fun generateUniqueRef():Response<LinkRef>


    @GET("api/link.page_v2.php")
    suspend fun linkPage(
        @Query("link") link: String
    ):Response<LinkGenPageResponse>

    @FormUrlEncoded
    @POST("api/domain.addsubdomain.php")
    suspend fun addSubdomain(
        @Field("name") name :String
    ):Response<GenericResponse<Subdomain>>


    @FormUrlEncoded
    @POST("api/user.login.php")
    suspend fun login(
        @Field("googleToken") googleToken :String
    ):Response<Login>


    @FormUrlEncoded
    @POST("api/user.signup.php")
    suspend fun signup(
        @Field("fullname") fullname: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("city") city: String,
        @Field("state") state: String,
        @Field("country") country: String,
        @Field("youtubeChanelLink") youtubeChanelLink: String,
        @Field("profilePic") profilePic: String,
        @Field("googleId") googleId: String?,
    ):Response<Login>

    @FormUrlEncoded
    @POST("api/link.addnew.php")
    suspend fun addNewLink(
        @Field("domain") domain :String,
        @Field("subdomain") subdomain :String,
        @Field("suffix") suffix :String,
        @Field("oLink") oLink :String,
        @Field("nLink") nLink :String,
        @Field("intent") intent :String,
        @Field("type") type :String,
        @Field("metaTitle") metaTitle :String,
        @Field("metaDesc") metaDesc :String,
        @Field("metaImg") metaImg :String,
    ):Response<GenericResponse<String>>

    @GET("api/link.suffixCheck.php")
    fun suffixCheck(
        @Query("suffix") suffix: String,
        @Query("domain") domain: String?,
        @Query("subdomain") subdomain: String?,
    ):Call<GenericResponse<Boolean>>


    @Multipart
    @POST("api/link.uploadThumb.php")
    suspend fun uploadThumb(
        @Part thumb: MultipartBody.Part,
    ): Response<GenericResponse<String>>


    @GET("api/link.get.php")
    suspend fun getLink(
        @Query("currPage") currPage: Int = 1,
        @Query("totalPage") totalPage: Int = 0,
        @Query("unique") unique: Int = 0,
    ):Response<GenericResponse<Paging<Link>>>


    @GET("api/link.getAllUser.php")
    suspend fun getAllUserLink(
        @Query("currPage") currPage: Int = 1,
        @Query("totalPage") totalPage: Int = 0,
        @Query("unique") unique: Int = 0,
    ):Response<GenericResponse<Paging<Link>>>


    @GET("api/chanelinfo.php")
    suspend fun chanelinfo(
        @Query("channel") channel: String,
    ):Response<GenericResponse<ChanelInfo>>


    @GET("api/user.highLighted.php")
    suspend fun highLightedUser(
    ):Response<GenericResponse<MutableList<Profile>>>


    @GET("api/plan.createNewDomainPage.php")
    suspend fun createNewDomainPage(
        @Query("type") domainType: String
    ):Response<GenericResponse<CreateNewDomainPage>>


    @GET("api/plan.createNewSubDomainPage.php")
    suspend fun createNewSubDomainPage(
    ):Response<GenericResponse<CreateNewDomainPage>>


    @GET("api/user.getHighlightedChannel.php")
    suspend fun getHighlightedChannel(
        @Query("currPage") currPage: Int = 1,
        @Query("totalPage") totalPage: Int = 0,
    ):Response<GenericResponse<Paging<HighLightedChannel>>>

    @GET("api/user.accessTokenAuth.php")
    suspend fun accessTokenAuth(
    ):Response<Login>

    @GET("api/user.updateFcmToken.php")
    suspend fun updateFcmToken(
        @Query("token") token : String?
    ):Response<Int>

    @GET("api/user.updateChannel.php")
    suspend fun updateChannel(
        @Query("channel") token : String?
    ):Response<MyResponse>


    @FormUrlEncoded
    @POST("api/plan.createDomainRef.php")
    suspend fun createDomainRef(
        @Field("channelName") channelName: String,
        @Field("domainType") domainType: String,
    ):Response<GenericResponse<Transaction>>

    @FormUrlEncoded
    @POST("api/plan.createSubDomainRef.php")
    suspend fun createSubDomainRef(
        @Field("channelName") channelName: String,
    ):Response<GenericResponse<Transaction>>


    @GET("api/domain.getCustomDomainSuggetion.php")
    suspend fun getCustomDomainSuggetions(
        @Query("videoId") videoId:String
    ):Response<GenericResponse<List<String>>>

    @GET("api/page.home.php")
    suspend fun homeData(
    ):Response<GenericResponse<HomePage>>






    companion object {

        @Volatile
        private var myApiInstance: MyApi? = null
        private val LOCK = Any()

        operator fun invoke() = myApiInstance ?: synchronized(LOCK) {
            myApiInstance ?: createClient().also {
                myApiInstance = it
            }
        }


        private fun createClient(): MyApi {

            val interceptor = run {
                val httpLoggingInterceptor = HttpLoggingInterceptor()
                httpLoggingInterceptor.apply {
                    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                }
            }

            val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                .readTimeout(2, TimeUnit.MINUTES)
                .connectTimeout(2, TimeUnit.MINUTES)
                .callTimeout(2, TimeUnit.MINUTES)
                .addInterceptor(interceptor)
                .addInterceptor (TokenInterceptor())
                .addInterceptor { chain ->
                    try {
                        val request = chain.request()
                        val response = chain.proceed(request)

                        response
                    }catch (e :Exception){
                        e.message?.let { Log.d("OkHttpError", it) }
                        chain.proceed(chain.request())
                    }
                }
                .addInterceptor { chain ->
                    val original: Request = chain.request()
                    val requestBuilder: Request.Builder = original.newBuilder()
                        .addHeader("Apikey", BuildConfig.API_KEY)
                        .method(original.method, original.body)
                    val request: Request = requestBuilder.build()
                    chain.proceed(request)
                }
                .build()

            val gsonBuilder = GsonBuilder()
            gsonBuilder.setLenient()
            val gson = gsonBuilder.create()

            return Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build()
                .create(MyApi::class.java)
        }


    }

    class TokenInterceptor: Interceptor{
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val token = if (LocalDB.getAccessToken()!=null) LocalDB.getAccessToken() else ""
            val userId = if (LocalDB.getUserId()!=null) LocalDB.getUserId() else ""


            return if(!token.isNullOrEmpty()){
               chain.proceed(chain.request()
                    .newBuilder()
                    .header("Authorization","AccessToken $token")
                    .header("Userid","$userId")
                    .build())


            }else{
                chain.proceed(chain.request())
            }
        }

    }
}