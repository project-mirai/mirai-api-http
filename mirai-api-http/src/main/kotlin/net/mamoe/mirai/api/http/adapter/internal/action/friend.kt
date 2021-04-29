package net.mamoe.mirai.api.http.adapter.internal.action

import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.LongTargetDTO

internal suspend fun onDeleteFriend(deleteFriendDTO: LongTargetDTO): StateCode {
    deleteFriendDTO.session.bot.getFriend(deleteFriendDTO.target)
        ?.delete() ?: return StateCode.NoElement

    return StateCode.Success
}