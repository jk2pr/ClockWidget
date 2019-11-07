package com.jk.mr.duo.clock.data.caldata
import java.io.Serializable
import java.util.*


class CalData (

	val name : String,
	val topLevelDomain : List<String>,
	val alpha2Code : String,
	val alpha3Code : String,
	val callingCodes : List<String>,
	val capital : String,
	val altSpellings : List<String>,
	val region : String,
	val subregion : String,
	val population : String,
	val latlng : List<String>,
	val demonym : String,
	val area : String,
	val gini : String,
	val timezones : List<String>,

	//Added

	var address: String,
	var currentCityTimeZone: String,
    var abbreviation: String,
    var isSelected:Boolean=false,

	val borders : List<String>,
	val nativeName : String,
	val numericCode : String,
	val currencies : List<Currencies>,
	val languages : List<Languages>,
	val translations : Translations,
	val flag : String,
	val regionalBlocs : List<RegionalBlocs>,
	val cioc : String
):Serializable