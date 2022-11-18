import data.WPMediaResponse
import data.WPPost
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import service.LoadConfig
import service.PostService
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit



fun main(){

    // プロパティファイルの読み込み
    val properties = LoadConfig().loadConfig()

    // 記事投稿サービス
    val postService = PostService()

    val auth : String = properties.getProperty("API_SECRET")
    val authStr = Base64.getEncoder().encodeToString(auth.toByteArray())

    // クライアント
    val client = OkHttpClient.Builder()
        .connectTimeout(30000, TimeUnit.MILLISECONDS)
        .readTimeout(30000, TimeUnit.MILLISECONDS)
        .build()

    // アイキャッチ画像をアップロード
    val wpMedia = postService.uploadEyeCache(properties, client, authStr)

    // 記事本文のアップロード
    postService.postContent(properties, client, authStr, wpMedia)
}

