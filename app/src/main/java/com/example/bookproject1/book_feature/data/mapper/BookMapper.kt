package com.example.bookproject1.book_feature.data.mapper

import com.example.bookproject1.book_feature.data.local.entity.BookEntity
import com.example.bookproject1.book_feature.data.local.entity.TocItemTuple
import com.example.bookproject1.book_feature.data.remote.dto.BookTreeDto
import com.example.bookproject1.book_feature.data.remote.dto.SectionDto
import com.example.bookproject1.book_feature.data.util.ArabicNormalizer
import com.example.bookproject1.book_feature.domain.model.BookParagraph
import com.example.bookproject1.book_feature.domain.model.TocItem


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
    ){
        sections?.forEach { section ->
            // إذا اكو عنوان فرعي (مثل "نسبه:") ناخذه، وإذا ماكو نخليه null
            val sub = if (section.subheading.isNullOrBlank()) null else section.subheading

            section.paragraphs.forEach { paragraphText ->
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
                        subheading = sub,
                        contentText = paragraphText,
                        searchText = ArabicNormalizer.normalize(paragraphText)
                    )
                )
            }
        }
    }
    this.introductions?.forEach { intro ->
        processSections(
            sections = intro.sections,
            mainName = intro.name
        )
    }

    // 2. استخراج الأبواب وما بداخلها
    this.babs?.forEach { bab ->
        // نصوص الباب المباشرة (التي تأتي قبل الفصول إن وجدت)
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
fun BookEntity.toDomainModel(): BookParagraph {
    // ندمج العناوين (مثال: الباب الأول - الفصل الثاني)
    val sectionParts = listOfNotNull(mainSectionName, faslName, mabhathName)
    val fullSectionTitle = sectionParts.joinToString(" - ")

    return BookParagraph(
        id = this.id,
        partNumber = this.partNumber,
        sectionTitle = fullSectionTitle,
        subheading = this.subheading,
        contentText = this.contentText
    )
}
// تحويل كائن الفهرس
fun TocItemTuple.toDomainModel(): TocItem {
    return TocItem(
        mainSectionName = this.mainSectionName,
        mainSectionTitle = this.mainSectionTitle,
        faslName = this.faslName,
        faslTitle = this.faslTitle,
        mabhathName = this.mabhathName,
        mabhathTitle = this.mabhathTitle
    )
}