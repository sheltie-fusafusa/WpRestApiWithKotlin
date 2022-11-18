package service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import data.WPMediaResponse
import data.WPPost
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.Properties

class PostService {

    /**
     * 記事本文の投稿
     *
     * @args client httpクライアント
     * @args suthStr 認証用文字列
     * @return return 戻り値の説明
     */
    fun postContent(properties : Properties, client : OkHttpClient, authStr: String, media : WPMediaResponse){

        var content: String =
            """
                <!-- wp:heading {"level":2} -->
                <h2>ここに見出しが入ります</h2>
                <!-- /wp:heading -->
                <!-- wp:paragraph -->
                <p>ここに本文が入ります</p>
                <!-- /wp:paragraph -->
                <!-- wp:heading {"level":3} -->
                <h3>ここに見出しが入ります その2</h3>
                <!-- /wp:heading -->
                <!-- wp:paragraph -->
                <p>ここに本文が入ります その2</p>
                <!-- /wp:paragraph -->
            """.trimIndent()

        // 記事データの作成
        val post : WPPost = WPPost(
            "タイトル",
            content,
            "publish",
            1,
            media.id
        )

        // 記事データの投稿
        val json = Json.encodeToString(post)

        // 記事をポストする
        val mediaType : MediaType = "application/json; charset=utf-8".toMediaType()

        val body : RequestBody = json.toRequestBody(mediaType)
        val requestWP: Request = Request.Builder()
            .url(properties.getProperty("WORDPRESS_POST_URL"))
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Basic $authStr")
            .post(body)
            .build()
        val responseWP : Response = client.newCall(requestWP).execute()

        responseWP.close()
    }

    /**
     * アイキャッチ画像のアップロード
     *
     * @args client httpクライアント
     * @args suthStr 認証用文字列
     * @return return 戻り値の説明
     */
    fun uploadEyeCache(properties : Properties, client : OkHttpClient, authStr : String) : WPMediaResponse{

        // アイキャッチ画像をアップロードする
        val mediaTypeImg : MediaType = "image/png".toMediaType()
        val jsonImg : String = "{source_url : ./28_LGTM.png}"
        val file : File = File("./28_LGTM.png")

        val multipartBody : MultipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("Content-Type", "image/png")
            .addFormDataPart("file", "28_LGTM.png", file.asRequestBody(mediaTypeImg))
            .build()

        val requestEyeCatch: Request = Request.Builder()
            .url(properties.getProperty("WORDPRESS_MEDIA_POST_URL"))
            .addHeader("Content-Type", "multipart/form-data;")
            .addHeader("Content-Disposition", "attachment;filename=28_LGTM.png")
            .addHeader("Authorization", "Basic $authStr")
            .post(multipartBody)
            .build()
        val responseWP : Response = client.newCall(requestEyeCatch).execute()

        val mapper : ObjectMapper = jacksonObjectMapper()
        val body : String? = responseWP.body?.string()
        val responseJson : WPMediaResponse = mapper.readValue(body, WPMediaResponse::class.java)
        println("アイキャッチアップロードリザルト")
        println(responseJson.toString())

        responseWP.close()

        return responseJson
    }
}