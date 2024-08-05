package com.hoppers.duoclock.search.repositories

import com.hoppers.duoclock.network.IApi

class SearchRepository(private val api: IApi) {
    suspend fun doSearch(query: String) = api.getSearch(query = query)
}
