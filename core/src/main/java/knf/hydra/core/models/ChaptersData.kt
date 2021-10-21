package knf.hydra.core.models

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

sealed class ChaptersData {
    data class Single(val model: ChapterModel): ChaptersData()
    data class Multiple(val chaptersPaging: Flow<PagingData<ChapterModel>>): ChaptersData()
}