package com.admin.project_kwork.models

import android.os.Parcelable
import java.io.Serializable

data class  Music(
    val musicId: String = "",
    val musicImageUrl: String = "",
    val titleMusic: String = "",
    val titleAuthor: String = "",
    //our music which(которую) we converts to uri
    val music: String
): Serializable {

}