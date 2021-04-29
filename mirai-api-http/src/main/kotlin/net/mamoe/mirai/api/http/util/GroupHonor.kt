package net.mamoe.mirai.api.http.util

import net.mamoe.mirai.data.GroupHonorType
import net.mamoe.mirai.utils.MiraiExperimentalApi

class GroupHonor {
    companion object {
        private val value = mapOf(
            1 to "龙王",
            2 to "群聊之火",
            3 to "群聊炽焰",
            5 to "冒尖小春笋",
            6 to "快乐源泉",
            7 to "活跃头衔",
            8 to "特殊头衔",
            9 to "管理头衔",
        )

        @OptIn(MiraiExperimentalApi::class)
        operator fun get(honor: GroupHonorType): String {
            return value[honor.value] ?: "未知群荣誉"
        }
    }
}
