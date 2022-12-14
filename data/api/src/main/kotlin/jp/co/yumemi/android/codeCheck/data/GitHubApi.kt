package jp.co.yumemi.android.codeCheck.data

import org.json.JSONObject

interface GitHubApi {
    suspend fun search(query: String): Result<JSONObject>
}