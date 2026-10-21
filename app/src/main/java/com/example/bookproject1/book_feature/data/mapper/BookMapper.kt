package com.example.bookproject1.book_feature.data.mapper

import com.example.bookproject1.book_feature.data.local.entity.BookEntity
import com.example.bookproject1.book_feature.data.remote.dto.BookTreeDto
import com.example.bookproject1.book_feature.data.remote.dto.SectionDto
import com.example.bookproject1.book_feature.data.util.ArabicNormalizer
import com.example.bookproject1.book_feature.domain.model.BookParagraph


fun BookTreeDto.toEntityList(partNumber:Int):List<BookEntity>{
    val entities = mutableListOf<BookEntity>()
    var globalIndex = 0

    fun processSections(
        sections: List<SectionDto>?,
        mainName: String,
        mainTitle: String? = null,
        fName: String? = null,
        fTitle: String? = null,
        mName: String? = null,
        mTitle: String? = null
    ) {
        var pendingSubheading: String? = null

        sections?.forEach { section ->
            val sub = section.subheading?.trim()?.takeIf { it.isNotEmpty() }
            val rawParagraphs = section.paragraphs.filter { it.isNotBlank() }

            if (rawParagraphs.isEmpty()) {
                if (sub != null) pendingSubheading = sub
            } else {
                val effectiveSubheading = pendingSubheading ?: sub

                val effectiveParagraphs = if (pendingSubheading != null && sub != null && sub != pendingSubheading) {
                    listOf(sub) + rawParagraphs
                } else {
                    rawParagraphs
                }
                pendingSubheading = null

                effectiveParagraphs.forEach { paragraphText ->
                    entities.add(
                        BookEntity(
                            partNumber = partNumber,
                            orderIndex = globalIndex++,
                            mainSectionName = mainName,
                            mainSectionTitle = mainTitle,
                            faslName = fName,
                            faslTitle = fTitle,
                            mabhathName = mName,
                            mabhathTitle = mTitle,
                            subheading = effectiveSubheading,
                            contentText = paragraphText,
                            searchText = ArabicNormalizer.normalize(paragraphText)
                        )
                    )
                }
            }
        }
    }
    this.introductions?.forEach { intro ->
        processSections(
            sections = intro.sections,
            mainName = intro.name
        )
    }

    this.babs?.forEach { bab ->
        processSections(
            sections = bab.sections,
            mainName = bab.name,
            mainTitle = bab.title
        )

        bab.fasls?.forEach { fasl ->
            processSections(
                sections = fasl.sections,
                mainName = bab.name, mainTitle = bab.title,
                fName = fasl.name, fTitle = fasl.title
            )
            fasl.mabhaths?.forEach { mabhath ->
                processSections(
                    sections = mabhath.sections,
                    mainName = bab.name, mainTitle = bab.title,
                    fName = fasl.name, fTitle = fasl.title,
                    mName = mabhath.name, mTitle = mabhath.title
                )
            }
        }
    }

    return entities
}
fun BookEntity.toDomainModel() = BookParagraph(
    id = id, partNumber = partNumber, orderIndex = orderIndex,
    mainSectionName = mainSectionName, mainSectionTitle = mainSectionTitle,
    faslName = faslName, faslTitle = faslTitle,
    mabhathName = mabhathName, mabhathTitle = mabhathTitle,
    subheading = subheading, contentText = contentText
)
