/*
 * This file is part of the UNES Open Source Project.
 * UNES is licensed under the GNU GPLv3.
 *
 * Copyright (c) 2019. João Paulo Sena <joaopaulo761@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.forcetower.database.base.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import dev.forcetower.database.AbstractDao
import dev.forcetower.model.base.Message

@Dao
abstract class MessageDao : AbstractDao<Message>() {
    @Transaction
    open suspend fun prepare(messages: List<Message>) {
        for (message in messages) {
            val direct = getMessageByHash(message.hashMessage, message.credentialId)
            if (direct != null) {
                val senderName = message.senderName
                if (senderName != null && direct.senderName.isNullOrBlank()) {
                    updateSenderName(message.sagresId, senderName)
                }

                val discipline = message.discipline
                if (discipline != null && direct.discipline.isNullOrBlank()) {
                    updateDisciplineName(message.sagresId, discipline)
                }

                val attachmentLink = message.attachmentLink
                if (attachmentLink != null) {
                    updateAttachmentLink(message.sagresId, attachmentLink)
                }

                val attachmentName = message.attachmentName
                if (attachmentName != null) {
                    updateAttachmentName(message.sagresId, attachmentName)
                }

                if (message.html && direct.html) {
                    updateDateString(message.sagresId, message.dateString)
                }
            }
        }

        insertAllIgnoring(messages)
    }

    @Query("SELECT * FROM Message WHERE hash_message = :hash AND credential_id = :credentialId")
    protected abstract suspend fun getMessageByHash(hash: Long?, credentialId: Long): Message?

    @Query("UPDATE Message SET date_string = :dateString WHERE sagres_id = :sagresId")
    protected abstract suspend fun updateDateString(sagresId: Long, dateString: String?)

    @Query("UPDATE Message SET discipline = :discipline WHERE sagres_id = :sagresId")
    protected abstract suspend fun updateDisciplineName(sagresId: Long, discipline: String)

    @Query("UPDATE Message SET sender_name = :senderName WHERE sagres_id = :sagresId")
    protected abstract suspend fun updateSenderName(sagresId: Long, senderName: String)

    @Query("UPDATE Message SET attachmentLink = :attachmentLink WHERE sagres_id = :sagresId")
    protected abstract suspend fun updateAttachmentLink(sagresId: Long, attachmentLink: String)

    @Query("UPDATE Message SET attachmentName = :attachmentName WHERE sagres_id = :sagresId")
    protected abstract suspend fun updateAttachmentName(sagresId: Long, attachmentName: String)
}