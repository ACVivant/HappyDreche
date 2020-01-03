package com.vivant.annecharlotte.happydreche.firestore

/**
 * Created by Anne-Charlotte Vivant on 03/01/2020.
 */
class MarkersUrl {
    private var centerInfos: String? = null

    fun createUrl(
        number: String,
        street: String,
        zipcode: String,
        town: String,
        country: String,
        myKey: String
    ): String {
        val base = "https://maps.googleapis.com/maps/api/staticmap?"
        val mapType = "&maptype="
        val mapTypeInfos = "roadmap"
        val marker = "&markers="
        val markerSize = "size:"
        val markerSizeInfos = "mid|"
        val markerColor = "color:"
        val markerColorInfos = "red|"
        val size = "&size="
        val sizeInfos = "300x300"
        val language = "&language="
        val lanquageInfos = "french"
        val key = "&key="
        val keyInfos: String

        keyInfos = myKey
        createAddressUrlField(number, street, zipcode, town, country)

        return base + size + sizeInfos + mapType + mapTypeInfos + marker + markerSize + markerSizeInfos + markerColor + markerColorInfos + centerInfos + language + lanquageInfos + key + keyInfos
    }

    private fun createAddressUrlField(number: String, street: String, zipcode: String, town: String, country: String) {
        centerInfos =
            number + "," + extractSmallNameOfStreet(formatAddressFieldsForMap(street)) + ','.toString() + zipcode + ','.toString() + formatAddressFieldsForMap(
                town
            ) + ','.toString() + formatAddressFieldsForMap(country)
    }

    private fun formatAddressFieldsForMap(input: String): String {
        var output = input.replace("-", "&")
        output = output.replace(" ", "&")
        return output
    }

    private fun extractSmallNameOfStreet(input: String): String {
        var input = input
        // We control that the las character is not &
        val inputLenght = input.length
        if (input[inputLenght - 1] == '&') {
            input = input.substring(0, inputLenght - 1)
        }

        val index = input.lastIndexOf("&")
        return input.substring(index + 1)
    }


    fun createGeocoderUrl(number: String, street: String, zipcode: String, town: String, country: String): String {
        return "$number+$street,+$zipcode+$town,+$country"
    }
}
