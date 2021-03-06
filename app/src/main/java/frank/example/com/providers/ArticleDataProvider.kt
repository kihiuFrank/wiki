package frank.example.com.providers

import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.google.gson.Gson
import frank.example.com.Models.Urls
import frank.example.com.Models.WikiResult
import java.io.Reader
import java.lang.Exception

class ArticleDataProvider {

    init {
        FuelManager.instance.baseHeaders = mapOf("User-Agent" to "Frank Wikipedia")
    }

    fun search (term: String, skip: Int, take: Int, responseHandler: (result:WikiResult) -> Unit?){
        Urls.getSearchUrl(term,skip,take).httpGet()
            .responseObject(WikiDataDeserializer()){_, response, result ->

                if (response.statusCode != 200) {
                    throw Exception("Unable to get articles")
                }

                val (data, _) = result
                responseHandler.invoke(data as @kotlin.ParameterName(name = "result") WikiResult)
        }
    }

    fun  getRandom(take: Int,responseHandler: (result: WikiResult) -> Unit?) {
        Urls.getRandomUrl(take).httpGet()
            .responseObject(WikiDataDeserializer()) { _, response, result ->

                if (response.statusCode != 200) {
                    throw Exception("Unable to get articles")
                }
                val (data, _) = result
                responseHandler.invoke(data as @kotlin.ParameterName(name = "result") WikiResult)
            }
    }

    class WikiDataDeserializer : ResponseDeserializable<WikiResult> {
        override fun deserialize(reader: Reader): WikiResult? = Gson().fromJson(reader, WikiResult::class.java)
    }
}